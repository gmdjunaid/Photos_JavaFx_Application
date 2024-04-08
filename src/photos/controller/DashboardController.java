package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static photos.controller.LoginController.loggedInUser;
import static photos.models.DataSave.loadTagTypes;

import photos.models.Album;
import photos.models.DataSave;
import photos.models.Photo;

/**
 * This class handles the dashboard.fxml view
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class DashboardController {

    /**
     * The button that opens an album.
     */
    @FXML
    public Button openButton;

    /**
     * The list view of all albums from a user.
     */
    @FXML
    public ListView<String> albumView = new ListView<>();

    /**
     * The first calendar to pick a date from for date search.
     */
    @FXML
    public DatePicker fromCalendar;

    /**
     * The second calendar to pick a date from for date search.
     */
    @FXML
    public DatePicker toCalendar;

    /**
     * The first tag type menu for tag search.
     */
    @FXML
    public ComboBox firstTypeMenu;

    /**
     * The second tag type menu for tag search. In the application, leave this blank for single tag search.
     */
    @FXML
    public ComboBox secondTypeMenu;

    /**
     * The first tag value text field for tag search.
     */
    @FXML
    public TextField firstTagTextField;

    /**
     * The second tag value text field for tag search. In the application, leave this blank for single tag search.
     */
    @FXML
    public TextField secondTagTextField;

    /**
     * The AND/OR menu for tag search.
     */
    @FXML
    public ComboBox andOrMenu = new ComboBox<>();

    /**
     * The label that displays the date range of an album.
     */
    @FXML
    public Label dateDetails;

    /**
     * The label that displays the number of photos on an album.
     */
    @FXML
    public Label albumSizeDetails;

    /**
     * The label that displays the name of an album.
     */
    @FXML
    public Label albumNameDetails;

    /**
     * The button for adding an album.
     */
    @FXML
    private Button addAlbumBtn;

    /**
     * The button for deleting an album.
     */
    @FXML
    private Button deleteAlbumBtn;

    /**
     * The button for logging out
     */
    @FXML
    private Button logOutBtn;

    /**
     * THe button for renaming an album.
     */
    @FXML
    private Button renameAlbumButton;

    /**
     * The label that displays the current logged in user.
     */
    @FXML
    private Label userLabel;

    /**
     * The current album used for when opening an album.
     */
    public static Album currentAlbum;

    /**
     * Initializes the view when loaded.
     */
    @FXML
    void initialize() {
        userLabel.setText("Logged in as " + loggedInUser.getUsername());
        loggedInUser = DataSave.loadUser(loggedInUser);
        DataSave.initializeStockPhotos();
        DataSave.loadTagTypes(loggedInUser);
        for (Album album : loggedInUser.getUserAlbums()) {
            albumView.getItems().add(album.getAlbumName());
        }

        ObservableList<String> tagTypes = FXCollections.observableArrayList(DataSave.getTagTypes().keySet());
        firstTypeMenu.setItems(tagTypes);
        secondTypeMenu.setItems(tagTypes);
        andOrMenu.setItems(FXCollections.observableArrayList(
                "AND",
                "OR"
        ));

        albumView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                for (Album album : loggedInUser.getUserAlbums()) {
                    if (album.getAlbumName().equalsIgnoreCase(newItem)) {
                        albumNameDetails.setText("Album Name: " + album.getAlbumName());
                        albumSizeDetails.setText("Album Size: " + (album.getNumberOfPhotos()));
                        if (!album.getPhotos().isEmpty()) {
                            dateDetails.setText("Date Range: " + album.getEarliestDate().toString() +
                                    " - " + album.getLatestDate().toString());
                        }
                        else {
                            dateDetails.setText("No Date Range");
                        }
                    }
                }
            }
        });
        DataSave.saveOnQuit(logOutBtn);
    }

    /**
     * Adds an album.
     */
    @FXML
    void handleAddAlbum() {
        boolean isValid = true;
        String s = null;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Enter Album Name:");
        dialog.setContentText("Album Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            s = result.get();
        }

        if (s != null && isNameExists(s)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This album name already exists.");
            alert.setContentText("Please enter a different name");
            alert.showAndWait();
            isValid = false;
        }

        if (isValid && result.isPresent()) {
            Album newAlbum = new Album(s, loggedInUser);
            albumView.getItems().add(s);
            loggedInUser.addAlbum(newAlbum);
            DataSave.saveUser(loggedInUser);
        }
    }

    /**
     * Deletes an album.
     */
    @FXML
    void handleDeleteAlbum() {
        String selectedItem = albumView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            ObservableList<String> albums = albumView.getItems();
            albums.remove(selectedItem);
            Iterator<Album> iterator = loggedInUser.getUserAlbums().iterator();
            while (iterator.hasNext()) {
                Album album = iterator.next();
                if (album.getAlbumName().equalsIgnoreCase(selectedItem)) {
                    iterator.remove();
                }
            }
            DataSave.deleteData(selectedItem, loggedInUser);
            DataSave.saveUser(loggedInUser);

        }
    }

    /**
     * Handles the log-out button.
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

            DataSave.saveUser(loggedInUser);

            // Close the login window
            Stage adminStage = (Stage) logOutBtn.getScene().getWindow();
            adminStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renames an album.
     */
    @FXML
    void handleRenameAlbum() {
        int selectedAlbumIndex = albumView.getSelectionModel().getSelectedIndex();

        if (selectedAlbumIndex >= 0) {
            String oldAlbumName = albumView.getItems().get(selectedAlbumIndex);

            TextInputDialog dialog = new TextInputDialog(oldAlbumName);
            dialog.setTitle("Rename Album");
            dialog.setHeaderText("Change Album:");
            dialog.setContentText("Album:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newName -> {
                if (!newName.isEmpty()) {
                    albumView.getItems().set(selectedAlbumIndex, newName);
                    for (Album album : loggedInUser.getUserAlbums()) {
                        if (album.getAlbumName().equalsIgnoreCase(oldAlbumName)) {
                            album.setAlbumName(newName);
                        }
                    }
                }
            });
            DataSave.deleteData(oldAlbumName, loggedInUser);
            DataSave.saveUser(loggedInUser);
        }
    }

    /**
     * Determines if an album name exists.
     *
     * @param name The name that exists
     * @return Returns true if the name exists, false otherwise
     */
    public boolean isNameExists(String name) {
        for (Album album : loggedInUser.getUserAlbums()) {
            if (name.equalsIgnoreCase(album.getAlbumName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a selected album.
     */
    @FXML
    void handleOpenButton() {
        String selectedAlbum = albumView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            for (Album album : loggedInUser.getUserAlbums()) {
                if (album.getAlbumName().equalsIgnoreCase(selectedAlbum)) {
                    currentAlbum = album;
                    openPhotosDashboard();
                    return;
                }
            }

        }
    }

    /**
     * Handles the search button for tag search.
     */
    public void handleTagSearch() {
        String firstTagType = (String) firstTypeMenu.getSelectionModel().getSelectedItem();
        String secondTagType = (String) secondTypeMenu.getSelectionModel().getSelectedItem();
        String andOrSelection = (String) andOrMenu.getSelectionModel().getSelectedItem();
        String firstTag = firstTagTextField.getText();
        String secondTag = secondTagTextField.getText();

        List<Photo> photoResultsList = new ArrayList<>();

        if (firstTagType != null && secondTagType != null && andOrSelection != null && firstTag != null && secondTag != null) {
            if (andOrSelection.equalsIgnoreCase("AND")) {
                for (Album album : loggedInUser.getUserAlbums()) {
                    for (Photo photo : album.getPhotos()) {
                        if (photo.hasTag(firstTagType, firstTag) && photo.hasTag(secondTagType, secondTag)) {
                            photoResultsList.add(photo);
                        }
                    }
                }
            } else if (andOrSelection.equalsIgnoreCase("OR")) {
                for (Album album : loggedInUser.getUserAlbums()) {
                    for (Photo photo : album.getPhotos()) {
                        if (photo.hasTag(firstTagType, firstTag) || photo.hasTag(secondTagType, secondTag)) {
                            photoResultsList.add(photo);
                        }
                    }
                }
            }
            if (!photoResultsList.isEmpty()) {
                openSearchResults(photoResultsList);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No results found with these parameters.");
                alert.setContentText("Please try a different search.");
                alert.showAndWait();
            }
        }

        // searching for only a single tag.
        else if (firstTagType != null && firstTag != null && (secondTag == null || secondTagType == null)) {
            for (Album album : loggedInUser.getUserAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    if (photo.hasTag(firstTagType, firstTag)) {
                        photoResultsList.add(photo);
                    }
                }
            }
            if (!photoResultsList.isEmpty()) {
                openSearchResults(photoResultsList);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No results found with these parameters.");
                alert.setContentText("Please try a different search.");
                alert.showAndWait();
            }
        }

    }

    /**
     * Handles the search button for date search.
     */
    public void handleDateSearch() {
        LocalDate fromDate = fromCalendar.getValue();
        LocalDate toDate = toCalendar.getValue();
        List<Photo> photoResultsList = new ArrayList<>();
        if (fromDate != null && toDate != null) {
            for (Album album : loggedInUser.getUserAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    LocalDate photoDate = photo.getLastModifiedDate();
                    if ((photoDate.isAfter(fromDate) || photoDate.isEqual(fromDate)) &&
                            (photoDate.isBefore(toDate) || photoDate.isEqual(toDate))) {
                        photoResultsList.add(photo);
                    }
                }
            }
            if (!photoResultsList.isEmpty()) {
                openSearchResults(photoResultsList);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No results found with these parameters.");
                alert.setContentText("Please try a different search.");
                alert.showAndWait();
            }
        }

    }

    /**
     * Opens the searchResultsView with all the results of a search.
     *
     * @param photos A list containing the results of a search.
     */
    @FXML
    private void openSearchResults(List<Photo> photos) {
        try {
            // Create a new Stage (window)
            Stage searchResultsStage = new Stage();

            // Load the Photos Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/photos/view/searchResultsView.fxml"));
            Parent root = loader.load();
            searchResultsController searchResultsController = loader.getController();
            searchResultsController.passData(photos);
            searchResultsStage.setScene(new Scene(root));
            searchResultsStage.setTitle("Search Results");
            searchResultsStage.show();

            Stage dashboardStage = (Stage) addAlbumBtn.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the photosDash view when opening an album.
     */
    @FXML
    private void openPhotosDashboard() {
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

            Stage dashboardStage = (Stage) addAlbumBtn.getScene().getWindow();
            dashboardStage.close();
            //((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the IOException
        }
    }


}
