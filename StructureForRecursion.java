import java.util.Vector;
import java.util.function.Consumer;

import org.w3c.dom.Element;
	
	public class StructureForRecursion {
	
		Vector<String> vNodeName	= new Vector<String>(0,1);
		Vector<String> vText		= new Vector<String>(0,1);
		Vector<String[]> request	= new Vector<String[]>(0,1);
		Vector<String> inserts		= new Vector<String>(0,1);
		Vector<Vector>	vector		= new Vector<Vector>(0,1);
		String newStr 				= null;
	
		public void pullToArray(String text){

			 vText.addElement(text);
			 
		}
		
		public String getArrayText(){
			
			String str = vText.toString();
			vText.removeAllElements(); 
			return str.substring(1, str.length()-1);
			
			
		}
				
		
		public void setInserts(String[] argTo){
			
			for (int i = 0; i < argTo.length; i++){
				argTo[i] = argTo[i].replaceFirst(" ","");
				String [] arrayTo = argTo[i].split("/");
				
				inserts(arrayTo);
			}
			
			vText.clear();
			
		}
	
		public void inserts(String[] to){
			
			String extraPoint = ", '";
			
			if (vText.isEmpty()){ // вообще такое не должно происходить, если случается, то это не правильно.
				return;
			}
				
			if (inserts.capacity() == 0){
				newStr = "insert into "+ to[0] + " (" + to[1] + ") values ()";
				extraPoint = "'";

			}else{
				newStr = newStr.substring(0, newStr.indexOf(") values")) + ", " + to[1]  + newStr.substring(newStr.indexOf(") values"));	
			}
			
			
			if (vText.capacity() == 1){
				
				if (inserts.capacity() > 0){
					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);
						
						currStr = newStr.substring(0, newStr.indexOf(")", newStr.indexOf("values"))) + extraPoint + vText.get(0) + "')";
						newStr = currStr;
						inserts.set(i, currStr);
					}
				}
				
				if (inserts.capacity() == 0){
					//System.out.println( newStr.substring(0, newStr.indexOf("')")));
					String currStr = newStr.substring(0, newStr.indexOf(")", newStr.indexOf("values"))) + extraPoint + vText.get(0) + "')";
					newStr = currStr;
					inserts.addElement(currStr);
					
				}

			}
			//------------------------------------------------------------
			
			if (vText.capacity() > 1){
				
				if (inserts.capacity() > 0){
					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);	
						currStr = newStr.substring(0, newStr.indexOf(")", newStr.indexOf("values"))) + extraPoint + vText.get(i) + "')";
						//newStr = currStr;
						inserts.set(i, currStr);
					}
				}
				
				if (inserts.capacity() == 0){
					
					for (int i = 0; i < vText.capacity(); i++){
						String currStr = newStr.substring(0, newStr.indexOf(")", newStr.indexOf("values"))) + extraPoint + vText.get(i) + "')";
						//newStr = currStr;
						inserts.addElement(currStr);
					}
					
				}
				
			}
			vText.clear();
			//vText.removeAllElements(); 
			for (int i = 0; i < inserts.capacity(); i++)
				System.out.println(inserts.get(i));
		}
		
	
	}
