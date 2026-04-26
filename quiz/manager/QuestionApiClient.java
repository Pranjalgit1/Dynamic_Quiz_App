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
    private static final String API_URL = "https://opentdb.com/api.php?amount=%d&category=18&type=multiple";

    public static List<Question> fetchQuestions(int amount) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL, amount)))
                .GET()
                .build();

        HttpResponse<String> response = null;
        int maxRetries = 3;
        int currentAttempt = 0;

        while (currentAttempt < maxRetries) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
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
