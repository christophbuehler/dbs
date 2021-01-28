public class Main {
	public static void main(String[] args) {
		Database db = new Database();
		try {
			db.connect();
			db.insertAll();
			db.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
