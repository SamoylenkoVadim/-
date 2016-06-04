import java.util.Vector;
import java.util.function.Consumer;

import org.w3c.dom.Element;
	
	public class StructureForRecursion {
	
		//Vector<String> vNodeName	= new Vector<String>(0,1);
		Vector<String> vText		= new Vector<String>(0,1);
		//Vector<String[]> request	= new Vector<String[]>(0,1);
		//Vector<String> inserts		= new Vector<String>(0,1);
		Vector<Vector<String>> 	requests = new Vector<Vector<String>>(0,1);
		String newStr 				= null;
		//String [] idStr			= new String[1000];
	
		public void pullToArray(String text){

			 vText.addElement(text);
			 
		}
					
		
		public void setInserts(String[] argTo){
						
			for (int i = 0; i < argTo.length; i++){
				
				boolean found = false;
				Vector<String> inserts = null;
				
				argTo[i] = argTo[i].replaceFirst(" ","");
				String [] arrayTo = argTo[i].split("/");
				
				if (requests.capacity()==0){
					
					inserts = new Vector<String>(0,1);
					inserts = formInserts(arrayTo, inserts);
					requests.addElement(inserts);
					
				}else{				
					for (int j = 0; j < requests.capacity(); j++){

						if (requests.get(j).get(0).indexOf(arrayTo[0]) > 0){
							
							if (requests.get(j).get(0).indexOf(arrayTo[1]) == -1){
								inserts = new Vector<String>(0,1);
								inserts = formInserts(arrayTo, requests.get(j));
								requests.set(j, inserts);
								found = true;
								break;
							}
							
						}						
					}
					
					if (!found){
						inserts = new Vector<String>(0,1);
						inserts = formInserts(arrayTo, inserts);
						requests.addElement(inserts);
					}
				}
			}	
			vText.clear();
			vText.removeAllElements();
			vText = null;
			newStr = null;
			vText = new Vector<String>(0,1);
		}
	
		public Vector<String> formInserts(String[] to, Vector<String> inserts){
			
			String extraPoint = ", '";
			if (vText.isEmpty()){ // вообще такое не должно происходить, если случается, то это не правильно.
				return inserts;
			}
			
			
				
			if (inserts.capacity() == 0){
				newStr = "insert into "+ to[0] + " (" + to[1] + ") values ()";
				extraPoint = "'";

			}else{
				newStr = inserts.get(0);
				newStr = newStr.substring(0, newStr.indexOf(") values")) + ", " + to[1]  + newStr.substring(newStr.indexOf(") values"));	
			}
			
			
			if (vText.capacity() == 1){
				
				if (inserts.capacity() > 0){

					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);
						currStr = currStr.substring(0, currStr.indexOf(") values")) + ", " + to[1]  + currStr.substring(currStr.indexOf(") values"));
						currStr = currStr.substring(0, currStr.indexOf(")", currStr.indexOf("values"))) + extraPoint + vText.get(0) + "')";
						//newStr = currStr;
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
			//--------------------------------------------------------------------------
			
			if (vText.capacity() > 1){
				
				if (inserts.capacity() > 1){
					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);
						currStr = currStr.substring(0, currStr.indexOf(") values")) + ", " + to[1]  + currStr.substring(currStr.indexOf(") values"));
						currStr = currStr.substring(0, currStr.indexOf(")", currStr.indexOf("values"))) + extraPoint + vText.get(i) + "')";
						//newStr = currStr;
						inserts.set(i, currStr);
					}
				}
				
				
				if ((inserts.capacity() == 0)||(inserts.capacity() == 1)){
					
					for (int i = 0; i < vText.capacity(); i++){
						String currStr = newStr.substring(0, newStr.indexOf(")", newStr.indexOf("values"))) + extraPoint + vText.get(i) + "')";

						if ((inserts.capacity() == 1)&&(i == 0))
							inserts.set(0, currStr);
						else
							inserts.addElement(currStr);
					}
					
				}
				
			}

			//vText.removeAllElements(); 
			//for (int i = 0; i < inserts.capacity(); i++)
				//System.out.println(inserts.get(i));
			
			return inserts;
		}
		
	
	}
