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
        System.out.println("Ring-Chat options:\n    login <UserID> <Password>\n    send all <message>\n    send UserId message\n who\n logout");
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
        FileInputStream is = new FileInputStream("ring-chat/config.properties");
        config.load(is);

        // Get config info
        host = config.getProperty("SERVER_HOST");
        port = Integer.parseInt(config.getProperty("SERVER_PORT"));
    }

    public static void sendRequest(String input) {
        outStream.println(input);
    }

    public static void startClient() {
        try {
            // Load server configuration
            loadConfiguration();
        } catch (IOException ioe) {
            System.out.println("There is an error in your client configuration. Please update config.properties and make sure it exists.");
        }

        try {
            // Establish server connection
            establishServerConnection();
        } catch (IOException ioe) {
            System.out.println("A connection could not be established with the chat server. Please make sure config.properties is correct.");
        }

        // Display menu and listen for input
        displayMenu();
        Scanner sc = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        while (true) {
            try {
                if (inStream.ready()) {
                    System.out.println(inStream.readLine());
                }
            } catch (IOException e) {
                System.out.println("The connection to the server has been interrupted. Please try sending your request again.");
            }

            while (sc.hasNext()) {
                input.append(sc.next());
            }

            // Send request to server for processing
            sendRequest(input.toString());
        }
    }
}