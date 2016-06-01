import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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
		PreparedStatement stTo;
		PreparedStatement stCfg;

		st = connect.connectFrom.prepareStatement("select * from SBT1 where EVENT_ID = 256021319621");
		//stTo = connect.connectTo.prepareStatement("insert into PERSONS (MSG_TYPE, LAST_NAME, FIRST_NAME, MIDDLE_NAME, CARD_NUM) values ('qqq', 5, 'eee', 'rrr', 08)");
		//stTo.executeQuery();
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
	    
	    String request = "select * from CONFIGURATIONS where MSG_TYPE = '" + MSG_TYPE + "'";
	    System.out.println(request+ "  ");
	    stCfg = connect.connectCfg.prepareStatement(request);
	    ResultSet cfg =stCfg.executeQuery();
	    
	    StructureForRecursion obj = new StructureForRecursion();
	   
	    while(cfg.next()){
	    	
	    	String from = cfg.getString("GO_FROM");
		    String to 	= cfg.getString("GO_TO");
		    String [] argFrom 	= from.split("/");
		    String [] argTo	 	= to.split("/");
		    
		    Element eElement = (Element) doc.getElementsByTagName(argFrom[0]).item(0);

		    downTo(eElement,argFrom,1,obj);
		    
		    //obj.prepareRow(argTo);
		    
		    //exportToDB(connect, obj.vector, argTo);
		    
		    //System.out.println(obj.vector.toString());
		    System.out.print(obj.getArrayNodeNames());
		    System.out.println(obj.getArrayText());
	   }
		    
	   
	    
	    
	    

	    
	    // тут будет функция по получению правила по обработке соощения из конфигов. допустим получили::
	    
	    //String from = "BillingPayExecRq/CardAcctId/CustInfo/PersonInfo/PersonName/FirstName";
	    //String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/NameBS";
	    //String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/AttributeLength/MinLength";
	    // String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite/NameVisible";
	    
	   // String to 	= "PERSONS/FIRST_NAME";
	    
	    
	    
	    
	    System.out.println("That is all");

	}	
		
		private static void downTo(Element eElement, String[] arg, int i, StructureForRecursion obj){

			if (arg[i].indexOf("*") != -1) {
				eElement = checkKey(eElement, arg[i]);
				i++;
			}
			
			NodeList nList  = eElement.getElementsByTagName(arg[i]);
			int lengthList = nList.getLength();
			
			for (int k = 0; k < lengthList; k++) {
				eElement = (Element) nList.item(k);
				
				if (i < arg.length - 1)
					downTo(eElement, arg, i+1, obj);
				else
					obj.pullToArray(eElement);
			}
			return;
		}
		

		private static Element checkKey(Element el, String str){
			
			int sep = str.indexOf("*");

				String nameCurrentNode = str.substring(0, str.indexOf("*"));
				String nameCheckField = str.substring(str.indexOf("where ",sep) + 6,str.indexOf(" =",sep));
				String value = str.substring(str.indexOf("= ") + 2, str.length());
				
				NodeList nList  = el.getElementsByTagName(nameCurrentNode);
				int lengthList = nList.getLength();
				for (int k = 0; k < lengthList; k++) {
					el = (Element) nList.item(k);

					if (el.getElementsByTagName(nameCheckField).item(0).getTextContent().equals(value)){
						return el;
					}
				}

				return el;
			}
		
		private static void exportToDB(Connect connect, Vector<Element> vector, String[] argTo){
			
			String request = "insert into " + argTo[0] + " (";
			for (int i = 1; i < argTo.length; i++)
				request = request + argTo[i] + ", ";
			
			PreparedStatement stTo;
			/*try {
				stTo = connect.connectTo.prepareStatement("insert into PERSONS (MSG_TYPE, LAST_NAME, FIRST_NAME, MIDDLE_NAME, CARD_NUM) values ('qqq', 5, 'eee', 'rrr', 08)");
				stTo.executeQuery();
			} catch (SQLException e) {
				System.out.println("ExportToDB: Something happened.. Data did not record in the DB ");
				e.printStackTrace();
			}*/


			
		}

}
