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

		st = connect.connectFrom.prepareStatement("select * from SBT1 "); //where EVENT_ID = 256021319621");
		ResultSet r1 =st.executeQuery();
		//r1.next();
		while (r1.next()){
			
			boolean isReqKnown = false;
			
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(r1.getString("EVENT_MSG")));
		    Document doc = builder.parse(is);
		    
		    NodeList nList = doc.getElementsByTagName("MSG_BODY");
		    String MSG_TYPE = nList.item(0).getFirstChild().getNodeName();
		    
		    String request = "select * from CONFIGURATIONS where MSG_TYPE = '" + MSG_TYPE + "'";
		    System.out.println(request+ "  ");
		    stCfg = connect.connectCfg.prepareStatement(request);
		    ResultSet cfg =stCfg.executeQuery();
		    cfg.next();
		    StructureForRecursion obj = new StructureForRecursion();

				while(cfg.next()){
					
					isReqKnown = true;
					//String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*item()/NameBS";
			    	String from = cfg.getString("GO_FROM");
				    String to 	= cfg.getString("GO_TO");
				    String [] argFrom 	= from.split("/");
				    String [] argTo	 	= to.split("/");
				    
				    Element eElement = (Element) doc.getElementsByTagName(argFrom[0]).item(0);
				    downTo(eElement,argFrom,1,obj);
				    //obj.prepareRow(argTo);
				    obj.setInserts(argTo);
				   // System.out.println(obj.request.get(0)[1]);
				    
				    
			   }
				//exportToDB(connect, obj, isReqKnown, MSG_TYPE);
		}
	    System.out.println("That is all");
	}
	
		private static void downTo(Element el, String[] arg, int i, StructureForRecursion obj){

			if (arg[i].indexOf("*") != -1) {
				specialRule(el, arg, i, obj);
				return;
			}
			
			NodeList nList  = el.getElementsByTagName(arg[i]);
			int lengthList = nList.getLength();
			for (int k = 0; k < lengthList; k++) {
				el = (Element) nList.item(k);
				if (i < arg.length - 1)
					downTo(el, arg, i+1, obj);
				else
					obj.pullToArray(el.getTextContent());
			}
			return;
		}
		

		private static void specialRule(Element el, String[] arg, int i, StructureForRecursion obj){
			
			int sep = arg[i].indexOf("*");

			String ruleName = arg[i].substring(sep + 1, sep + 5);
			
			if (ruleName.indexOf("(")>0)
				ruleName=ruleName.substring(0,ruleName.indexOf("("));
			
			switch (ruleName) {
				case "wher":
					ruleWhere(el, arg, i, obj);
					break;
				case "item":
					ruleItem(el, arg, i, obj);
					break;
				case "subs":
					ruleSubstring(el, arg, i, obj);
					break;
			}
			
		}
		
		private static void ruleWhere(Element el, String[] arg, int i, StructureForRecursion obj){
			String str = arg[i];
			int sep = arg[i].indexOf("*");
			
			String nameCurrentNode = str.substring(0, str.indexOf("*")).replaceAll(" ", "");
			String nameCheckField = str.substring(str.indexOf("where ",sep) + 6,str.indexOf(" =",sep)).replaceAll(" ", "");
			String value = str.substring(str.indexOf("= ") + 2, str.length());
			
			NodeList nList  = el.getElementsByTagName(nameCurrentNode);
			int lengthList = nList.getLength();
			for (int k = 0; k < lengthList; k++) {
				el = (Element) nList.item(k);
				if (el.getElementsByTagName(nameCheckField).item(0).getTextContent().equals(value)){  //не предполагается, что есть больше одного
					downTo(el,arg,i+1,obj);
				}
			}
			return;
		}
		
		private static void ruleItem(Element el, String[] arg, int i, StructureForRecursion obj){
			
			String str = arg[i];
			int sep = arg[i].indexOf("*");
			int itemInt = 1;
			
			String nameCurrentNode = str.substring(0, str.indexOf("*"));
			String itemString = str.substring(str.indexOf("(") + 1, str.indexOf(")")).replaceAll(" ", "");
			if (itemString.length() != 0)
				itemInt = Integer.parseInt(itemString);
			NodeList nList  = el.getElementsByTagName(nameCurrentNode);
			int lengthList = nList.getLength();
			
			if (itemString.length() != 0){
				el = (Element) nList.item(itemInt - 1);
				downTo(el,arg,i+1,obj);
			}
			
			if (itemString.length() == 0){
				for (int k = 0; k < lengthList; k++) {
					el = (Element) nList.item(k);
					downTo(el,arg,i+1,obj);
				}
			}

		}
		
		private static void ruleSubstring(Element el, String[] arg, int i, StructureForRecursion obj){ // применима только для конечных узлов
			
			String str = arg[i];
			int sep = arg[i].indexOf("*");
			int itemSub = 1;
			
			String nameCurrentNode = str.substring(0, str.indexOf("*"));
			String subString = str.substring(str.indexOf("(") + 1, str.indexOf(")")).replaceAll(" ", "");
			NodeList nList  = el.getElementsByTagName(nameCurrentNode);
			el = (Element) nList.item(0);
			
			if (subString.indexOf(",") > 0){
				
				int subIntBegin = Integer.parseInt(subString.substring(0, subString.indexOf(",")));
				int subIntLength = Integer.parseInt(subString.substring(subString.indexOf(",") + 1, subString.length()));
				
				obj.pullToArray(el.getTextContent().substring(subIntBegin - 1, subIntBegin+subIntLength));

			}
			
			if (subString.indexOf(",") < 0){
				
				int subIntLength = Integer.parseInt(subString.substring(0, subString.length()));
				obj.pullToArray(el.getTextContent().substring(0, subIntLength));

			}		
			
		}
		
		private static void exportToDB(Connect connect, StructureForRecursion obj, boolean isReqKnown, String MSG_TYPE){
			
			PreparedStatement stTo;
			try {
					if (!isReqKnown){
						stTo = connect.connectTo.prepareStatement("insert into UNKNOWN_REQ (REQUEST) values ('" + MSG_TYPE + "')");
					   	stTo.executeQuery();
					}
					if (isReqKnown){
						stTo = connect.connectTo.prepareStatement(obj.request.get(0)[1]);
						System.out.println(obj.request.get(0)[1]);
						stTo.executeQuery();
					}
		    } catch (SQLException e) {
				
		    	e.printStackTrace();
			}

		}

}
//Node node = doc.getChildNodes().item(0);
//NodeList children = node.getChildNodes();

//stTo = connect.connectTo.prepareStatement("insert into PERSONS (MSG_TYPE, LAST_NAME, FIRST_NAME, MIDDLE_NAME, CARD_NUM) values ('qqq', 5, 'eee', 'rrr', 08)");
//stTo.executeQuery();
// тут будет функция по получению правила по обработке соощения из конфигов. допустим получили::

//String from = "BillingPayExecRq/CardAcctId/CustInfo/PersonInfo/PersonName/FirstName";
//String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/NameBS";
//String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/AttributeLength/MinLength";
// String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite/NameVisible";

// String to 	= "PERSONS/FIRST_NAME";



