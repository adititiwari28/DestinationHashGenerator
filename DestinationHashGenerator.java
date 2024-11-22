import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <jar-file> <roll-number> <json-file-path>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", ""); // Ensure lowercase, no spaces
        String jsonFilePath = args[1];

        try {
            // Step 1: Parse the JSON file
            File jsonFile = new File(jsonFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Step 2: Traverse JSON to find the first "destination" key
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Step 3: Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Step 4: Concatenate and generate MD5 hash
            String inputString = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(inputString);

            // Step 5: Output result
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method to recursively find the first occurrence of "destination"
    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
        // Iterate through fields in the JSON object
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            // Check if the key is "destination"
            if (field.getKey().equals("destination")) {
                return field.getValue().asText();
            }
            // Recursive call for nested nodes
            String value = findDestinationValue(field.getValue());
            if (value != null) {
                return value;
            }
        }
    } else if (node.isArray()) {
        // If the node is an array, iterate through its elements
        for (JsonNode element : node) {
            String value = findDestinationValue(element);
            if (value != null) {
                return value;
            }
        }
    }
        return null;
    }

    // Method to generate an MD5 hash
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    // Method to generate a random alphanumeric string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomString.append(chars.charAt(random.nextInt(chars.length())));
        }
        return randomString.toString();
    }
}