package photos.models;

import photos.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the management of users.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class UserManager {

    /**
     * The directory where users are stored.
     */
    private static final String USERS_FILE = "src/photos/models/users.txt";

    /**
     * The list of users to use.
     */
    private static List<User> users = new ArrayList<>();

    /**
     * Loads the users from disk.
     */
    static {
        loadUsers();
    }

    /**
     * Populates the users list with the users from users.txt
     */
    public static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String username = line.trim();
                User user = new User(username);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all users.
     *
     * @return users list
     */
    public static List<User> getUsers() {
        return users;
    }

    /**
     * Adds a user to the list.
     *
     * @param user The user to add
     */
    public static void addUser(User user) {
        users.add(user);
    }

}
