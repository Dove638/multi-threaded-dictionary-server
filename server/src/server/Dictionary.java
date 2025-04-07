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

public class Dictionary {
    // Underlying storage: a map from lower-case words to a set of meanings
    private ConcurrentMap<String, Set<String>> dictionary;
    private final String filepath;

    // Constructors
    public Dictionary() {
        this.filepath = "";
        dictionary = new ConcurrentHashMap<>();
    }

    public Dictionary(String filepath){
        this.filepath = filepath;
        dictionary = new ConcurrentHashMap<>();
    }

    public Dictionary(ConcurrentMap<String, Set<String>> dictionary){
        this.filepath = "";
        this.dictionary = dictionary;
    }

    protected void loadInitialDictionary() throws IOException {
        dictionary = new ConcurrentHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            String line;

            // Read each non-empty line
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] tokens = line.split(",");

                if (tokens.length < 2) { // No meaning is provided
                    continue;
                }

                // The first token is the word,
                String word = tokens[0].trim().toLowerCase();
                Set<String> meanings = new HashSet<>();

                // Add each token as a meaning
                for (int i = 1; i < tokens.length; i++) {
                    String meaning = tokens[i].trim();
                    if (!meaning.isEmpty()) {
                        meanings.add(meaning);
                    }
                    dictionary.put(word, meanings);
                }
            }
        }
    }

    public void saveToFile() throws IOException {
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


    // Query the meanings of a word; returns null if not found.
    public Set<String> query(String word) {
        if (word == null) return null;
        return dictionary.get(word.toLowerCase());
    }

    // Add a new word with its meanings.
    // Returns true if the word was added, false if it already exists.
    public boolean addWord(String word, Set<String> meanings) {
        if (word == null || meanings == null || meanings.isEmpty()) {
            throw new IllegalArgumentException("Word and meanings must not be null or empty.");
        }
        String key = word.toLowerCase();
        // putIfAbsent returns null if the word was absent, meaning insertion succeeded.
        return dictionary.putIfAbsent(key, new CopyOnWriteArraySet<>(meanings)) == null;
    }

    // Remove a word from the dictionary.
    // Returns true if the word was removed; false if it was not found.
    public boolean removeWord(String word) {
        if (word == null) return false;
        return dictionary.remove(word.toLowerCase()) != null;
    }

    // Add an additional meaning to an existing word.
    // Returns true if the meaning was added, false if the word is not found or the meaning already exists.
    public boolean addMeaning(String word, String meaning) {
        if (word == null || meaning == null || meaning.trim().isEmpty()) {
            throw new IllegalArgumentException("Word and meaning must not be null or empty.");
        }
        Set<String> meanings = dictionary.get(word.toLowerCase());
        if (meanings != null) {
            return meanings.add(meaning);
        }
        return false;
    }

    // Update an existing meaning with a new one.
    // Returns true if the update was successful, false otherwise.
    public boolean updateMeaning(String word, String oldMeaning, String newMeaning) {
        if (word == null || oldMeaning == null || newMeaning == null ||
                oldMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: word and meanings must not be null or empty.");
        }
        Set<String> meanings = dictionary.get(word.toLowerCase());
        if (meanings != null && meanings.contains(oldMeaning)) {
            meanings.remove(oldMeaning);
            meanings.add(newMeaning);
            return true;
        }
        return false;
    }


}

