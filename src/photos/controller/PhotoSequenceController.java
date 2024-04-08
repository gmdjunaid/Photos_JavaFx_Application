package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import photos.models.Photo;

import static photos.controller.PhotosController.currentPhoto;
import static photos.controller.DashboardController.currentAlbum;

/**
 * This class handles the photoSequenceView view.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class PhotoSequenceController {

    /**
     * The image view of the current photo.
     */
    @FXML
    public ImageView imageView;

    /**
     * The button that handles moving to the previous photo.
     */
    @FXML
    public Button leftButton;

    /**
     * The button that handles moving to the next photo.
     */
    @FXML
    public Button rightButton;

    /**
     * The text that displays the caption of a photo.
     */
    @FXML
    public Text captionText;

    /**
     * The text that displays the tags of a photo.
     */
    @FXML
    public Text tagsText;

    /**
     * The text that displays the date of a photo.
     */
    public Text dateText;

    /**
     * The button that closes the window when done.
     */
    @FXML
    public Button doneButton;

    /**
     * The index of the current photo.
     */
    private int currentPhotoIndex;

    /**
     * Initializes the view when first loaded.
     */
    @FXML
    void initialize() {
        for (Photo photo : currentAlbum.getPhotos()) {
            if (photo.equals(currentPhoto)) {
                currentPhotoIndex = currentAlbum.getPhotos().indexOf(photo);
                break;
            } else {
                currentPhotoIndex = 0;
            }
        }

        if (currentPhoto != null) {
            displayPhoto(currentPhoto);
        } else {
            displayPhoto(currentAlbum.getPhotos().get(0));
        }

    }

    /**
     * Displays the photo.
     *
     * @param photo The photo to display.
     */
    private void displayPhoto(Photo photo) {
        Image image = new Image(photo.getFilePath().toUri().toString());
        imageView.setImage(image);
        captionText.setText(photo.getCaption());
        tagsText.setText("Tags: " + photo.getFormattedTags());
        dateText.setText(photo.getFormattedCaptureDate());
    }

    /**
     * Handles the left button.
     */
    @FXML
    public void handleLeftButton() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex -= 1;
            currentPhoto = currentAlbum.getPhotos().get(currentPhotoIndex);
            displayPhoto(currentPhoto);
        }
    }

    /**
     * Handles the right button.
     */
    @FXML
    public void handleRightButton() {
        if (currentPhotoIndex < currentAlbum.getPhotos().size()-1) {
            currentPhotoIndex += 1;
            currentPhoto = currentAlbum.getPhotos().get(currentPhotoIndex);
            displayPhoto(currentPhoto);
        }
    }

    /**
     * Handles the done button.
     */
    @FXML
    public void handleDoneButton() {
        Stage sequenceStage = (Stage) doneButton.getScene().getWindow();
        sequenceStage.close();
    }
}
