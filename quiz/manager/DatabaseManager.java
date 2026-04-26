package quiz.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import quiz.model.Question;

public class DatabaseManager {
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:quiz_data.db";
    private static final List<String> CURATED_TOPICS = Arrays.asList(
            "Java Basics",
            "OOP Fundamentals",
            "Collections Framework",
            "Exception Handling",
            "JDBC & SQL");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DEFAULT_DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT NOT NULL)";
            stmt.execute(createUsersTable);

            // Create sessions table
            String createSessionsTable = "CREATE TABLE IF NOT EXISTS sessions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "score INTEGER DEFAULT 0, " +
                    "date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id))";
            stmt.execute(createSessionsTable);

            // Create answers table
            String createAnswersTable = "CREATE TABLE IF NOT EXISTS answers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "session_id INTEGER, " +
                    "question_text TEXT, " +
                    "selected_option TEXT, " +
                    "correct_option TEXT, " +
                    "is_correct BOOLEAN, " +
                    "FOREIGN KEY(session_id) REFERENCES sessions(id))";
            stmt.execute(createAnswersTable);

            // Create topic-wise questions table
            String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "topic TEXT NOT NULL, " +
                    "difficulty TEXT NOT NULL, " +
                    "question_text TEXT NOT NULL UNIQUE, " +
                    "option_a TEXT NOT NULL, " +
                    "option_b TEXT NOT NULL, " +
                    "option_c TEXT NOT NULL, " +
                    "option_d TEXT NOT NULL, " +
                    "correct_option_idx INTEGER NOT NULL CHECK(correct_option_idx BETWEEN 0 AND 3), " +
                    "explanation TEXT)";
            stmt.execute(createQuestionsTable);

            seedQuestionsIfEmpty(conn);

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void seedQuestionsIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM questions";
        try (PreparedStatement countStmt = conn.prepareStatement(countSql);
                ResultSet rs = countStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // DB already has questions — skip seeding
            }
        }

        String insertSql = "INSERT INTO questions(topic, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option_idx, explanation) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            // Java Basics
            addQ(pstmt, "Java Basics", "easy", "Which keyword is used to inherit a class in Java?", "extends", "implements", "inherit", "super", 0, "Use extends for class inheritance.");
            addQ(pstmt, "Java Basics", "easy", "Which method is JVM entry point?", "start()", "main()", "run()", "init()", 1, "main() is the entry point.");
            addQ(pstmt, "Java Basics", "medium", "What is method overloading?", "Same name, different parameters", "Same signature, different class", "Different name, same params", "Same return type only", 0, "Overloading changes parameter list.");
            addQ(pstmt, "Java Basics", "medium", "Which type stores true/false?", "int", "String", "boolean", "char", 2, "boolean stores true/false.");
            addQ(pstmt, "Java Basics", "hard", "What does final class mean?", "Cannot create object", "Cannot be inherited", "Has only static methods", "Can only have final fields", 1, "A final class cannot be extended.");

            // OOP Fundamentals
            addQ(pstmt, "OOP Fundamentals", "easy", "Encapsulation means:", "Hiding data with methods", "Using many constructors", "Running in parallel", "Avoiding classes", 0, "Encapsulation wraps data and behavior.");
            addQ(pstmt, "OOP Fundamentals", "easy", "Polymorphism allows:", "One interface many forms", "Only one object", "Only one method", "No inheritance", 0, "Polymorphism means many forms.");
            addQ(pstmt, "OOP Fundamentals", "medium", "Which is runtime polymorphism?", "Method overloading", "Method overriding", "Constructor chaining", "Abstract class", 1, "Overriding is resolved at runtime.");
            addQ(pstmt, "OOP Fundamentals", "medium", "Abstraction focuses on:", "What to do", "How to compile", "How memory works", "Thread scheduling", 0, "Abstraction hides implementation details.");
            addQ(pstmt, "OOP Fundamentals", "hard", "Association represents:", "IS-A", "PART-OF", "USES-A relationship", "HAS-A strict ownership", 2, "Association is a generic uses relationship.");

            // Collections Framework
            addQ(pstmt, "Collections Framework", "easy", "Which collection allows duplicates and maintains insertion order?", "Set", "List", "Map", "TreeSet", 1, "List allows duplicates in order.");
            addQ(pstmt, "Collections Framework", "easy", "Which collection stores key-value pairs?", "List", "Set", "Map", "Queue", 2, "Map stores key-value entries.");
            addQ(pstmt, "Collections Framework", "medium", "Which implementation keeps insertion order for map?", "HashMap", "TreeMap", "LinkedHashMap", "WeakHashMap", 2, "LinkedHashMap keeps insertion order.");
            addQ(pstmt, "Collections Framework", "medium", "Which collection does not allow duplicates?", "List", "Set", "Vector", "ArrayList", 1, "Set prevents duplicates.");
            addQ(pstmt, "Collections Framework", "hard", "Which map keeps keys sorted?", "HashMap", "LinkedHashMap", "TreeMap", "Hashtable", 2, "TreeMap sorts keys.");

            // Exception Handling
            addQ(pstmt, "Exception Handling", "easy", "Which block always executes?", "try", "catch", "finally", "throw", 2, "finally executes in most flows.");
            addQ(pstmt, "Exception Handling", "easy", "Which keyword throws an exception explicitly?", "throws", "throw", "catch", "final", 1, "throw is used inside method body.");
            addQ(pstmt, "Exception Handling", "medium", "Which is unchecked exception?", "IOException", "SQLException", "NullPointerException", "ParseException", 2, "NPE is unchecked.");
            addQ(pstmt, "Exception Handling", "medium", "throws keyword is used in:", "catch block", "method signature", "finally block", "class declaration", 1, "throws appears in method signature.");
            addQ(pstmt, "Exception Handling", "hard", "Custom exceptions should usually extend:", "Throwable", "Error", "RuntimeException or Exception", "Object", 2, "Custom exceptions commonly extend Exception or RuntimeException.");

            // JDBC & SQL
            addQ(pstmt, "JDBC & SQL", "easy", "Which command retrieves rows?", "INSERT", "SELECT", "UPDATE", "DELETE", 1, "SELECT fetches data.");
            addQ(pstmt, "JDBC & SQL", "easy", "Which clause filters rows?", "GROUP BY", "WHERE", "ORDER BY", "HAVING", 1, "WHERE filters rows.");
            addQ(pstmt, "JDBC & SQL", "medium", "Which join returns matching rows from both tables?", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "FULL JOIN", 2, "INNER JOIN returns matched rows.");
            addQ(pstmt, "JDBC & SQL", "medium", "Which command inserts new rows?", "INSERT", "ADD", "CREATE", "PUT", 0, "INSERT adds new rows.");
            addQ(pstmt, "JDBC & SQL", "hard", "Which clause filters grouped results?", "WHERE", "HAVING", "ORDER BY", "LIMIT", 1, "HAVING filters groups.");
        }
        System.out.println("Seeded 25 starter questions into the database.");
    }

    private static void addQ(
            PreparedStatement pstmt, String topic, String difficulty,
            String questionText, String optA, String optB, String optC, String optD,
            int correctIdx, String explanation) throws SQLException {
        pstmt.setString(1, topic);
        pstmt.setString(2, difficulty);
        pstmt.setString(3, questionText);
        pstmt.setString(4, optA);
        pstmt.setString(5, optB);
        pstmt.setString(6, optC);
        pstmt.setString(7, optD);
        pstmt.setInt(8, correctIdx);
        pstmt.setString(9, explanation);
        pstmt.executeUpdate();
    }

    public static List<String> getAvailableTopics() {
        List<String> topics = new ArrayList<>();
        String sql = "SELECT DISTINCT topic FROM questions ORDER BY topic";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                topics.add(rs.getString("topic"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading topics: " + e.getMessage());
        }

        List<String> ordered = new ArrayList<>();
        for (String topic : CURATED_TOPICS) {
            if (topics.contains(topic)) {
                ordered.add(topic);
            }
        }

        for (String topic : topics) {
            if (!ordered.contains(topic)) {
                ordered.add(topic);
            }
        }
        return ordered;
    }

    public static List<String> getTopicCatalog() {
        return new ArrayList<>(CURATED_TOPICS);
    }

    public static int getQuestionCountByTopic(String topic) {
        if (topic == null || topic.isBlank() || "All Topics".equalsIgnoreCase(topic)) {
            return getTotalQuestionCount();
        }

        String sql = "SELECT COUNT(*) FROM questions WHERE topic = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topic);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting questions by topic: " + e.getMessage());
        }
        return 0;
    }

    public static int getTotalQuestionCount() {
        String sql = "SELECT COUNT(*) FROM questions";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting total questions: " + e.getMessage());
        }
        return 0;
    }

    public static boolean insertQuestionIfAbsent(
            String topic,
            String difficulty,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            int correctOptionIdx,
            String explanation) {

        String sql = "INSERT OR IGNORE INTO questions(topic, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option_idx, explanation) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topic);
            pstmt.setString(2, difficulty == null || difficulty.isBlank() ? "medium" : difficulty.toLowerCase());
            pstmt.setString(3, questionText);
            pstmt.setString(4, optionA);
            pstmt.setString(5, optionB);
            pstmt.setString(6, optionC);
            pstmt.setString(7, optionD);
            pstmt.setInt(8, correctOptionIdx);
            pstmt.setString(9, explanation);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting imported question: " + e.getMessage());
        }
        return false;
    }

    public static List<Question> getQuestionsByTopic(String topic, int requestedCount) {
        List<Question> questions = new ArrayList<>();
        boolean allTopics = topic == null || topic.isBlank() || "All Topics".equalsIgnoreCase(topic);

        String sql = "SELECT id, topic, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option_idx, explanation " +
                "FROM questions " +
                (allTopics ? "" : "WHERE topic = ? ") +
                "ORDER BY RANDOM() LIMIT ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int idx = 1;
            if (!allTopics) {
                pstmt.setString(idx++, topic);
            }
            pstmt.setInt(idx, requestedCount);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] options = new String[] {
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d")
                    };

                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getString("question_text"),
                            options,
                            rs.getInt("correct_option_idx"),
                            rs.getString("difficulty"),
                            rs.getString("topic"),
                            rs.getString("explanation")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }

        return questions;
    }

    public static int saveUser(String name) {
        String sql = "INSERT INTO users(name) VALUES(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
        return -1;
    }

    public static int createSession(int userId) {
        String sql = "INSERT INTO sessions(user_id) VALUES(?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating session: " + e.getMessage());
        }
        return -1;
    }

    public static void saveAnswer(int sessionId, String questionText, String selectedOption, String correctOption,
            boolean isCorrect) {
        String sql = "INSERT INTO answers(session_id, question_text, selected_option, correct_option, is_correct) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, selectedOption);
            pstmt.setString(4, correctOption);
            pstmt.setBoolean(5, isCorrect);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving answer: " + e.getMessage());
        }
    }

    public static void updateSessionScore(int sessionId, int score) {
        String sql = "UPDATE sessions SET score = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, score);
            pstmt.setInt(2, sessionId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating session score: " + e.getMessage());
        }
    }
}
