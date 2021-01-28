import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Env {
	private HashMap<String, String> data = new HashMap<String, String>();

	public String get(String key) {
		return this.data.get(key);
	}

	public Env(String path) {
		this.read(path);
	}

	private void read(String path) {
		try {
			File myObj = new File(path);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				String[] parts = line.split("=");
				this.data.put(parts[0], parts[1]);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
