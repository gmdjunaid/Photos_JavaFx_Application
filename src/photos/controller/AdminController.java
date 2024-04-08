package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import photos.models.DataSave;
import photos.models.User;
import photos.models.UserManager;

import java.io.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;

/**
 * This class handles the admin.fxml view
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class AdminController {

    /**
     * Button to add a user.
     */
    @FXML
    private Button addUser;

    /**
     * Button to delete a user.
     */
    @FXML
    private Button deleteUser;

    /**
     * Button to rename a user.
     */
    @FXML
    private Button renameUser;

    /**
     * Button to log out.
     */
    @FXML
    private Button logOutBtn;

    /**
     * List view of all users.
     */
    @FXML
    private ListView<String> userList;

    /**
     * Initializes the view on startup.
     */
    @FXML
    void initialize() {
        // populate the array of users for the listview
        populateUserList();
        userList.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && newItem.equalsIgnoreCase("admin")) {
                // If the selected item is "admin", disable the delete button
                deleteUser.setDisable(true);
                renameUser.setDisable(true);
            } else {
                // If the selected item is not "admin" or no item is selected, enable the delete button
                deleteUser.setDisable(false);
                renameUser.setDisable(false);
            }
        });
    }

    /**
     * Populates the list view with the users from users.txt
     */
    private void populateUserList() {
        try {
            File usersFile = new File("src/photos/models/users.txt");
            Scanner scanner = new Scanner(usersFile);
            ObservableList<String> users = FXCollections.observableArrayList();

            while (scanner.hasNextLine()) {
                String user = scanner.nextLine();
                users.add(user);
            }

            users.sort(null);
            userList.setItems(users);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a user when the "Add User" button is clicked.
     */
    @FXML
    void handleAddUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter Username:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();

        // Check if the user is empty
        result.ifPresent(username -> {
            if (!username.isEmpty()) {
                if (isUsernameExists(username)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("This username already exists.");
                    alert.setContentText("Please enter a different username");
                    alert.showAndWait();
                } else {
                    userList.getItems().add(username);
                    User user = new User(username);
                    UserManager.addUser(user);
                    writeUserToFile(username);
                    DataSave.saveUser(user);
                }
            }
        });
    }

    /**
     * Determines if the username exists.
     *
     * @param user The username that exists.
     * @return Returns true if the user exists, false otherwise.
     */
    private boolean isUsernameExists(String user) {
        try (Scanner scanner = new Scanner(new File("src/photos/models/users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase(user)) {
                    return true;
                }
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Writes the added user to user.txt
     *
     * @param newUser the username to add.
     */
    private void writeUserToFile(String newUser) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/photos/models/users.txt", true))) {
            writer.println(newUser);
            File userDirectory = new File("src/photos/saves/", newUser);
            boolean success = userDirectory.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user, including all its save data.
     *
     * @param user The user to delete.
     */
    private void deleteUserFromFile(String user) {
        String filePath = "src/photos/models/users.txt";

        try {
            // Read the contents of the file
            File inputFile = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains(user)) {
                    sb.append(line).append(System.lineSeparator());

                }
            }
            reader.close();

            // Write the modified content back to the file
            FileWriter writer = new FileWriter(inputFile);
            writer.write(sb.toString());
            writer.close();

            File userDirectory = new File("src/photos/saves/", user);
            deleteDirectory(userDirectory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively deletes data files from a directory.
     *
     * @param directory The directory to delete.
     * @return returns true if successfully deleted.
     */
    private static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }

        // Check if the given file is a directory
        if (directory.isDirectory()) {
            // Get all the files and subdirectories in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                // Iterate over each file and directory
                for (File file : files) {
                    boolean success = deleteDirectory(file);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }

    /**
     * Deletes a user when the Delete User button is clicked.
     */
    @FXML
    void handleDeleteUser() {
        String selectedUser = userList.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            ObservableList<String> users = userList.getItems();
            users.remove(selectedUser);
            Iterator<User> iterator = UserManager.getUsers().iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (user.getUsername().equalsIgnoreCase(selectedUser)) {
                    iterator.remove();
                }
            }
            deleteUserFromFile(selectedUser);
        }
    }

    /**
     * Renames a user when the Rename User button is clicked.
     */
    @FXML
    void handleRenameUser() {
        int selectedUserIndex = userList.getSelectionModel().getSelectedIndex();

        if (selectedUserIndex >= 0) {
            String oldUsername = userList.getItems().get(selectedUserIndex);

            TextInputDialog dialog = new TextInputDialog(oldUsername);
            dialog.setTitle("Rename User");
            dialog.setHeaderText("Change Username:");
            dialog.setContentText("Username:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newUsername -> {
                if (!newUsername.isEmpty()) {
                    userList.getItems().set(selectedUserIndex, newUsername);
                    deleteUserFromFile(oldUsername);
                    writeUserToFile(newUsername);
                    for (User user : UserManager.getUsers()) {
                        if (user.getUsername().equalsIgnoreCase(oldUsername)) {
                            user.setUsername(newUsername);
                            DataSave.saveUser(user);
                        }
                    }
                }
            });
        }
    }

    /**
     * Logs out when the "Log Out" button is clicked.
     */
    @FXML
    void handleLogOut() {
        try {
            Stage logInStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/login.fxml"));
            logInStage.setScene(new Scene(root));
            logInStage.setTitle("Dashboard");
            logInStage.show();

            // Close the login window
            Stage adminStage = (Stage) logOutBtn.getScene().getWindow();
            adminStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
