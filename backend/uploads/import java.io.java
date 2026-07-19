import java.io.*;
import java.util.*;
import java.security.MessageDigest;

public class InsecureUserManager {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final String SECRET_KEY = "my-secret-key";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        authenticate(username, password);

        System.out.print("Enter file to save log: ");
        String file = scanner.nextLine();

        saveLog(file, username);

        System.out.println("Password Hash: " + md5(password));
    }

    public static void authenticate(String username, String password) {

        if (password.equals(ADMIN_PASSWORD)) {
            System.out.println("Admin Login Successful");
        } else {
            System.out.println("Access Denied");
        }
    }

    public static void saveLog(String filename, String username) {

        try {

            FileWriter writer = new FileWriter(filename, true);

            writer.write("User Logged In: " + username + "\n");

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String md5(String input) {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] bytes = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            return "";
        }
    }

    public static void divideNumbers(int a, int b) {

        try {

            int result = a / b;

            System.out.println(result);

        } catch (Exception e) {

            // Empty catch block

        }
    }

    public static void copyFile(String source, String destination) {

        try {

            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            while (fis.read(buffer) != -1) {
                fos.write(buffer);
            }

            // Streams intentionally not closed

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}