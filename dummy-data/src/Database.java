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

	private final int UNI_COUNT = 3;
	private int[] uni_ids = new int[UNI_COUNT];
	private final int POST_COUNT = 100;
	private final int AUTOR_COUNT = 30;
	private int[] autor_ids = new int[AUTOR_COUNT];
	private final int PERSON_COUNT = 100;
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
		insertAutoren();
		insertAutorHatArtikel();
		insertPosts();
	}

	public void insertPosts() throws SQLException, ParseException {
		Faker faker = new Faker(new Locale("de-DE"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date from = dateFormat.parse("2020-08-01");
		java.util.Date to = dateFormat.parse("2021-01-28");

		PreparedStatement stmtPost = con.prepareStatement("INSERT INTO post (hauptautor_id, sprache_kuerzel, uni_id, datum, ist_live, likes) VALUES (?, 'de', ?, ?, 0, ?)", new String[] { "id" });
		PreparedStatement stmtRev = con.prepareStatement("INSERT INTO revision (post_id, uni_id, titel, inhalt) VALUES (?, ?, ?, ?)");

		con.setAutoCommit(false);
		
		for (int i=0; i<POST_COUNT; ++i) {
			
			int uniId = uni_ids[faker.number().numberBetween(0, UNI_COUNT - 1)];
			
			// post
			stmtPost.setInt(1, autor_ids[faker.number().numberBetween(0, AUTOR_COUNT - 1)]); // hauptautor_id
			stmtPost.setInt(2, uniId); // uni_id
			Date date = Date.valueOf(dateFormat.format(faker.date().between(from, to)));
			stmtPost.setDate(3, date); // datum
			stmtPost.setInt(4, faker.number().numberBetween(0, 1000)); // likes
			
			stmtPost.executeUpdate();
			
			try (ResultSet generatedKeys = stmtPost.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	stmtRev.setInt(1, (int)generatedKeys.getLong(1)); // post_id
	            }
	            else {
	                throw new SQLException("Creating post failed, no ID obtained.");
	            }
	        }
			
			// revision
			stmtRev.setInt(2, uniId); // uni_id
			stmtRev.setString(3, faker.beer().malt()); // titel
			stmtRev.setString(4, faker.chuckNorris().fact()); // inhalt
			stmtRev.executeUpdate();
			
			con.commit();
		}
	}
	
	public void insertAutoren() throws SQLException {
		Faker faker = new Faker(new Locale("de-DE"));
		PreparedStatement stmt = con.prepareStatement("INSERT INTO autor (uni_id, kuerzel) VALUES (?, ?)", new String[] { "id" });
		con.setAutoCommit(false);
		
		for (int i=0; i<AUTOR_COUNT; ++i) {
			stmt.setInt(1, uni_ids[faker.number().numberBetween(0, UNI_COUNT - 1)]);
			stmt.setString(2, faker.name().username());
			stmt.executeUpdate();
			
			// get gen ids
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	autor_ids[i] = (int)generatedKeys.getLong(1);
	            }
	            else {
	                throw new SQLException("Creating autor failed, no ID obtained.");
	            }
	        }
			
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
		PreparedStatement stmt = con.prepareStatement("INSERT INTO universitaet (name, studierende) VALUES (?, 0)", new String[] { "id" });
		con.setAutoCommit(true);
		int i = 0;
		
		for(String uni : unis) {
			stmt.setString(1, uni);
			int affectedRows = stmt.executeUpdate();
			// con.commit();
			
			if (affectedRows == 0) {
				throw new SQLException("Creating uni failed, no rows updated.");
			}
			
			// get gen ids
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	uni_ids[i] = (int)generatedKeys.getLong(1);
	            } else {
	                throw new SQLException("Creating uni failed, no ID obtained.");
	            }
	        }
			
			
			
			++i;
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
