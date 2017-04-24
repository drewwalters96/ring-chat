import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RingServer {
	public static void main(String[] args) {

		Properties config = new Properties();

		try {
			// Load server config file
			FileInputStream is = new FileInputStream("ring-chat/config.properties");
			config.load(is);

			// Create server
			ChatServer server = new ChatServer(config.getProperty("SERVER_HOST"), Integer.parseInt(config.getProperty("SERVER_PORT")), Integer.parseInt(config.getProperty("SERVER_TIMEOUT")), Integer.parseInt(config.getProperty("MAX_CLIENTS")));
		} catch (IOException ex) {
			System.out.println("There was a problem loading the config file. Please make sure \"config.properties\" exists.");
		}
	}
}
