import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connect {

	public static void main(String[] args) throws SQLException {
		
		System.out.println("-------- Oracle JDBC Connection Testing ------");

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;

		}

		System.out.println("Oracle JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:SBTPRACTICE", "esblog",
					"qwe123");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		PreparedStatement st = connection.prepareStatement("select * from SBT1 where EVENT_ID = 256021319621");
	    ResultSet r1 =st.executeQuery();

	     r1.next();
	     System.out.println( r1.getString(2));
		
		/*DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md. //select * from SBT1 where EVENT_ID=256021319621;
		while (rs.next()) {
		  System.out.println(rs.getString("EVENT_ID"));
		}*/
		System.out.println("That is all");
	}
		
}
