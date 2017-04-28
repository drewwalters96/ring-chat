/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {
    private boolean connected = true;
    private BufferedReader inStream;
    private PrintWriter outStream;
    private User user;

    public Client(Socket clientSocket) throws IOException {
        this.inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.outStream = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void notify(String message) {
        outStream.println(message);
    }

    private void processInput(String input) {

        StringBuilder message = new StringBuilder();

        // Split input string into args
        String[] args = input.split("\\s");

        // Parse input and process request
        switch (args[0]) {
            case "login":
                // Get username, pass and login
                String username = args[1];
                String password = args[2];

                // CAUTION: returns null if login failed.
                if ((user = ChatServer.login(username, password)) == null) {
                    notify("Username or password incorrect.");
                }
                else {
                    notify("Welcome, " + user.getUserId() + "! You have been successfully logged in.");
                }
                break;
            case "send":
                if (user != null) {
                    if (args[1] == "all") {
                        // Get message from input
                        for (String arg : args) {
                            message.append(arg);
                        }

                        // Broadcast message
                        ChatServer.broadcastMessage(message.toString());
                    }
                    else {
                        // Get username to send message to
                        String userId = args[1];

                        // Get message from input
                        for (String arg : args) {
                            message.append(arg);
                        }

                        ChatServer.sendMessage(userId, message.toString());
                    }
                } else {
                    notify("You must be logged in to send messages.");
                }
                break;
            case "who":
                if (user != null) {
                    // Get users online
                    ArrayList<User> users = ChatServer.getOnlineUsers();

                    // Create response containing online users to send to client
                    StringBuilder response = new StringBuilder("Online users:");
                    for (User onlineUser : users) {
                        response.append(" " + onlineUser.getUserId());
                    }

                    notify(response.toString());
                }
                else {
                    notify("You must be logged in to see a list of online users.");
                }
                break;

            case "logout":
                // Chat server removes client
                ChatServer.logout(this);

                // Stop the thread
                stop();
                break;
            default:
                notify("Invalid input. Please try again.");
                break;
        }
    }

    public String getUserId() {
        return user.getUserId();
    }

    public User getUser() { return user; }

    public void run() {
        String input;

        try {
            while (connected) {
                // Get input from the client
                if ((input = inStream.readLine()) != null) {

                    // Process client input
                    processInput(input);
                }
            }

        } catch (IOException e) {
            outStream.println("Invalid input. Please try again.");
        }
    }

    public void stop() {
        connected = false;
    }
}
