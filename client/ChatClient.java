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
    private static boolean connected = true;
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
            while (connected) {
                try {
                    // Get server input
                    if (inStream.ready()) {
                        String response = inStream.readLine();

                        // Verify server has not closed connection
                        if (!response.equals("CONNECTION_TERMINATED")) {
                            // Display server response
                            System.out.println(response);
                            System.out.print("> ");
                        } else {
                            connected = false;
                        }
                    }

                    // Get user input and send to server for processing
                    if (userInput.ready()) {
                        outStream.println(userInput.readLine());
                        String response = inStream.readLine();
                        System.out.print("> ");
                        if (response == null) {
                          System.out.println("[ERROR]: The connection to the server has been interrupted.");
                          break;
                        }
                        else if (!response.equals("ACK")) {
                          System.out.println(response);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("[ERROR]: The connection to the server has been interrupted.");
                }
            }

            // Close connections and exit
            closeServerConnection();
            System.out.println("Connection closed.");
            System.exit(0);
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
        } catch (Exception e) {
            System.out.println("[Ring-Chat]: There is an error in your client configuration. Please update config.properties.");
            Logger.logMsg(Level.SEVERE.intValue(), e.getMessage());
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
            System.out.println("[Ring-Chat]: A connection could not be made to the specified server. Please make sure it is online.");
            closeServerConnection();
            System.exit(1);
        }

    }

    private static void closeServerConnection() {
        // Close socket and input/output streams
        try {
            if (socket != null) {
                socket.close();
            }

            if (inStream != null) {
                inStream.close();
            }

            if (outStream != null) {
                outStream.close();
            }
        } catch (IOException ioe) {
            Logger.logMsg(Level.WARNING.intValue(), ioe.getMessage());
        }
    }
}
