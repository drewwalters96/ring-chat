/*
 * Created by Andrew Walters for CS4850 at the University of Missouri.
 *
 * 4/28/2017
 *
 * Ring-Chat is a CLI client-server chat program that utilizes the
 * the Socket API.
 */

public class RingServer {
	public static void main(String[] args) {
		try {
		    // Start chat server
			ChatServer.start();
		} catch (Exception ex) {
			System.out.println("[ERROR]: Server could not be started. Please make sure config.properties exists and the specified port is unbound.");
		} finally {
		    ChatServer.stop();
        }
	}
}
