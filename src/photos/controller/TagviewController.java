package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import photos.models.DataSave;
import photos.models.Photo;
import photos.models.Album;
import photos.models.User;

import java.io.IOException;
import java.util.*;

import static photos.controller.LoginController.loggedInUser;

/**
 * This class handles the tagview view for tag management.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class TagviewController {

    /**
     * The buttons for deleting, editing, adding tags and tag types.
     */
    @FXML
    private Button deletePhotoTagBtn, editPhotoTagBtn, addTagBtn, deleteTagTypeBtn, addTagTypeButton;

    /**
     * The button that closes the window when done.
     */
    @FXML
    private Button doneButton;

    /**
     * The Combo Box containing the current tag values and tag types.
     */
    @FXML
    private ComboBox<String> currentTagsBox, tagTypesBox;

    /**
     * The Combo Box containing the defined multiplicities of tag types. (Single or Many)
     */
    @FXML
    private ComboBox<String> tagMultiplicityBox;

    /**
     * The text field where the tag type or value is entered.
     */
    @FXML
    private TextField tagTextField;

    /**
     * The image view of the current selected photo. Displays blank if no photo is selected.
     */
    @FXML
    private ImageView imageView;

    /**
     * The current selected photo to use.
     */
    private Photo currentPhoto;

    /**
     * Sets the image view to the current selected photo.
     *
     * @param photo The current photo.
     */
    public void setCurrentPhoto(Photo photo) {
        this.currentPhoto = photo;
        if (currentPhoto != null && currentPhoto.getFilePath() != null) {
            Image image = new Image(currentPhoto.getFilePath().toUri().toString());
            imageView.setImage(image);
        }
        loadCurrentTags(); // Reload tags for the new photo
    }


    /**
     * Initializes the screen when the window opens.
     */
    @FXML
    public void initialize() {
        loadTagTypes();
        if (currentPhoto != null) {
            loadCurrentTags();
        }
        currentTagsBox.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage tagViewStage = (Stage) newScene.getWindow();
                tagViewStage.setOnCloseRequest(event -> {
                    event.consume();
                    handleDone();
                });
            }
        });
    }

    /**
     * Populates the currentTagsBox with the tags associated with the current photo.
     */
    private void loadCurrentTags() {
        ObservableList<String> tags = FXCollections.observableArrayList();
        for (Map.Entry<String, Set<String>> entry : currentPhoto.getTags().entrySet()) {
            // For each tag name, iterate over its set of values
            for (String value : entry.getValue()) {
                tags.add(entry.getKey() + ": " + value);
            }
        }
        currentTagsBox.setItems(tags);
    }

    /**
     * Populates the tagTypesBox with the current tag types.
     */
    private void loadTagTypes() {
        ObservableList<String> tagTypesList = FXCollections.observableArrayList(DataSave.getTagTypes().keySet());
        tagTypesBox.setItems(tagTypesList); // creating/editing tags
    }

    /**
     * Adds a tag to the photo.
     */
    @FXML
    private void handleAddTagToPhoto() {
        String selectedType = tagTypesBox.getSelectionModel().getSelectedItem();
        String tagValue = tagTextField.getText().trim();
        if (selectedType != null && !tagValue.isEmpty()) {
            boolean success = currentPhoto.addTag(selectedType, tagValue);
            if (!success) {
                // Show an error popup
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Adding Tag");
                alert.setHeaderText("Constraint Violation");
                alert.setContentText("A 'Single' tag type can only have one instance.");
                alert.showAndWait();
            } else {
                // Success, reload tags to reflect the new addition
                loadCurrentTags();
            }
        }
    }

    /**
     * Deletes a tag from a photo
     */
    @FXML
    private void handleDeletePhotoTag() {
        String selectedTag = currentTagsBox.getSelectionModel().getSelectedItem();
        if (selectedTag != null) {
            String[] parts = selectedTag.split(": ");
            if (parts.length == 2) {
                currentPhoto.removeTag(parts[0], parts[1].trim());
                loadCurrentTags();
            }
        }
    }

    /**
     * Handles the button that adds a new tag type.
     */
    @FXML
    private void handleAddNewTagType() {
        String newType = tagTextField.getText().trim();
        //String multiplicityChoice = tagMultiplicityBox.getSelectionModel().getSelectedItem(); // Get selected multiplicity

        List<String> choices = new ArrayList<>();
        choices.add(DataSave.TagTypeConstraint.SINGLE.toString());
        choices.add(DataSave.TagTypeConstraint.MULTIPLE.toString());

        if (!newType.isEmpty() && !DataSave.getTagTypes().containsKey(newType)) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Single/Multiple", choices);
            dialog.setTitle("Select the Multiplicity of the Tag Type");
            dialog.setHeaderText("Choose a Value:");
            dialog.setContentText("Multiplicity:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                DataSave.TagTypeConstraint constraint = "Multiple".equalsIgnoreCase(result.get()) ? DataSave.TagTypeConstraint.MULTIPLE : DataSave.TagTypeConstraint.SINGLE;
                DataSave.addTagType(newType, constraint);
                loadTagTypes();
            }
        } else {
            // Show error message for duplicate or empty tag type
        }
    }

    /**
     * Handles the button that deletes a tag type.
     */
    @FXML
    private void handleDeleteTagType() {
        String selectedType = tagTypesBox.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            DataSave.removeTagType(selectedType);
            loadTagTypes();
        }
    }

    /**
     * Edits the tags of a photo.
     */
    @FXML
    void editPhotoTags() {
        String selectedTagCombo = currentTagsBox.getSelectionModel().getSelectedItem();
        if (selectedTagCombo != null && !selectedTagCombo.isEmpty()) {
            String[] parts = selectedTagCombo.split(": ");
            if (parts.length < 2) return; // Something went wrong, or the selected format isn't as expected.

            String tagKey = parts[0];
            String tagValue = parts[1];

            // Prompt user for new tag value
            TextInputDialog dialog = new TextInputDialog(tagValue);
            dialog.setTitle("Edit Tag");
            dialog.setHeaderText("Edit Tag for " + tagKey);
            dialog.setContentText("Enter new tag value:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newTagValue -> {
                // Remove the old tag and add the new one
                currentPhoto.removeTag(tagKey, tagValue); // Remove old tag value
                currentPhoto.addTag(tagKey, newTagValue); // Add new tag value

                // Update UI and save changes
                loadCurrentTags();
                DataSave.saveUser(loggedInUser);
            });
        } else {
            // No tag selected, show an error message or a notification to the user
        }
    }

    /**
     * Handles the done button.
     */
    @FXML
    private void handleDone() {
        DataSave.saveUser(loggedInUser); // Save changes
        switchToPhotosDashboard(); // Switch back to the PhotosDashboard view
    }

    /**
     * Switches back to the photos dashboard once exited.
     */
    private void switchToPhotosDashboard() {
        try {
            // Create a new Stage (window)
            Stage photosDashboardStage = new Stage();

            // Load the Photos Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/photosDash.fxml"));
            Parent root = loader.load();
            PhotosController photosController = loader.getController();
            photosDashboardStage.setScene(new Scene(root));
            photosDashboardStage.setTitle("Photos Dashboard");
            photosDashboardStage.show();

            Stage dashboardStage = (Stage) doneButton.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException
        }
    }



}
