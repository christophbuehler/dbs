import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.github.javafaker.Faker;
import oracle.jdbc.driver.*;

public class Database {
	private Env env;
	private boolean isConnected = false;
	private Connection con;

	private final int PERSON_COUNT = 10000;
	private String[][] persons = new String[PERSON_COUNT][3]; // nachname, vorname
	
	
	
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
		try {
			genFakeData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		insertPosts();
	}

	public void insertPosts() throws SQLException {
		PreparedStatement stmtPost = con.prepareStatement("INSERT INTO post (hauptautor_id, sprache_kuerzel, uni_id, datum, ist_live, likes) VALUES (?, 'de', ?, ?, ?, 0, 0)");
		PreparedStatement stmtRev = con.prepareStatement("INSERT INTO revision (vorname, nachname, dob) VALUES (?, ?, ?)");
		
		
		con.setAutoCommit(false);

		for(String[] person : persons) {
			stmt.setString(1, person[0]);
			stmt.setString(2, person[1]);
			stmt.setDate(3, Date.valueOf(person[2]));
			stmt.executeUpdate();
			con.commit();
		}
	}
	
	public void insertPersonen() throws SQLException {
		PreparedStatement stmt = con.prepareStatement("INSERT INTO person (vorname, nachname, dob) VALUES (?, ?, ?)");
		con.setAutoCommit(false);

		for(String[] person : persons) {
			stmt.setString(1, person[0]);
			stmt.setString(2, person[1]);
			stmt.setDate(3, Date.valueOf(person[2]));
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
	
	private void genFakeData() throws ParseException {
		Faker faker = new Faker(new Locale("de-DE"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date from = dateFormat.parse("1960-01-01");
		java.util.Date to = dateFormat.parse("2007-01-01");
		
		for (int i=0; i<PERSON_COUNT; ++i) {
			persons[i] = new String[] {
				faker.name().firstName(),
				faker.name().lastName(),
				dateFormat.format(faker.date().between(from, to))
			};
		}

	}
}
