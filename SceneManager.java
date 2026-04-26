package quiz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quiz.model.QuizSession;

//switching bw FXML scenes
public class SceneManager {

    private static Stage primaryStage;
    private static QuizSession currentSession;
    private static String playerName;

    // stores primary stage reference
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    // load fxml from view and apply css to it
    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("view/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 650);

            // Apply the shared stylesheet
            scene.getStylesheets().add(
                    SceneManager.class.getResource("view/style.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            System.err.println("Failed to load scene: " + fxmlFile);
            e.printStackTrace();
        }
    }

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
