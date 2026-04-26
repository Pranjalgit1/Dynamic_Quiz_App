package quiz.manager;

import org.json.JSONArray;
import org.json.JSONObject;
import quiz.model.Question;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionApiClient {
    private static final String OPEN_TDB_API_URL = "https://opentdb.com/api.php?amount=%d&category=18&type=multiple";
    private static final String TRIVIA_API_URL = "https://the-trivia-api.com/v2/questions?limit=%d";
    private static final int OPEN_TDB_BATCH_SIZE = 50;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static List<Question> fetchQuestions(int amount) throws Exception {
        try {
            return fetchQuestionsFromOpenTdb(amount);
        } catch (Exception openTdbError) {
            System.out.println("OpenTDB unavailable/rate-limited. Switching to fallback provider...");
            return fetchQuestionsFromTriviaApi(amount);
        }
    }

    private static List<Question> fetchQuestionsFromOpenTdb(int amount) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(OPEN_TDB_API_URL, amount)))
                .GET()
                .build();

        HttpResponse<String> response = null;
        int maxRetries = 3;
        int currentAttempt = 0;

        while (currentAttempt < maxRetries) {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 429) {
                System.out.println("Trivia API rate limit hit (HTTP 429). Waiting 5 seconds to retry... (" + (currentAttempt + 1) + "/" + maxRetries + ")");
                Thread.sleep(5000); // Wait 5 seconds to clear OpenTDB limits
                currentAttempt++;
            } else if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch questions: HTTP " + response.statusCode());
            } else {
                break; // Found HTTP 200 OK, break out of loop
            }
        }

        if (response == null || response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch questions after " + maxRetries + " retries due to rate limits.");
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        int responseCode = jsonResponse.getInt("response_code");
        if (responseCode != 0) {
            throw new RuntimeException("API returned non-zero response code: " + responseCode);
        }

        JSONArray results = jsonResponse.getJSONArray("results");
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            
            String category = item.getString("category");
            String difficulty = item.getString("difficulty");
            String questionText = unescapeHtml(item.getString("question"));
            String correctAnswer = unescapeHtml(item.getString("correct_answer"));
            JSONArray incorrectAnswersJson = item.getJSONArray("incorrect_answers");

            List<String> optionsList = new ArrayList<>();
            optionsList.add(correctAnswer);
            for (int j = 0; j < incorrectAnswersJson.length(); j++) {
                optionsList.add(unescapeHtml(incorrectAnswersJson.getString(j)));
            }

            // Shuffle options
            Collections.shuffle(optionsList);
            int correctIdx = optionsList.indexOf(correctAnswer);

            String[] optionsArray = optionsList.toArray(new String[0]);

            // Constructing Question
            Question q = new Question(
                i + 1, // ID
                questionText,
                optionsArray,
                correctIdx,
                difficulty,
                category,
                "Correct Answer: " + correctAnswer
            );

            questions.add(q);
        }

        return questions;
    }

    private static List<Question> fetchQuestionsFromTriviaApi(int amount) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(TRIVIA_API_URL, amount)))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Fallback provider failed: HTTP " + response.statusCode());
        }

        JSONArray results = new JSONArray(response.body());
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);

            String category = item.optString("category", "General");
            String difficulty = item.optString("difficulty", "medium");
            String questionText = unescapeHtml(extractTriviaApiQuestionText(item));
            String correctAnswer = unescapeHtml(item.optString("correctAnswer", ""));
            JSONArray incorrectAnswersJson = item.optJSONArray("incorrectAnswers");

            if (questionText == null || questionText.isBlank() || correctAnswer.isBlank() || incorrectAnswersJson == null || incorrectAnswersJson.length() < 3) {
                continue;
            }

            List<String> optionsList = new ArrayList<>();
            optionsList.add(correctAnswer);
            for (int j = 0; j < incorrectAnswersJson.length(); j++) {
                optionsList.add(unescapeHtml(incorrectAnswersJson.getString(j)));
            }

            Collections.shuffle(optionsList);
            int correctIdx = optionsList.indexOf(correctAnswer);
            if (correctIdx < 0) {
                continue;
            }

            String[] optionsArray = optionsList.toArray(new String[0]);

            Question q = new Question(
                    i + 1,
                    questionText,
                    optionsArray,
                    correctIdx,
                    difficulty,
                    category,
                    "Correct Answer: " + correctAnswer);

            questions.add(q);
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("Fallback provider returned no usable questions.");
        }

        return questions;
    }

    private static String extractTriviaApiQuestionText(JSONObject item) {
        Object qObj = item.opt("question");
        if (qObj instanceof JSONObject) {
            return ((JSONObject) qObj).optString("text", "");
        }
        if (qObj instanceof String) {
            return (String) qObj;
        }
        return "";
    }

    public static int importQuestionsForTopic(String topic, int minNewQuestions) throws Exception {
        if (topic == null || topic.isBlank() || "All Topics".equalsIgnoreCase(topic) || minNewQuestions <= 0) {
            return 0;
        }

        int added = 0;
        int attempts = 0;
        int maxAttempts = 8;

        while (added < minNewQuestions && attempts < maxAttempts) {
            List<Question> fetched = fetchQuestions(OPEN_TDB_BATCH_SIZE);
            for (Question q : fetched) {
                String mappedTopic = classifyTopic(q);
                if (!topic.equalsIgnoreCase(mappedTopic)) {
                    continue;
                }

                String[] opts = q.getOptions();
                if (opts == null || opts.length < 4) {
                    continue;
                }

                boolean inserted = DatabaseManager.insertQuestionIfAbsent(
                        topic,
                        normalizeDifficulty(q.getDifficulty()),
                        q.getquesText(),
                        opts[0],
                        opts[1],
                        opts[2],
                        opts[3],
                        q.getcorrectOptionidx(),
                        q.getExplanation());

                if (inserted) {
                    added++;
                    if (added >= minNewQuestions) {
                        break;
                    }
                }
            }
            attempts++;
        }

        return added;
    }

    public static int importQuestionsForAllTopics(int minNewPerTopic) throws Exception {
        int totalAdded = 0;
        for (String topic : DatabaseManager.getTopicCatalog()) {
            totalAdded += importQuestionsForTopic(topic, minNewPerTopic);
        }
        return totalAdded;
    }

    private static String classifyTopic(Question question) {
        StringBuilder text = new StringBuilder();
        text.append(safeLower(question.getquesText())).append(' ');
        text.append(safeLower(question.getExplanation())).append(' ');
        String[] options = question.getOptions();
        if (options != null) {
            for (String opt : options) {
                text.append(safeLower(opt)).append(' ');
            }
        }

        String haystack = text.toString();

        if (containsAny(haystack, "sql", "database", "query", "join", "table", "jdbc", "resultset", "preparedstatement", "statement")) {
            return "JDBC & SQL";
        }

        if (containsAny(haystack, "arraylist", "linkedlist", "hashmap", "treemap", "set", "map", "list", "queue", "collection", "iterator")) {
            return "Collections Framework";
        }

        if (containsAny(haystack, "exception", "try", "catch", "finally", "throw", "throws", "runtimeexception", "error")) {
            return "Exception Handling";
        }

        if (containsAny(haystack, "inherit", "polymorphism", "encapsulation", "abstraction", "interface", "override", "object", "class", "constructor")) {
            return "OOP Fundamentals";
        }

        return "Java Basics";
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private static String normalizeDifficulty(String difficulty) {
        if (difficulty == null) {
            return "medium";
        }
        String value = difficulty.trim().toLowerCase();
        if ("easy".equals(value) || "medium".equals(value) || "hard".equals(value)) {
            return value;
        }
        return "medium";
    }

    private static String unescapeHtml(String text) {
        if (text == null) return null;
        return text.replace("&quot;", "\"")
                   .replace("&#039;", "'")
                   .replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&shy;", "");
    }
}
