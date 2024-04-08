package photos.models;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class handles the Photo object.
 *
 * @author Rayaan Afzal
 * @author Junaid Ghani
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The file path of a photo to use.
     */
    private transient Path filePath;

    /**
     * The file path to String to use.
     */
    private String filePathToString;

    /**
     * The last modified date to use.
     */
    private LocalDate lastModifiedDate;

    /**
     * The caption of a photo to use.
     */
    private String caption;

    /**
     * The tags associated with a photo to use.
     */
    private Map<String, Set<String>> tags;

    /**
     * The constructor that creates the Photo object.
     *
     * @param filePath The file path of the photo.
     */
    public Photo(Path filePath) {
        this.filePath = filePath;
        this.filePathToString = filePath.toString();
        this.tags = new HashMap<>();
        this.caption = "";
        try {
            BasicFileAttributes attrs = java.nio.file.Files.readAttributes(filePath, BasicFileAttributes.class);
            this.lastModifiedDate = LocalDate.ofInstant(attrs.lastModifiedTime().toInstant(), java.time.ZoneId.systemDefault());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the last modified date of a photo.
     *
     * @return the last modified date.
     */
    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Gets the file path of a photo
     *
     * @return returns the Path of a photo.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Gets the file path to String of a photo.
     *
     * @return the file path to String.
     */
    public String getFilePathToString() { return filePathToString; }

    /**
     * Sets the file path of a photo.
     */
    public void setFilePath() {
        filePath = Paths.get(filePathToString);
    }

    /**
     * Gets the caption of a photo.
     *
     * @return String caption of a photo.
     */
    public String getCaption() {
        return caption != null ? caption : "";
    }

    /**
     * Sets the caption of a photo.
     *
     * @param caption The caption to set.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Adds a tag to a photo.
     *
     * @param tagName The tag type associated with the photo.
     * @param tagValue The tag value associated with the tag type.
     * @return Returns true if the tag was successfully added.
     */
    public boolean addTag(String tagName, String tagValue) {
        DataSave.TagTypeConstraint constraint = DataSave.getTagTypes().getOrDefault(tagName, DataSave.TagTypeConstraint.MULTIPLE);

        if (constraint == DataSave.TagTypeConstraint.SINGLE) {
            // If it's a single value tag type and already exists, don't add the new value.
            if (this.tags.containsKey(tagName) && !this.tags.get(tagName).contains(tagValue)) {
                return false; // Indicate failure to add because it violates the single instance rule.
            }
            this.tags.put(tagName, new HashSet<>(Collections.singleton(tagValue)));
        } else {
            // For MULTIPLE, just add the new value.
            this.tags.computeIfAbsent(tagName, k -> new HashSet<>()).add(tagValue);
        }
        return true;
    }

    /**
     * Removes a tag from a photo.
     *
     * @param tagName  The tag type associated with the photo.
     * @param tagValue The tag value associated with the tag type.
     */
    public void removeTag(String tagName, String tagValue) {
        if (this.tags.containsKey(tagName)) {
            boolean removed = this.tags.get(tagName).remove(tagValue);
            if (removed) {
                // If the tag value was successfully removed and now the set is empty, remove the tag name as well.
                if (this.tags.get(tagName).isEmpty()) {
                    this.tags.remove(tagName);
                }
            }
        }
    }

    /**
     * Gets the tag type/value pairs of a photo in a neat String format.
     *
     * @return String of tag type/value pairs.
     */
    public String getFormattedTags() {
        // Assuming tags is a Map<String, Set<String>>
        if (tags.isEmpty()) {
            return "";
        } else {
            return tags.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * Gets the date of a photo in String form.
     *
     * @return String of date of photo.
     */
    public String getFormattedCaptureDate() {
        if (lastModifiedDate != null) {
            return lastModifiedDate.format(DateTimeFormatter.ofPattern("dd MMMM uuuu"));
        }
        return "No Date";
    }

    /**
     * Gets the tags associated with a photo.
     *
     * @return Map of tags.
     */
    public Map<String, Set<String>> getTags() {
        return tags;
    }

    /**
     * Determines if a photo contains a certain tag.
     *
     * @param selectedType The tag type it has.
     * @param tagValue The tag value it has.
     * @return Returns true if it has that tag type/value pair, false otherwise.
     */
    public boolean hasTag(String selectedType, String tagValue) {
        Set<String> values = tags.get(selectedType);
        return values != null && values.contains(tagValue);
    }
}
