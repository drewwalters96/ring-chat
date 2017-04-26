/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{
    private Socket clientSocket;
    private BufferedReader inStream;
    private PrintWriter outStream;

    public Client(Socket clientSocket) throws IOException {
        this.clientSocket= clientSocket;
        this.inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.outStream = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void run() {
        StringBuilder input = new StringBuilder();
        try {
            String line;

            // Get all input from the client
            while ((line = inStream.readLine()) != null) {
                input.append(line);
            }

            // Process input on main thread
            Platform.runLater(() -> {
                ChatServer.processInput(input.toString());
            });
        } catch (IOException e) {
            return;
        }
    }
}
