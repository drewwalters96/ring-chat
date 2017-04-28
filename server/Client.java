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
                try {
                    // Verify client is not already logged in
                    if (user != null) {
                        notify("[Error]: You are already logged in");
                        break;
                    }

                    // Get username, pass and login
                    String username = args[1];
                    String password = args[2];

                    System.out.println(username + password);
                    // CAUTION: returns null if login failed.
                    if ((user = ChatServer.login(username, password)) == null) {
                        notify("Username or password incorrect.");
                    }
                    else {
                        notify("Welcome, " + user.getUserId() + "! You have been successfully logged in.");
                    }
                } catch (Exception e) {
                    notify("Invalid input. Please try again.");
                }

                break;
            case "send":
                try {
                    if (user != null) {
                        // Append userId
                        message.append("[" + user.getUserId() + "]:");

                        // Get message from input
                        for (int i = 2; i < args.length; i++) {
                            message.append(" ");
                            message.append(args[i]);
                        }

                        // Determine if the message is for another user or everyone
                        if (args[1].equals("all")) {

                            // Broadcast message
                            ChatServer.broadcastMessage(message.toString());
                        }
                        else {
                            // Get username to send message to
                            String userId = args[1];

                            // Send message to user
                            if (!ChatServer.sendMessage(userId, message.toString())) {
                                notify("[Error]: " + userId + " is not online");
                            }
                        }
                    } else {
                        notify("[Error]: You must be logged in to send messages.");
                    }
                } catch (Exception e) {
                    notify("[Error]: Invalid input. Please try again.");
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
                    notify("[Error]: You must be logged in to see a list of online users.");
                }
                break;

            case "logout":
                // Chat server removes client
                ChatServer.logout(this);

                // Stop the thread
                stop();
                break;
            default:
                notify("[Error]: Invalid input. Please try again.");
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
            outStream.println("[Error]: Invalid input. Please try again.");
        }
    }

    public void stop() {
        connected = false;
    }
}
