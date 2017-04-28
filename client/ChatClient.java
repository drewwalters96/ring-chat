/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private static String host;
    private static Integer port;
    private static BufferedReader inStream;
    private static PrintWriter outStream;

    public static void displayMenu() {
        System.out.println("Ring-Chat options:\n\tlogin <UserID> <Password>\tlog in to the chat server\n\tsend all <message>\t\tsend message to every online user\n\tsend <UserID> <message>\t\tsend message to an online user\n\twho\t\t\t\tdisplay a list of online users\n\tlogout\t\t\t\tlog out of the server\n\n");
    }

    private static void  establishServerConnection() throws IOException {

        // Establish connection to server with socket and get in/out streams
        Socket socket = new Socket(host, port);
        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outStream = new PrintWriter(socket.getOutputStream(), true);
    }

    private static void loadConfiguration() throws IOException {
        Properties config = new Properties();

        // Load server config file
        FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "/config.properties");
        config.load(is);

        // Get config info
        host = config.getProperty("SERVER_HOST");
        port = Integer.parseInt(config.getProperty("SERVER_PORT"));

        is.close();
    }

    public static void sendRequest(String input) {
        outStream.println(input);
    }

    public static void startClient() {
        try {
            // Load server configuration
            loadConfiguration();
        } catch (FileNotFoundException fne) {
            System.out.println("[Ring-Chat]: Client configuration not found. Please make sure config.properties exists.");
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("[Ring-Chat]: There is an error in your client configuration. Please update config.properties.");
            System.exit(1);
        }

        try {
            // Establish server connection
            establishServerConnection();
        } catch (IOException ioe) {
            System.out.println("[Ring-Chat]: A connection could not be established with the specified chat server. Please make sure config.properties is correct.");
            System.exit(1);
        }

        // Display menu and listen for input
        System.out.println("Welcome to Ring-Chat!\n");
        displayMenu();
        Scanner sc = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        while (true) {
            try {
                if (inStream.ready()) {
                    System.out.print("> ");
                    System.out.println(inStream.readLine());
                }
            } catch (IOException e) {
                System.out.println("[Ring-Chat]: The connection to the server has been interrupted. Please try sending your request again.");
            }

            while (sc.hasNext()) {
                input.append(sc.next());
            }

            // Send request to server for processing
            sendRequest(input.toString());
        }
    }
}