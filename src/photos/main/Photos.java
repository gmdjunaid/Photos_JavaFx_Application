package photos.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import photos.models.UserManager;

/**
 * This is the main class file that starts the program
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class Photos extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        UserManager.loadUsers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/login.fxml"));
        Parent root = loader.load();


        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Simple Login");
        primaryStage.show();
    }

    /**
     * The main method of the application
     *
     * @param args Arguments
     */
    public static void main(String[] args) {

        launch(args);
    }
}