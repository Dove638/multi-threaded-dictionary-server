package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CSVDictionaryLoader implements DictionaryLoader{
    @Override
    public ConcurrentMap<String, Set<String>> loadInitialDictionary(String filepath) throws IOException {
        ConcurrentMap<String, Set<String>> dictionary = new ConcurrentHashMap<>();

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
        return dictionary;
    }
}
