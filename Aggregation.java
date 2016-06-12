import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
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

	public static void main(String[] args) throws Exception  {
		
		Connect connect 			= new Connect();
		StructureForRecursion obj 	= new StructureForRecursion();
		ResultSet r1 = getDataBase("select * from SBT1", connect.connectFrom);

		while (r1.next()){
			
			Document doc 	= getDocumentFromResultStream(r1, "EVENT_MSG");
		    String request 	= getRequestForConfigDB(doc);
		    ResultSet cfg 	= getDataBase(request, connect.connectCfg);

			while(cfg.next()){
				
				aggregation(cfg, doc, obj);
				
			}
			
		}
		exportToDB(connect, obj);
	    System.out.println("That is all");
	}
	
	//---------------------------------------------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------------------------------------------------
	
		private static void aggregation(ResultSet cfg, Document doc, StructureForRecursion obj) throws Exception{
			
			String from = cfg.getString("GO_FROM");
		    String to 	= cfg.getString("GO_TO");
		    String [] argFrom 	= from.split("/");
		    String [] argTo	 	= to.split(",");
		    Element eElement = (Element) doc.getElementsByTagName(argFrom[0]).item(0);

		    downTo(eElement,argFrom,1,obj);
		    obj.setInserts(argTo);
		    
		}
	
		private static String getRequestForConfigDB(Document doc){
			
			NodeList nList = doc.getElementsByTagName("MSG_BODY");
		    String MSG_TYPE = nList.item(0).getFirstChild().getNodeName();
		    String request = "select * from CONFIGURATIONS where MSG_TYPE = '" + MSG_TYPE + "'";
			
			return request;
		}
	
		private static Document getDocumentFromResultStream(ResultSet r1, String nameColumn) throws Exception  {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(r1.getString(nameColumn)));
		    Document doc = builder.parse(is);
			
			return doc;
		}
	
		private static ResultSet getDataBase(String sqlRequest, Connection connect) throws SQLException{
			
			PreparedStatement prSt;
		
			prSt = connect.prepareStatement(sqlRequest);  // where EVENT_ID = 256021319621");
			ResultSet resultSet =prSt.executeQuery();
			
			return resultSet;
		}
	
		private static void downTo(Element el, String[] arg, int i, StructureForRecursion obj){

			if (arg[i].indexOf("*") != -1) {
				specialRule(el, arg, i, obj);
				return;
			}
			
			NodeList nList  = el.getElementsByTagName(arg[i]);
			int lengthList = nList.getLength();
			
			if (lengthList == 0){
				obj.pullToArray("null");
				return;
			}
			
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

			if (lengthList == 0){
				obj.pullToArray("null");
				return;
			}
			boolean found = false;
			for (int k = 0; k < lengthList; k++) {
				el = (Element) nList.item(k);
				if (el.getElementsByTagName(nameCheckField).item(0).getTextContent().equals(value)){  //не предполагается, что есть больше одного
					downTo(el,arg,i+1,obj);
					found = true;
				}
			}
			if (!found)
				obj.pullToArray("null");
			
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
			
			if (itemInt > lengthList){
				obj.pullToArray("null");
				return;
			}
				
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
			if (nList.getLength() == 0){
				obj.pullToArray("null");
				return;
			}
			
			el = (Element) nList.item(0);
			
			if (subString.indexOf(",") > 0){
				
				int subIntBegin = Integer.parseInt(subString.substring(0, subString.indexOf(",")));
				int subIntLength = Integer.parseInt(subString.substring(subString.indexOf(",") + 1, subString.length()));
				
				if ((subIntBegin > el.getTextContent().length()) || (el.getTextContent().length() < subIntLength + subIntBegin)){
					subIntBegin = 1;
					subIntLength = el.getTextContent().length();
				}

				obj.pullToArray(el.getTextContent().substring(subIntBegin - 1, subIntBegin+subIntLength-1));

			}
			
			if (subString.indexOf(",") < 0){
				
				int subIntLength = Integer.parseInt(subString.substring(0, subString.length()));
				if (el.getTextContent().length() < subIntLength)
					subIntLength = el.getTextContent().length();
				obj.pullToArray(el.getTextContent().substring(0, subIntLength));

			}		
			
		}

		private static void exportToDB(Connect connect, StructureForRecursion obj){
			
			PreparedStatement stTo;
			try {	
					for (int i = 0; i < obj.requests.capacity(); i++){
						for (int j = 0; j < obj.requests.get(i).capacity(); j++){
							stTo = connect.connectTo.prepareStatement(obj.requests.get(i).get(j));
							System.out.println(obj.requests.get(i).get(j));
							//stTo.executeQuery();
						}
					}
		    } catch (SQLException e) {
		    	e.printStackTrace();
			}

		}

}



//String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*item()/NameVisible";
//String to = "PERSONS/LAST_NAME, AAA/FIRST_NAME";
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



