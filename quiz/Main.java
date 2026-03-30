package quiz;

import javafx.application.Application;
import javafx.stage.Stage;

import quiz.manager.DatabaseManager;

/**
 * Entry point for the Quiz Master JavaFX application.
 * Initializes the SceneManager and loads the Start screen.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize SQLite Database
        DatabaseManager.initializeDatabase();
        
        // Initialize scene manager with the primary stage
        SceneManager.init(primaryStage);

        // Configure window
        primaryStage.setTitle("Quiz Master");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        // Load the start screen
        SceneManager.switchScene("start.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
