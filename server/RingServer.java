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

		/* This is used for testing purposes */
		User.register("Tom", "Tom11");

		// Create chat server
		ChatServer server = new ChatServer();

		try {
			server.startServer();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
