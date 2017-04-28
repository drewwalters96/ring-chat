/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

import java.io.FileInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Properties;

public class ChatServer {
	private static InetAddress host;
	private static int port;
	private static int timeout;
	private static int maxClients;
    private static boolean serverOnline = false;

	private static ArrayList<Client> clients;

	public static void broadcastMessage(String message) {
	    // Send message to every client
	    for (Client client : clients) {
            client.notify(message);
        }
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
            if (user.getUserId() == userId && user.getPassword() == password) {
                return user; // User is added to Client
            }
        }
        return null; // CAUTION: returns null if login failed
    }

    public static void logout(Client client) {
	    // Remove client from active clients list
	    clients.remove(client);
    }

    public static boolean sendMessage(String userId, String message) {

	    // Send message to user if they are online
	    for (Client client : clients) {
	        if (client.getUserId() == userId) {
	            client.notify(message);
	            return true;
            }
            else {
	            return false;
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

	public static void startServer() throws Exception {
		// Load server configuration
		loadConfiguration();

		// Create server socket and update status
        ServerSocket serverSocket = new ServerSocket(port, timeout, host);
        serverOnline = true;

        // Accept clients while server is online
        clients = new ArrayList<>();
        while (serverOnline && clients.size() <= maxClients) {
            Client client = new Client(serverSocket.accept());
            clients.add(client);
            new Thread(client).start();

            client.notify("[Ring-Chat]: Connection to server established. Please log in.");
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
