package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The {@code Dictionary} class manages a thread-safe dictionary of words and their meanings.
 * It supports loading from and saving to a file, as well as querying and modifying dictionary entries.
 */
public class Dictionary {
    // Thread-safe map storing words and their meanings
    private ConcurrentMap<String, Set<String>> dictionary;
    private final String filepath;
    private final Object fileLock = new Object();


    /**
     * Constructs a new Dictionary with the specified file path.
     *
     * @param filepath The path to the dictionary file.
     */
    public Dictionary(String filepath){
        this.filepath = filepath;
        dictionary = new ConcurrentHashMap<>();
    }

    /**
     * Loads the dictionary from the file specified during construction.
     * Each line in the file should have the format: word,meaning1,meaning2,...
     *
     * @throws IOException If the file cannot be read.
     */
    protected void loadInitialDictionary() throws IOException {
        dictionary = new ConcurrentHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            String line;

            // Read and process each line in the dictionary file
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] tokens = line.split(",");

                if (tokens.length < 2) {
                    continue; // Skip lines without meanings
                }

                String word = tokens[0].trim().toLowerCase();
                Set<String> meanings = new HashSet<>();

                for (int i = 1; i < tokens.length; i++) {
                    String meaning = tokens[i].trim();
                    if (!meaning.isEmpty()) {
                        meanings.add(meaning);
                    }
                    // Store the word with its set of meanings
                    dictionary.put(word, meanings);
                }
            }
        }
    }


    /**
     * Saves the current dictionary contents to the file.
     *
     * @throws IOException If the file cannot be written.
     */
    protected void saveToFile() throws IOException {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
                for (Map.Entry<String, Set<String>> entry : dictionary.entrySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(entry.getKey());
                    for (String meaning : entry.getValue()) {
                        sb.append(",").append(meaning);
                    }
                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Returns the set of meanings for a given word.
     *
     * @param word The word to query.
     * @return A set of meanings, or {@code null} if the word is not found.
     */
    protected Set<String> query(String word) {
        if (word == null) {
            return null;
        }
        return dictionary.get(word.toLowerCase());
    }

    /**
     * Adds a new word with its meanings to the dictionary.
     *
     * @param word     The word to add.
     * @param meanings The meanings associated with the word.
     * @return {@code true} if the word was added; {@code false} if it already exists.
     */
    protected boolean addWord(String word, Set<String> meanings) {
        if (word == null || meanings == null || meanings.isEmpty()) {
            throw new IllegalArgumentException("Word and meanings must not be null or empty.");
        }
        String key = word.toLowerCase();

        // Only add if the word doesn't already exist
        return dictionary.putIfAbsent(key, new CopyOnWriteArraySet<>(meanings)) == null;
    }


    /**
     * Removes a word from the dictionary.
     *
     * @param word The word to remove.
     * @return {@code true} if the word was removed; {@code false} if it was not found.
     */
    protected boolean removeWord(String word) {
        if (word == null) return false;
        return dictionary.remove(word.toLowerCase()) != null;
    }

    /**
     * Adds a new meaning to an existing word.
     *
     * @param word    The word to which the meaning is to be added.
     * @param meaning The new meaning to add.
     * @return {@code true} if the meaning was added; {@code false} if the word is not found or the meaning already exists.
     */
    protected boolean addMeaning(String word, String meaning) {
        if (word == null || meaning == null || meaning.trim().isEmpty()) {
            throw new IllegalArgumentException("Word and meaning must not be null or empty.");
        }
        Set<String> meanings = dictionary.get(word.toLowerCase());
        if (meanings != null) {
            synchronized (meanings){
                return meanings.add(meaning); // returns false if meaning already exists
            }
        }
        return false;
    }



    /**
     * Updates a meaning of a word by replacing the old meaning with a new one.
     *
     * @param word       The word whose meaning is to be updated.
     * @param oldMeaning The existing meaning to be replaced.
     * @param newMeaning The new meaning to replace the old one.
     * @return {@code true} if the update was successful; {@code false} otherwise.
     */
    protected boolean updateMeaning(String word, String oldMeaning, String newMeaning) {
        if (word == null || oldMeaning == null || newMeaning == null ||
                oldMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: word and meanings must not be null or empty.");
        }
        Set<String> meanings = dictionary.get(word.toLowerCase());

        synchronized (meanings){
            if (meanings != null && meanings.contains(oldMeaning)) {
                meanings.remove(oldMeaning);
                meanings.add(newMeaning);
                return true;
            }
        }
        return false;
    }
}

