import java.util.Vector;
import java.util.function.Consumer;

import org.w3c.dom.Element;
	
	public class StructureForRecursion {
	
		Vector<String> vNodeName	= new Vector<String>(0,1);
		Vector<String> vText		= new Vector<String>(0,1);
		Vector<String[]> request	= new Vector<String[]>(0,1);
	
		public void pullToArray(Element el){
			 
			 vNodeName.addElement(el.getNodeName());
			 vText.addElement(el.getTextContent());
			 
		}
		
		public String getArrayText(){
			
			return vText.toString();
			
		}
		
		public String getArrayNodeNames(){
			
			return vNodeName.toString();
			
		}
/*		
		public void prepareRow(String[] to){
			boolean flag = false;
			int i = 0;
			String[] curr;
			
			for (i = 0; i < request.capacity(); i++)
				if (request.get(i)[0] == to[0]){
					flag = true;
					break;
				}
					
			
			if (flag)
				curr = request.get(i);
				
			
		}*/
		
	
	}
