public class Main {
	public static void main(String[] args) {
		Env env = new Env("../univie.env");
		Database db = new Database(env);

		try {
			db.connect();
			db.removeAll();
			db.insertAll();
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
