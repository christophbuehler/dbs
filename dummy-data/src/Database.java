import java.sql.*;
import oracle.jdbc.driver.*;

public class Database {
	private String connStr;
	private String uname;
	private String pwd;
	private boolean isConnected = false;
	private Connection con;
	
	public Database(String connStr, String uname, String pwd) {
		this.connStr = connStr;
		this.uname = uname;
		this.pwd = pwd;
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// String dburl = "jdbc:oracle:thin:@oracle-lab.cs.univie.ac.at:1521:lab";
		// String dburl = "jdbc:oracle:thin:@vier.cs.univie.ac.at:1521:o10g ";
		this.con = DriverManager.getConnection(this.connStr, this.uname, this.pwd);
		this.isConnected = true;
	}
	
	public void disconnect() throws Exception {
		this.con.close();
	}
	
	public void insertAll() throws Exception {
		if (!this.isConnected) throw new Exception("Client not connected.");
		this.insertSprachen();
	}

	public void insertSprachen() throws SQLException {
		Statement stmt = con.createStatement();
		String sql = "INSERT INTO sprache VALUES ('de', 0, 'deutsch', 'UTF-8')";
		stmt.executeUpdate(sql);
		
		/*ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String konr = rs.getString("konr");
			System.out.println(konr + " " + rs.getInt("kontostand"));*/
	}
}
