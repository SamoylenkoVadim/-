import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

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
	    
	    NodeList nList = doc.getElementsByTagName("MSG_BODY");
	    String MSG_TYPE = nList.item(0).getFirstChild().getNodeName();
	    
	    
	    // тут будет функция по получению правила по обработке соощения из конфигов. допустим получили::
	    
	    String from = "BillingPayExecRq/CardAcctId/CustInfo/PersonInfo/PersonName/FirstName";
	    String to 	= "PERSONS/FIRST_NAME";
	    
	    String [] argFrom 	= from.split("/");
	    String [] argTo	 	= to.split("/");
	    System.out.println(argFrom.length);
	    
	    Element eElement = (Element) doc.getElementsByTagName(argFrom[0]).item(0);
	    
	   /* eElement = (Element) eElement.getElementsByTagName(argFrom[1]).item(0);
	    Element eElement = (Element) eElement.getElementsByTagName(argFrom[2]).item(0)getFirstChild().getTextContent();
	    eElement = (Element) eElement.getElementsByTagName(argFrom[3]).item(0);
	    eElement = (Element) eElement.getElementsByTagName(argFrom[4]).item(0);
	    eElement = (Element) eElement.getElementsByTagName(argFrom[5]).item(0);*/
	    
	    //System.out.println(eElement.getTextContent());
	    //System.out.println(eElement.getFirstChild().getTextContent());
	    
	    eElement = downTo(eElement,argFrom,1);
	    
	    System.out.println(eElement.getTextContent());
	   	//eElement = (Element) eElement.getElementsByTagName("CardAcctId").item(0);
	    
        
        System.out.println("That is all");

	}
	
	private static Node search(NodeList children, String arg){
		
		Node	current = null;
		for (int i = 0; i < children.getLength(); i++) {
	         current = children.item(i);
	         if (current.getNodeName() == arg)
	        	 return current;
	    }
		return null;
	}
	
	
	private static String getValue(String[] arg){
		
		return null;
	}
	
	private static Element downTo(Element eElement, String[] arg, int i){
			
		eElement = (Element) eElement.getElementsByTagName(arg[i]).item(0);
		if (i < arg.length - 1)
			eElement = downTo(eElement,arg,i+1);
		return eElement;
		
	}
	
}

/* 
for (int i = 0; i < children.getLength(); i++) {
     Node current = children.item(i);
     if (current.getNodeName() == "MSG_BODY"){
    	 current = current.getFirstChild();
    	 System.out.println(current.getNodeName());
     }
}

Node current = null;
for (int i = 0; i < children.getLength(); i++) {
     current = children.item(i);
     if (current.getNodeName() == "MSG_BODY"){
    	 System.out.println("BODY");
    	 break;
    	 }
}

NodeList List = current.getChildNodes();

for (int i = 0; i < List.getLength(); i++) {
     current = List.item(i);
     if (current.getNodeName() == "BillingPayExecRq")
    	 System.out.println("EXEC!!!");
}*/

/*NodeList list = search(children, "MSG_BODY").getChildNodes();
Node one = search(list,"BillingPayExecRq");
System.out.println(one.getFirstChild().getTextContent());*/

//System.out.println(search(list,"SPName").get);


//NodeList nList = doc.getElementsByTagName("BillingPayExecRq").item(0).getChildNodes();



//System.out.println(eElement.getElementsByTagName("CardAcctId").item(0).getFirstChild().getNodeName());

//System.out.println(nList.item(0).getNodeName());

