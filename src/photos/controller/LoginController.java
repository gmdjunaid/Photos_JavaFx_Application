package photos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import photos.models.User;
import photos.models.UserManager;

/**
 * This class handles the login.fxml view.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class LoginController {

    /**
     * A static variable that keeps track of the current session's user.
     */
    public static User loggedInUser;

    /**
     * The text field where the username is inputted.
     */
    @FXML
    private TextField usernameField;

    /**
     * The button that logs in.
     */
    @FXML
    private Button loginButton;

    /**
     * A field that displays when a username does not exist.
     */
    @FXML
    private Label blankField;

    /**
     * Returns the current session's logged-in user.
     *
     * @param username the username of the user.
     * @return Returns the user.
     */
    public static User getLoggedInUser(String username) {
        for (User user : UserManager.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Handles the login button.
     */
    @FXML
    private void handleLogin() {
        // login logic
        String input = usernameField.getText();
        File users = new File("src/photos/models/users.txt");

        for (User user : UserManager.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(input)) {
                if (user.getUsername().equalsIgnoreCase("admin")) {
                    openAdminDashboard();
                    return;
                }
                else {
                    loggedInUser = getLoggedInUser(input);
                    openDashboard();
                    return;
                }
            }
        }
        blankField.setText("The user \"" + input + "\" does not exist.");

    }

    /**
     * Opens the dashboard view when successfully logged in.
     */
    private void openDashboard() {
        try {
            Stage dashboardStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/dashboard.fxml"));
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();

            // Close the login window
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the admin dashboard view when logging in as "admin".
     */
    private void openAdminDashboard() {
        try {
            Stage adminStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/admin.fxml"));
            adminStage.setScene(new Scene(root));
            adminStage.setTitle("Dashboard");
            adminStage.show();

            // Close the login window
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}