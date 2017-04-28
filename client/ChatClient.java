/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import com.sun.media.jfxmedia.logging.Logger;
import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

public class ChatClient {
    private static String host;
    private static Integer port;
    private static Socket socket;
    private static BufferedReader inStream;
    private static PrintWriter outStream;

    public static void startClient() {
        // Load server configuration
        loadConfiguration();

        // Establish server connection
        establishServerConnection();

        System.out.println("Welcome to Ring-Chat!\n");
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            // Listen for user input and server responses
            while (true) {
                try {
                    // Get server input
                    if (inStream.ready()) {
                        System.out.println(inStream.readLine());
                    }

                    // Get user input and send to server for processing
                    if (userInput.ready()) {
                        outStream.println(userInput);
                    }

                    System.out.print("> ");
                } catch (IOException e) {
                    System.out.println("[ERROR]: The connection to the server has been interrupted.");
                } finally {
                    closeServerConnection();
                }
            }
        } catch (IOException ioe) {
            Logger.logMsg(Level.WARNING.intValue(), ioe.getMessage());
        }
    }


    private static void loadConfiguration() {
        Properties config = new Properties();

        // Load server config file
        try (FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "/config.properties")) {

            config.load(is);

            // Get config info
            host = config.getProperty("SERVER_HOST");
            port = Integer.parseInt(config.getProperty("SERVER_PORT"));
        } catch (IOException ioe) {
            System.out.println("[Ring-Chat]: There is an error in your client configuration. Please update config.properties.");
            Logger.logMsg(Level.SEVERE.intValue(), ioe.getMessage());
            System.exit(1);
        }
    }

    private static void  establishServerConnection() {

        // Establish connection to server with socket and get in/out streams
        try {
            socket = new Socket(host, port);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("[Ring-Chat]: A connection could not be made to the specified server.");
            closeServerConnection();
            System.exit(1);
        }

    }

    private static void closeServerConnection() {
        // Close socket and input/output streams
        try {
            socket.close();
            inStream.close();
            outStream.close();
        } catch (IOException ioe) {
            Logger.logMsg(Level.WARNING.intValue(), ioe.getMessage());
        }
    }
}