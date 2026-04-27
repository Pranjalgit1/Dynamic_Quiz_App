package quiz;

import javafx.application.Application;
import javafx.stage.Stage;

import quiz.manager.DatabaseManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        DatabaseManager.initializeDatabase();

        SceneManager.init(primaryStage);

        primaryStage.setTitle("Dynamic Quiz App");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        SceneManager.switchScene("start.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
