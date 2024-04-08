package photos.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import photos.models.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static photos.controller.LoginController.loggedInUser;

/**
 * This class handles the searchResultsView view
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class searchResultsController {

    /**
     * The button that creates an album based on the search results.
     */
    @FXML
    private Button createAlbumFromResultsBtn;

    /**
     * The button that closes the window when done.
     */
    @FXML
    private Button doneBtn;

    /**
     * The tile pane that displays all the search results.
     */
    @FXML
    private TilePane photosPane;

    /**
     * The list of results.
     */
    private List<Photo> photoResults = new ArrayList<>();

    /**
     * Passes the search results list from the dashboard to the search results view.
     *
     * @param photos The list of results
     */
    public void passData(List<Photo> photos) {
        for (Photo photo : photos) {
            displayPhoto(photo);
        }
    }

    /**
     * Displays a photo in the tile pane. (Typically done inside a loop)
     *
     * @param photo The photo to display.
     */
    private void displayPhoto(Photo photo) {
        Image image = new Image(Paths.get(photo.getFilePathToString()).toUri().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        photoResults.add(photo);

        photosPane.getChildren().add(imageView);
    }

    /**
     * Handles the button that creates an album from the list of results.
     */
    @FXML
    void createAlbumFromResults() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Album");
        dialog.setHeaderText("Enter Album Name:");
        dialog.setContentText("Album Name:");

        Optional<String> result = dialog.showAndWait();

        DashboardController dc = new DashboardController();

        // Check if the user is empty
        result.ifPresent(albumName -> {
            if (!albumName.isEmpty()) {
                if (dc.isNameExists(albumName)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("This album name already exists.");
                    alert.setContentText("Please enter a different album name.");
                    alert.showAndWait();
                } else {
                    Album album = new Album(albumName, loggedInUser);
                    album.setPhotos(photoResults);
                    loggedInUser.addAlbum(album);
                    DataSave.saveUser(loggedInUser);
                }
            }
        });
    }

    /**
     * Handles the done button.
     */
    @FXML
    void handleDoneButton(ActionEvent event) {
        try {
            Stage dashboardStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/dashboard.fxml"));
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();

            // Close the login window
            Stage sequenceStage = (Stage) doneBtn.getScene().getWindow();
            sequenceStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
