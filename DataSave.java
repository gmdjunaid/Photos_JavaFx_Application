package photos.models;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static photos.controller.LoginController.loggedInUser;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import photos.controller.DashboardController;

/**
 * This class handles the necessary functions for saving user data to disk.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class DataSave {

    /**
     * The stock photos directory included with the project.
     */
    public static final String STOCK_PHOTOS_DIRECTORY = "data/";

    /**
     * The tag types for all users.
     */
    private static Map<String, TagTypeConstraint> tagTypes = new HashMap<>();

    /**
     * The multiplicity of the tag types.
     */
    public enum TagTypeConstraint {
        /**
         * Single tag type allows for only one value of said type.
         */
        SINGLE,
        /**
         * Multiple tag type allows for more than one value.
         */
        MULTIPLE
    }

    /**
     * Special case for initializing stock photos from disk.
     */
    public static void initializeStockPhotos() {
        File stockPhotosDir = new File(STOCK_PHOTOS_DIRECTORY);
        File stockUserData = new File("src/photos/saves/stock/userdata.dat");
        if (stockPhotosDir.exists() && stockPhotosDir.isDirectory() && loggedInUser.getUsername().equalsIgnoreCase("stock")) {
            // Load stock photos from the directory
            Album stockAlbum;
            DashboardController dc = new DashboardController();
            if (!dc.isNameExists("stock") && !stockUserData.exists()) {
                stockAlbum = new Album("stock", loggedInUser);
                loggedInUser.addAlbum(stockAlbum);
                for (File file : stockPhotosDir.listFiles()) {
                    if (file.isFile()) {
                        Photo photo = new Photo(Paths.get(file.toString()));
                        stockAlbum.addPhoto(photo);
                    }
                }
            }
        }
    }

    /**
     * Save the user data to disk.
     *
     * @param user The specified user to save.
     */
    public static void saveUser(User user) {
        String userDirectoryPath = "src/photos/saves/" + user.getUsername();
        File baseDirectory = new File(userDirectoryPath);

        // Create the user directory if it doesn't exist
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Serialize the user object and save it to a file
        String filePath = baseDirectory.getPath() + "/userdata.dat";
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(user);
        } catch (IOException ignored) {
        }

        // save user albums
        for (Album album : user.getUserAlbums()) {
            saveAlbum(album, baseDirectory);
        }
    }

    /**
     * Loads the user's data from disk.
     *
     * @param user The user to load data from.
     * @return The User with all of its albums and photos from the previous session.
     */
    public static User loadUser(User user) {
        // Define the directory path for the user
        String userDirectoryPath = "src/photos/saves/" + user.getUsername();

        // Load the user data
        String userFilePath = userDirectoryPath + "/userdata.dat";
        try (FileInputStream fileIn = new FileInputStream(userFilePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            user = (User) objectIn.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return user;
    }

    /**
     * Save all the user's albums to disk.
     *
     * @param album The specified album to save. (Typically done in a loop to save all albums)
     * @param userDirectory The directory where the user data is located.
     */
    public static void saveAlbum(Album album, File userDirectory) {
        String albumFilePath = userDirectory.getPath() + "/album_" + album.getAlbumName() + ".dat";
        try (FileOutputStream fileOut = new FileOutputStream(albumFilePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(album);
        } catch (IOException ignored) {
        }

        savePhotos(userDirectory, album);
    }

    /**
     * Saves the photos of an album.
     *
     * @param userDirectory The directory where the user data is located.
     * @param album The specified album to save photos from.
     */
    public static void savePhotos(File userDirectory, Album album) {
        String photoFilePath = userDirectory.getPath() + "/" +album.getAlbumName() + "_photos.dat";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(photoFilePath))) {
            outputStream.writeObject(album.getPhotos());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the save data of an album.
     *
     * @param name The album name that needs to be deleted
     * @param user The user associated with the album.
     */
    public static void deleteData(String name, User user) {
        String filename = "/album_" + name + ".dat";
        File file = new File("src/photos/saves/" + user.getUsername() + filename);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Saves the tag types to disk.
     */
    private static void saveTagTypes(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/photos/saves/" + loggedInUser.getUsername() + "/" + loggedInUser.getUsername() + "_tagTypes.dat"))) {
            tagTypes.put("location", TagTypeConstraint.SINGLE);
            tagTypes.put("person", TagTypeConstraint.MULTIPLE);
            oos.writeObject(tagTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the tag types from disk.
     *
     * @param user The user to load tags for.
     */
    public static void loadTagTypes(User user) {
        if (user != null) {
            File file = new File("src/photos/saves/" + loggedInUser.getUsername() + "/" + loggedInUser.getUsername() + "_tagTypes.dat");
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    Object readObject = ois.readObject();
                    if (readObject instanceof Map) {
                        tagTypes = (Map<String, TagTypeConstraint>) readObject;
                    } else {
                        // Handle legacy format or unexpected data
                        tagTypes = new HashMap<>();
                        saveTagTypes(loggedInUser);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    tagTypes = new HashMap<>(); // Reinitialize on error
                }
            } else {
                tagTypes = new HashMap<>(); // Initialize with some default tag types if the file doesn't exist
                saveTagTypes(loggedInUser); // Save these initial types for future use
            }

        }
    }

    /**
     * Saves data when a user force quits the application (Closes the window)
     *
     * @param button A button associated with the current view.
     */
    public static void saveOnQuit(Button button) {
        button.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage tagViewStage = (Stage) newScene.getWindow();
                tagViewStage.setOnCloseRequest(event -> {
                    event.consume();
                    DataSave.saveUser(loggedInUser);
                    System.exit(0);
                });
            }
        });
    }

    /**
     * Adds a tag type to tagTypes
     *
     * @param type The type of tag
     * @param constraint The multiplicity of the tag (Single or Many)
     */
    public static void addTagType(String type, TagTypeConstraint constraint) {
        tagTypes.put(type, constraint);
        saveTagTypes(loggedInUser);
    }

    /**
     * Removes a tag type from tagTypes
     *
     * @param type The type to remove
     */
    public static void removeTagType(String type) {
        tagTypes.remove(type);
        saveTagTypes(loggedInUser);
    }

    /**
     * Gets the HashMap of tag types
     *
     * @return HashMap tagTypes
     */
    public static Map<String, TagTypeConstraint> getTagTypes() {
        return new HashMap<>(tagTypes);
    }
}