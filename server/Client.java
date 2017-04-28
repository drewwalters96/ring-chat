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

    public void run() {
        String input;

        try {
            while (connected) {
                // Get input from the client
                if ((input = inStream.readLine()) != null) {

                    // Process client input
                    ChatServer.processInput(this, input);
                }
            }

        } catch (IOException e) {
            outStream.println("[Error]: Invalid input. Please try again.");
        }
    }

    public void stop() {
        // Thread will end safely
        connected = false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
