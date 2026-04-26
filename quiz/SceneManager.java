package quiz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quiz.model.QuizSession;

/**
 * Utility class for switching between FXML scenes and
 * sharing state (player name, quiz session) across screens.
 */
public class SceneManager {

    private static Stage primaryStage;
    private static QuizSession currentSession;
    private static String playerName;

    /** Store reference to the primary stage */
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Load an FXML file from the view/ folder, apply CSS, and set it on the stage.
     * @param fxmlFile filename inside quiz/view/ (e.g. "start.fxml")
     */
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("view/" + fxmlFile)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 650);

            // Apply the shared stylesheet
            scene.getStylesheets().add(
                SceneManager.class.getResource("view/style.css").toExternalForm()
            );
            primaryStage.setScene(scene);
        } catch (Exception e) {
            System.err.println("Failed to load scene: " + fxmlFile);
            e.printStackTrace();
        }
    }

    // ---- Shared state getters/setters ----

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static QuizSession getCurrentSession() {
        return currentSession;
    }

    public static void setCurrentSession(QuizSession session) {
        currentSession = session;
    }

    public static String getPlayerName() {
        return playerName;
    }

    public static void setPlayerName(String name) {
        playerName = name;
    }
}
