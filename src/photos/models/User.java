package photos.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles User accounts.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The username of the user to use.
     */
    private String username;

    /**
     * The albums associated with a user to use.
     */
    private List<Album> albums;

    /**
     * Constructor for creating a new User.
     *
     * @param username The username of the user.
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    /**
     * Gets the username of a user.
     *
     * @return the username of a user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of a user.
     *
     * @param newUser The new username
     */
    public void setUsername(String newUser) {
        username = newUser;
    }

    /**
     * Gets all the albums associated with the user.
     *
     * @return List of albums.
     */
    public List<Album> getUserAlbums() {
        return albums;
    }

    /**
     * Adds an album to the list of albums.
     *
     * @param album The album to add.
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }
}
