import java.sql.*;
import java.util.Locale;

import com.github.javafaker.Faker;
import oracle.jdbc.driver.*;

public class Database {
	private Env env;
	private boolean isConnected = false;
	private Connection con;

	private final int PERSON_COUNT = 10000;
	private String[][] persons = new String[PERSON_COUNT][2]; // nachname, vorname
	private String[] unis = { "Univie", "WU", "TU" };
	private String[] tables = {
		"autor_hat_post",
		"autor_hat_sprache",
		"revision",
		"post",
		"autor",
		"mitarbeiter",
		"person",
		"universitaet",
		"sprache"
	};

	public Database(Env env) {
		this.env = env;
		genFakeData();
	}

	public void removeAll() throws SQLException {
		for(String table : tables) {
			Statement stmt = con.createStatement();
			String sql = "DELETE FROM " + table;
			stmt.executeUpdate(sql);
		}
	}

	public void insertAll() throws Exception {
		if (!isConnected)
			throw new Exception("Client not connected.");
		insertSprachen();
		insertUnis();

		insertPersonen();

		insertAutorHatArtikel();
	}

	public void insertPersonen() throws SQLException {
		PreparedStatement stmt = con.prepareStatement("INSERT INTO person (vorname, nachname) VALUES (?, ?)");
		con.setAutoCommit(false);

		for(String[] person : persons) {
			stmt.setString(1, person[0]);
			stmt.setString(2, person[1]);
			stmt.executeUpdate();
			con.commit();
		}
	}

	public void insertSprachen() throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "INSERT INTO sprache VALUES ('de', 0, 'deutsch', 'UTF-8')";
		stmt.executeUpdate(sql);
	}

	public void insertUnis() throws SQLException {
		PreparedStatement stmt = con.prepareStatement("INSERT INTO universitaet (name, studierende) VALUES (?, 0)");
		con.setAutoCommit(false);
		
		for(String uni : unis) {
			stmt.setString(1, uni);
			stmt.executeUpdate();
			con.commit();
		}
	}

	public void insertAutorHatArtikel() throws SQLException { }

	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection(env.get("conn_str"), env.get("uname"), env.get("pwd"));
		isConnected = true;
	}

	public void disconnect() throws Exception {
		con.close();
		isConnected = false;
	}
	
	private void genFakeData() {
		Faker faker = new Faker(new Locale("de-DE"));

		for (int i=0; i<PERSON_COUNT; ++i) {
			persons[i] = new String[] {
				faker.name().firstName(),
				faker.name().lastName(),
			};
		}

	}
}
