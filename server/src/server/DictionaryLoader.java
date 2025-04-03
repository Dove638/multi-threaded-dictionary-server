package server;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


public interface DictionaryLoader {
    ConcurrentMap<String, Set<String>> loadInitialDictionary(String filepath) throws Exception;
}
