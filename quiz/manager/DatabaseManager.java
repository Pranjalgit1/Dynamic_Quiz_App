package quiz.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:quiz_data.db";

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

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
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
