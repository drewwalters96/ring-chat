import java.net.*;
import java.util.ArrayList;

class ChatServer {
	private InetAddress host;
	private int port;
	private int timeout;
	private int maxClients;

	private ArrayList<User> users;

	public ChatServer(String host, int port, int timeout, int maxClients) {
		try {
			this.host = InetAddress.getByName(host);
		} catch (UnknownHostException ex) {
			System.out.println("The specified host is invalid. Please update in \"config.properties\".");
		}
		this.timeout = timeout;
		this.port = port;
		this.maxClients = maxClients;
	}

	public void startServer() {
		try {
			// Read registered users from file
			User.loadUsers();

			// Open server socket and listen for client
			ServerSocket serverSocket = new ServerSocket(port, timeout, host);
			Socket clientSocket = serverSocket.accept();

		} catch (SecurityException securityEx) {
			System.out.println("ERROR: A security policy is preventing the server from starting.");
		} catch (IllegalArgumentException iaEx) {
			System.out.println("ERROR: The specified port number is invalid. Please choose an inactive port number between 0 and 65535.");
		} catch (SocketTimeoutException stEx) {
			System.out.println("ERROR: A timeout occured.");
		} catch (Exception ex) {
			System.out.println("An error occured while starting the chat server.");
		}

	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getMaxClients() {
		return maxClients;
	}
}
