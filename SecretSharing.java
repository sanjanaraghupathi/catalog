import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SecretSharing {

    public static void main(String[] args) {
        try {
            // Read JSON input
            String json = readJson("input.json");
            System.out.println("JSON Input: " + json); // Debug print

            // Get the roots from the JSON
            Map<Integer, Long> roots = getRoots(json);
            // Print the roots
            System.out.println("Decoded Roots: " + roots);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readJson(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private static Map<Integer, Long> getRoots(String json) {
        Map<Integer, Long> roots = new HashMap<>();

        // Extract n and k from the JSON
        int n = extractValue(json, "n");
        int k = extractValue(json, "k");

        // Debugging print for n and k
        System.out.println("Extracted n: " + n + ", k: " + k); // Debug print

        // Loop through the provided keys and decode values
        for (int i = 1; i <= n; i++) {
            String base = extractBase(json, i);
            String value = extractValueFromRoot(json, i);

            // Decode the value only if base and value are not empty
            if (base != null && value != null) {
                try {
                    long decodedValue = decodeValue(base, value);
                    roots.put(i, decodedValue);
                } catch (NumberFormatException e) {
                    System.err.println("Failed to decode value for index " + i + " with base " + base + " and value " + value);
                }
            }
        }
        return roots;
    }

    private static int extractValue(String json, String key) {
        // Adjust the search string to look inside the "keys" object
        String searchString = "\"keys\": {";
        int keysStartIndex = json.indexOf(searchString) + searchString.length();
        int keyStartIndex = json.indexOf("\"" + key + "\":", keysStartIndex) + key.length() + 3; // +3 for the '":'
        
        int endIndex = json.indexOf(",", keyStartIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", keyStartIndex);
        }
        
        String value = json.substring(keyStartIndex, endIndex).trim();

        // Debugging print for the value being parsed
        System.out.println("Parsing value for key \"" + key + "\": " + value); // Debug print

        // Check if value is a valid integer before parsing
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for key " + key + ": " + value);
            return -1;  // Return a default or error value
        }
    }

    private static String extractBase(String json, int index) {
        String searchString = "\"" + index + "\": {";
        int startIndex = json.indexOf(searchString) + searchString.length();
        int endIndex = json.indexOf(",", startIndex);

        String base = json.substring(startIndex, endIndex).trim();
        String[] parts = base.split("\"");
        if (parts.length > 3) {
            return parts[3].trim(); // The base should be the 3rd part in the split
        }
        return null; // Return null if base could not be found
    }

    private static String extractValueFromRoot(String json, int index) {
        String searchString = "\"" + index + "\": {";
        int startIndex = json.indexOf(searchString) + searchString.length();
        int endIndex = json.indexOf(",", startIndex);
        
        String value = json.substring(startIndex, endIndex).trim();
        String[] parts = value.split("\"");
        if (parts.length > 5) {
            return parts[5].replaceAll("[}]", "").trim(); // Remove trailing '}'
        }
        return null; // Return null if value could not be found
    }

    private static long decodeValue(String base, String value) {
        // Trim the value to remove any extra spaces or characters
        value = value.trim();

        // Convert the base from String to an integer
        int radix;
        try {
            radix = Integer.parseInt(base);
        } catch (NumberFormatException e) {
            System.err.println("Invalid base value: " + base);
            return -1; // Return an error code
        }

        // Convert the value to a long
        return Long.parseLong(value, radix);
    }
}
