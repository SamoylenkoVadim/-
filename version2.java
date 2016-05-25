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
	
	public static String parser(String str1, String str2){
		
		String answer 	= null;
		String check 	= null;
		
		int start 	= str1.indexOf(str2) + str2.length() + 1;
		int end 	= str1.indexOf("/" + str2, start) - 1;
		
		answer = str1.substring(start, end); //java parsers
		/**
		 * что такое DOM
		 * org.w3c.*
		 * javax.parsers...
		 */
		
		return answer;
	}

	public static void main(String[] args) throws SQLException, ParserConfigurationException, SAXException, IOException {
		
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
				
			r1.next(); //какое-то изменение
			
			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    InputSource is = new InputSource(new StringReader(r1.getString("EVENT_MSG")));
			    Document doc = builder.parse(is);
			    
			    doc.getDocumentElement().normalize();
		         System.out.println("Root element :" 
		            + doc.getDocumentElement().getNodeName());

	    
			String message = "MsgId"; //DataItem
	     /*
		    System.out.println("Parser is ready:");
		    System.out.println("<"+ message + ">");
		    System.out.println(parser(r1.getString(2), message)); 
		    System.out.println("</"+ message + ">");
	     
	 */
		System.out.println("That is all");
	}
		
}

//System.out.println( r1.getString(2));

	/*DatabaseMetaData md = connection.getMetaData();
	ResultSet rs = md. //select * from SBT1 where EVENT_ID=256021319621;
	while (rs.next()) {
	  System.out.println(rs.getString("EVENT_ID"));
	}*/