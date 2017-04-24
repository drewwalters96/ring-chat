import java.io.*;
import java.util.ArrayList;

class User {
	private String userId;
	private String password; // CHANGE TO hash
	private static ArrayList<User> users = null;
	private static final String USER_FILE = "users.bin";

	public User(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}

	public static ArrayList<User> getUsers() {
		if (users == null) {
			loadUsers();
		}

		return users;
	}

	public static void loadUsers() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		users = new ArrayList<>();

		try {
			File file = new File(USER_FILE);
			file.createNewFile();
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);

			User user;
			while ((user = (User)ois.readObject()) != null) {
				users.add(user);
			}
		} catch (FileNotFoundException e) {
			// no registered users
		} catch (IOException e) {
			System.out.println("There was an error loading the registered users.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveUsers() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			File file = new File(USER_FILE);
			file.createNewFile();
			fos = new FileOutputStream(file,false);
			oos = new ObjectOutputStream(fos);

			for (User user : users) {
				oos.writeObject(user);
			}
		} catch (IOException e) {
			System.out.println("There was an error saving the registered users.");
		} finally {
			close(oos);
			close(fos);
		}
	}

	private static void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				System.out.println("There was an error saving the registered users.");
			}
		}
	}
}
