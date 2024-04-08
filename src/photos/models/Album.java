package photos.models;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * The Album class handles the album objects that belong to the user.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class Album implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The album name String to use.
     */
    String albumName;

    /**
     * The user to use.
     */
    User user;

    /**
     * The list of photos to use.
     */
    private List<Photo> photos = new ArrayList<>();

    /**
     * Creates an album object.
     *
     * @param albumName The name of the album.
     * @param user The user the album is associated with.
     */
    public Album(String albumName, User user) {
        this.albumName = albumName;
        this.user = user;
        this.photos = new ArrayList<>(photos);
    }

    /**
     * Returns the album name.
     *
     * @return Album name
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Sets the album name to another.
     *
     * @param newName Defines the new name.
     */
    public void setAlbumName(String newName) {
        albumName = newName;
    }

    /**
     * Return the user associated with the album.
     *
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the number of photos inside an album.
     *
     * @return integer of number of photos.
     */
    public int getNumberOfPhotos() {
        return photos.size();
    }

    /**
     * adds a photo to the album.
     *
     * @param photo the specified photo to add.
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    /**
     * Get the photos inside an album.
     *
     * @return ArrayList containing the photo objects.
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Set the photos inside an album to a new list.
     *
     * @param photos List containing the new photos.
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = new ArrayList<>(photos);
    }

    /**
     * Searches the album for the photo with the earliest date.
     *
     * @return LocalDate type of the photo with the earliest date.
     */
    public LocalDate getEarliestDate() {
        if (!photos.isEmpty()) {
            LocalDate earliestDate = photos.get(0).getLastModifiedDate();
            for (int i = 1; i < photos.size(); i++) {
                // find the earliest date
                LocalDate date = photos.get(i).getLastModifiedDate();
                if (date != null && date.isBefore(earliestDate)) {
                    earliestDate = date;
                }
            }
            return earliestDate;
        }
        return null;
    }

    /**
     * Searches the album for the photo with the latest date.
     *
     * @return LocalDate type of the photo with the latest date.
     */
    public LocalDate getLatestDate() {
        if (!photos.isEmpty()) {
            LocalDate latestDate = photos.get(0).getLastModifiedDate();
            for (int i = 1; i < photos.size(); i++) {
                LocalDate date = photos.get(i).getLastModifiedDate();
                if (date != null && date.isAfter(latestDate)) {
                    latestDate = date;
                }
            }
            return latestDate;
        }
        return null;
    }
}
