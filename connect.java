import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import oracle.jdbc.OracleResultSet;

public class Connect {
	
	public Connection connectFrom 	= null;
	public Connection connectCfg	= null;
	public Connection connectTo		= null;
	
	public Connect(){
		
		findDriver();
		setConnection();
		
	}
	
	private void findDriver(){
		
		System.out.println("-------- Oracle JDBC Connection Testing ------");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
		}
		System.out.println("Oracle JDBC Driver Registered!");
		
	}
	
	private void setConnection(){
		
		try {
			connectFrom = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:SBTPRACTICE", "esblog",
					"qwe123");
			connectCfg = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:SBTPRACTICE", "esblog_cfg",
					"qwe123");
			connectTo = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:SBTPRACTICE", "esblog_new",
					"qwe123");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if ((connectFrom != null)&&(connectCfg != null)) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		System.out.println("Connect:: All connections with Oracle DB is established successfully!");
		
	}

		
}


