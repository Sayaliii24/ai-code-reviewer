import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class VulnerableApp {

    static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    static final String USER = "root";
    static final String PASS = "password123"; // Hardcoded password

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        login(username, password);

        readFile(scanner.nextLine());

        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("ping " + username); // Command Injection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(String username, String password) {

        try {

            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            Statement stmt = conn.createStatement();

            // SQL Injection
            String query = "SELECT * FROM users WHERE username='"
                    + username
                    + "' AND password='"
                    + password
                    + "'";

            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                System.out.println("Login Successful");
            } else {
                System.out.println("Invalid Credentials");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Information Disclosure
        }
    }

    public static void readFile(String filename) {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(filename)); // Path Traversal

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}