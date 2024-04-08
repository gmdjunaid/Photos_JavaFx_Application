package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.models.*;

import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static photos.controller.DashboardController.currentAlbum;
import static photos.controller.LoginController.loggedInUser;

import photos.models.Album;

/**
 * This class handles the photosDash view
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class PhotosController {

    /**
     * The button that deletes a photo.
     */
    @FXML
    public Button deletePhoto;

    /**
     * The button that captions or re-captions a photo.
     */
    @FXML
    public Button captionPhotoBtn;

    /**
     * The button that logs out the user.
     */
    @FXML
    public Button logOutBtn;

    /**
     * The label that displays the current logged in user.
     */
    @FXML
    public Label userLabel;

    /**
     * The button that bring the user back to the album dashboard.
     */
    @FXML
    public Button backButton;

    /**
     * The button that copies a photo to another album.
     */
    @FXML
    public Button copyToBtn;

    /**
     * The button that moves a photo to another album.
     */
    @FXML
    public Button moveToBtn;

    /**
     * The button that starts a photo sequence.
     */
    @FXML
    public Button photoSeqBtn;

    /**
     * The button that adds a tag to photo.
     */
    @FXML
    public Button addTagBtn;

    /**
     * The button that deletes a tag from a photo.
     */
    @FXML
    public Button delTagBtn;

    /**
     * The label that displays the current album the user is in.
     */
    @FXML
    public Label currentAlbumLabel;

    /**
     * The tile pane that displays each photo.
     */
    @FXML
    private TilePane photosPane;

    /**
     * The button that opens the tag manager.
     */
    @FXML
    private Button mngTagBtn;

    /**
     * The larger view of a photo when it is selected.
     */
    @FXML
    private ImageView largePhotoView;

    /**
     * The label that displays the caption of a photo.
     */
    @FXML
    private Label photoCaptionLabel;

    /**
     * The label that displays the tags of a photo.
     */
    @FXML
    private Label photoTagsLabel;

    /**
     * The label that displays the date of a photo.
     */
    @FXML
    private Label photoDateLabel;

    /**
     * The list of photos associated with an album.
     */
    private List<Photo> photos = new ArrayList<>();

    /**
     * The selected photo on click.
     */
    private Photo selectedPhoto;

    /**
     * The current photo on click.
     */
    public static Photo currentPhoto;

    /**
     * The name of the current album.
     */
    private String albumName;


    /**
     * The current photo being displayed.
     */
    private Photo displayedPhoto; // The photo being currently displayed

    /**
     * Initialize the screen when the dashboard is loaded.
     */
    @FXML
    void initialize() {
        DataSave.loadUser(loggedInUser);
        if (!currentAlbum.getPhotos().isEmpty()) {
            for (Photo photo : currentAlbum.getPhotos().subList(0, currentAlbum.getNumberOfPhotos())) {
                photo.setFilePath();
                displayPhoto(photo);
            }
        }
        userLabel.setText("Logged in as " + loggedInUser.getUsername());
        currentAlbumLabel.setText("Current Album: " + currentAlbum.getAlbumName());
        DataSave.saveOnQuit(mngTagBtn);
    }

    /**
     * Displays the details of a photo.
     *
     * @param photo The specified photo.
     */
    private void displayPhotoDetails(Photo photo) {
        if (photo != null) {
            displayedPhoto = photo;
            Image image = new Image(photo.getFilePath().toUri().toString(), true);
            largePhotoView.setImage(image);
            photoCaptionLabel.setText(photo.getCaption());
            photoDateLabel.setText(photo.getFormattedCaptureDate());
            photoTagsLabel.setText(photo.getFormattedTags());
        }
    }

    /**
     * Copies a photo from one album to another.
     */
    @FXML
    private void copyPhotoToAlbum() {
        if (displayedPhoto != null) {
            List<String> choices = new ArrayList<>();
            for (Album album : loggedInUser.getUserAlbums()) {
                choices.add(album.getAlbumName());
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>("Albums", choices);
            dialog.setTitle("Select an Album to Copy to");
            dialog.setHeaderText("Choose an Album:");
            dialog.setContentText("Album:");

            // Show the choice dialog and wait for user input
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selAlbum = result.get();
                for (Album album : loggedInUser.getUserAlbums()) {
                    if (album.getAlbumName().equalsIgnoreCase(selAlbum)) {
                        album.addPhoto(displayedPhoto);
                        if (result.get().equalsIgnoreCase(currentAlbum.getAlbumName())) {
                            displayPhoto(displayedPhoto);
                        }
                        break;
                    }
                }

            }
        }
    }

    /**
     * Moves a photo from one album to another.
     */
    @FXML
    private void movePhotoToAlbum() {
        if (displayedPhoto != null) {
            List<String> choices = new ArrayList<>();
            for (Album album : loggedInUser.getUserAlbums()) {
                choices.add(album.getAlbumName());
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>("Albums", choices);
            dialog.setTitle("Select an Album to Move to");
            dialog.setHeaderText("Choose an Album:");
            dialog.setContentText("Album:");

            // Show the choice dialog and wait for user input
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selAlbum = result.get();
                for (Album album : loggedInUser.getUserAlbums()) {
                    if (album.getAlbumName().equalsIgnoreCase(selAlbum)) {
                        album.addPhoto(displayedPhoto);
                        removePhoto();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Saves the info of all photos to disk.
     */
    @FXML
    private void savePhotoInfo() {
        DataSave.saveUser(loggedInUser);
        try {
            Stage dashboardStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/dashboard.fxml"));
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();

            // Close the login window
            Stage photosStage = (Stage) logOutBtn.getScene().getWindow();
            photosStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a photo to the album when the "Add Photo" button is clicked.
     */
    @FXML
    private void addPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        // Set extension filter to only .jpg, .jpeg, .png, and .gif files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        // Open file dialog
        Stage stage = (Stage) photosPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                // Create a photo object and add it to the album and list
                Path path = file.toPath();
                Photo photo = new Photo(path);
                photos.add(photo);
                currentAlbum.addPhoto(photo); // Add photo to the current album
                DataSave.saveUser(loggedInUser);
                displayPhoto(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes a photo from an album when the remove button is clicked.
     */
    @FXML
    private void removePhoto() {
        if (selectedPhoto != null) {
            photos.remove(selectedPhoto);
            currentAlbum.getPhotos().remove(selectedPhoto);
            DataSave.saveUser(loggedInUser);
            ImageView imageViewToRemove = null;
            for (Node node : photosPane.getChildren()) {
                if (node instanceof ImageView imageView) {
                    if (imageView.getUserData() == selectedPhoto) {
                        imageViewToRemove = imageView;
                        break;
                    }
                }
            }
            if (imageViewToRemove != null) {
                photosPane.getChildren().remove(imageViewToRemove);
            }
            largePhotoView.setImage(null);
            photoCaptionLabel.setText("");
            photoDateLabel.setText("");
            photoTagsLabel.setText("");
            photosPane.getChildren().clear();
            for (Photo photo : currentAlbum.getPhotos().subList(0, currentAlbum.getNumberOfPhotos())) {
                displayPhoto(photo);
            }

        }
    }

    /**
     * Displays a photo on the tile pane.
     *
     * @param photo The photo to display.
     */
    private void displayPhoto(Photo photo) {
        Image image = new Image(photo.getFilePath().toUri().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Add the ImageView to the photosPane with a click event to select the photo
        imageView.setOnMouseClicked(e -> {
            selectedPhoto = photo;
            displayPhotoDetails(photo);
        });
        photosPane.getChildren().add(imageView);

    }

    /**
     * Opens the photo sequence view when the photo sequence button is clicked.
     *
     * @param actionEvent Button click
     */
    public void handlePhotoSequence(ActionEvent actionEvent) {
        currentPhoto = selectedPhoto;
        try {
            Stage sequenceStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/photoSequenceView.fxml"));
            sequenceStage.setScene(new Scene(root));
            sequenceStage.setTitle("Sequence");
            sequenceStage.show();

            DataSave.saveUser(loggedInUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the log out button.
     *
     */
    public void handleLogOut() {
        try {
            Stage logInStage = new Stage();
            // Load the dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/photos/view/login.fxml"));
            logInStage.setScene(new Scene(root));
            logInStage.setTitle("Dashboard");
            logInStage.show();

            DataSave.saveUser(loggedInUser);

            // Close the login window
            Stage photosStage = (Stage) logOutBtn.getScene().getWindow();
            photosStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Re/Caption" button.
     */
    public void captionPhoto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Caption or Recaption a Photo");
        dialog.setHeaderText("Enter Caption:");
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();
        if (displayedPhoto != null) {
            result.ifPresent(newCaption -> {
                if (!newCaption.isEmpty()) {
                    displayedPhoto.setCaption(newCaption);
                    displayPhotoDetails(displayedPhoto);
                }
            });
        }
    }

    /**
     * Opens the photo tag manager when the "Manage Tags" button is clicked.
     *
     * @param actionEvent the button click.
     */
    public void managePhotoTags(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/tagview.fxml"));
            Parent root = loader.load();

            TagviewController tagviewController = loader.getController();
            if (selectedPhoto != null) {
                tagviewController.setCurrentPhoto(selectedPhoto); // Also ensure the current photo is set
            }

            Stage tagViewStage = new Stage();
            tagViewStage.setScene(new Scene(root));
            tagViewStage.setTitle("Manage Photo Tags");
            tagViewStage.show();

            Stage photosdashboardStage = (Stage) mngTagBtn.getScene().getWindow();
            photosdashboardStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
