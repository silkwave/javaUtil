import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        // Create a Map
        Map<String, Object> ctxMap = new HashMap<>();
        Map<String, Object> data = new HashMap<>();        

        // Use put method to add key-value pairs to the map
        ctxMap.put("A", Arrays.asList(1, 2, 3));
        ctxMap.put("B", "Hello");
        ctxMap.put("C", 123);

        // Print initial values to verify
        System.out.println("Initial values:");
        ctxMap.forEach((key, value) -> {
            System.out.println("Value for key '" + key + "': " + value);
        });

        // Add more key-value pairs to the map
        ctxMap.put("D", true);
        ctxMap.put("E", Arrays.asList("Apple", "Banana", "Cherry"));

        // Print updated values to verify
        System.out.println("\nUpdated values:");
        ctxMap.forEach((key, value) -> {
            System.out.println("Value for key '" + key + "': " + value);
        });

        // Add the 'data' map to the 'map'
        data.put("a1", "a1");
        data.put("a2", 22);        
        data.put("a3", Arrays.asList(1, 2, 3));      
        ctxMap.put("data", data);  

        // Print the 'data' map using forEach
        System.out.println("\nValue for key 'data': ");
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) ctxMap.get("data");
        dataMap.forEach((key, value) -> {
            System.out.println("    " + key + ": " + value);
        });
    }
}
