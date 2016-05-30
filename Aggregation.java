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
	    
	    //String from = "BillingPayExecRq/CardAcctId/CustInfo/PersonInfo/PersonName/FirstName";
	    String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/NameBS";
	    //String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite*where NameVisible = КБК/AttributeLength/MinLength";
	    //String from = "BillingPayExecRq/RecipientRec/Requisites/Requisite/NameVisible";
	    
	    String to 	= "PERSONS/FIRST_NAME";
	    
	    String [] argFrom 	= from.split("/");
	    String [] argTo	 	= to.split("/");
	    
	    Element eElement = (Element) doc.getElementsByTagName(argFrom[0]).item(0);
	    
	    StructureForRecursion obj = new StructureForRecursion();
	    
	    downTo(eElement,argFrom,1,obj);
	    System.out.println(obj.vector.toString());
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
	
	
	
		private static void downTo(Element eElement, String[] arg, int i, StructureForRecursion obj){

			if (arg[i].indexOf("*") != -1) {
				obj = checkKey(eElement, arg[i], obj);
				eElement = obj.eElement;
				i++;
			}
			
			NodeList nList  = eElement.getElementsByTagName(arg[i]);
			int lengthList = nList.getLength();
			
			for (int k = 0; k < lengthList; k++) {
				eElement = (Element) nList.item(k);
				
				if (i < arg.length - 1)
						downTo(eElement, arg, i+1, obj);
				else
					obj.pullToArray(eElement.getTextContent());
			}
			return;
		}
		
		
		
		private static StructureForRecursion checkKey(Element el, String str, StructureForRecursion obj){
			
			int sep = str.indexOf("*");
			obj.eElement = el;

				String nameCurrentNode = str.substring(0, str.indexOf("*"));
				String nameCheckField = str.substring(str.indexOf("where ",sep) + 6,str.indexOf(" =",sep));
				String value = str.substring(str.indexOf("= ") + 2, str.length());
				obj.arg = nameCurrentNode;
				
				
				NodeList nList  = el.getElementsByTagName(nameCurrentNode);
				int lengthList = nList.getLength();
				for (int k = 0; k < lengthList; k++) {
					el = (Element) nList.item(k);

					if (el.getElementsByTagName(nameCheckField).item(0).getTextContent().equals(value)){

						obj.eElement = el;
						return obj;
					}
				}

				return obj;
			}

		
}

/* eElement = (Element) eElement.getElementsByTagName(argFrom[1]).item(0);
Element eElement = (Element) eElement.getElementsByTagName(argFrom[2]).item(0)getFirstChild().getTextContent();
eElement = (Element) eElement.getElementsByTagName(argFrom[3]).item(0);
eElement = (Element) eElement.getElementsByTagName(argFrom[4]).item(0);
eElement = (Element) eElement.getElementsByTagName(argFrom[5]).item(0);*/

//System.out.println(eElement.getTextContent());
//System.out.println(eElement.getFirstChild().getTextContent());

//eElement = downTo(eElement,argFrom,1,null);
 // Element [] elements = downTo(eElement,argFrom,1, new Element [50],0);
//System.out.println(argFrom[argFrom.length-1]);

//System.out.println(elements[0].getTextContent());
	//eElement = (Element) eElement.getElementsByTagName("CardAcctId").item(0);

//eElement = (Element) eElement.getElementsByTagName(arg[i]).item(0);

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
