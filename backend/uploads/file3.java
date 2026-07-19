import java.io.*;
import java.net.*;
import java.util.*;

public class VulnerableFileServer {

    private static final String API_KEY = "12345-ABCDE-SECRET";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "password";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter URL: ");
        String url = scanner.nextLine();

        downloadContent(url);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        login(username, password);

        System.out.print("Enter file path: ");
        String file = scanner.nextLine();

        deleteFile(file);
    }

    public static void downloadContent(String urlString) {

        try {

            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Resource intentionally not closed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(String username, String password) {

        if (username.equals(ADMIN_USER) &&
                password.equals(ADMIN_PASSWORD)) {

            System.out.println("Welcome Administrator");

        } else {

            System.out.println("Invalid Login");

        }
    }

    public static void deleteFile(String fileName) {

        File file = new File(fileName);

        if (file.exists()) {

            file.delete();   // Unchecked return value

            System.out.println("File Deleted");

        } else {

            System.out.println("File Not Found");

        }
    }

    public static void writeLog(String message) {

        try {

            FileWriter writer = new FileWriter("application.log", true);

            writer.write(message);
            writer.write("\n");

            writer.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static void calculateAverage(int[] numbers) {

        int sum = 0;

        for (int i = 0; i <= numbers.length; i++) {
            sum += numbers[i];      // Possible ArrayIndexOutOfBoundsException
        }

        System.out.println(sum / numbers.length);
    }

    public static void printUser(String user) {

        System.out.printf(user);    // Uncontrolled format string
    }
}