import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Aggregation {

	public static void main(String[] args) throws SQLException, ParserConfigurationException, SAXException, IOException {
		
		Connect connect = new Connect();
		
		PreparedStatement st;

		st = connect.connectFrom.prepareStatement("select * from SBT1 where EVENT_ID = 256021319621");
		ResultSet r1 =st.executeQuery();
	
		r1.next();
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(r1.getString("EVENT_MSG")));
	    Document doc = builder.parse(is);
	    
	    Node node = doc.getChildNodes().item(0);
	    NodeList children = node.getChildNodes();
	    
	    for (int i = 0; i < children.getLength(); i++) {
	         Node current = children.item(i);
	         if (current.getNodeName() == "MSG_BODY"){
	        	 current = current.getFirstChild();
	        	 System.out.println(current.getNodeName());
	         }
	    }

        
        System.out.println("That is all");

	}
	
}
