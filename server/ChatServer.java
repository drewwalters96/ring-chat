/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Properties;

public class ChatServer {
	private static InetAddress host;
	private static int port;
	private static int timeout;
	private static int maxClients;
    private static boolean serverOnline = false;

    private static ServerSocket serverSocket;

	private static ArrayList<Client> clients;

	public static void broadcastMessage(String message) {

	    // Send message to every client
	    for (Client client : clients) {
            client.notify(message);
        }

        // Log in server console
        System.out.println(message);
    }

    public static ArrayList<User> getOnlineUsers() {
	    ArrayList<User> onlineUsers = new ArrayList<>();

	    for (Client client : clients) {
	        onlineUsers.add(client.getUser());
        }

        return onlineUsers;
    }

    public static User login(String userId, String password) {

	    // Verify use information is correct
        for (User user : User.getUsers()) {
            if (user.getUserId().equals(userId) && user.getPassword().equals(password)) {
                broadcastMessage(userId + " logged in");
                return user; // User is added to Client
            }
        }
        return null; // CAUTION: returns null if login failed
    }

    public static void logout(Client client) {
	    // Remove client from active clients list
	    clients.remove(client);

	    // Log action
        broadcastMessage(client.getUserId() + " logged out");
    }

    public static boolean sendMessage(String userId, String message) {

	    // Check for userId in list of clients
	    for (Client client : clients) {
	        if (client.getUserId().equals(userId)) {

                // Send message to user if they are online
	            client.notify(message);

	            // Log the action in the server
                System.out.println(message + " --> " + userId);
	            return true;
            }
        }
        return false;
    }

	private static void loadConfiguration() throws Exception {
		Properties config = new Properties();

		// Load server config file
		FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "/server/config.properties");
		config.load(is);

		host = InetAddress.getByName(config.getProperty("SERVER_HOST"));
		port = Integer.parseInt(config.getProperty("SERVER_PORT"));
		timeout = Integer.parseInt(config.getProperty("SERVER_TIMEOUT"));
		maxClients = Integer.parseInt(config.getProperty("MAX_CLIENTS"));

		is.close();
	}

	public static void start() throws Exception {
	    System.out.println("[Ring-Chat]: Starting server...");
		// Load server configuration
		loadConfiguration();

		// Create server socket and update status
        serverSocket = new ServerSocket(port, timeout, host);
        serverOnline = true;
        System.out.println("[Ring-Chat]: Server listening on " + host + ":" + port);

        // Accept clients while server is online
        clients = new ArrayList<>();
        while (serverOnline && clients.size() <= maxClients) {
            Client client = new Client(serverSocket.accept());
            clients.add(client);
            new Thread(client).start();

            client.notify("[Ring-Chat]: Connection to server established. Please log in.");
        }
	}

    public static void stop() {
        serverOnline = false;

        try {
            // Safely stop remaining client threads
            for (Client client : clients) {
                client.stop();
            }

            // Unbind socket
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("[Ring-Chat]: Server did not shutdown successfully.");
        }
    }

	public static InetAddress getHost() {
		return host;
	}

	public static int getPort() {
		return port;
	}

	public static int getMaxClients() {
		return maxClients;
	}
}
