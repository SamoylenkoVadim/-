import java.util.Vector;
import java.util.function.Consumer;

import org.w3c.dom.Element;
	
	public class StructureForRecursion {
	
		Vector<String> vNodeName	= new Vector<String>(0,1);
		Vector<String> vText		= new Vector<String>(0,1);
		Vector<String[]> request	= new Vector<String[]>(0,1);
		Vector<String> inserts		= new Vector<String>(0,1);
		String newStr 				= null;
	
		public void pullToArray(String text){
			 
			 //vNodeName.addElement(el.getNodeName());
			 vText.addElement(text);
			 
		}
		
		public String getArrayText(){
			
			String str = vText.toString();

			vText.removeAllElements(); 
			return str.substring(1, str.length()-1);
			
			
		}
		
		public String getArrayNodeNames(){
			
			return vNodeName.toString();
			
		}
	
		public void prepareRow(String[] to){

			boolean flag = true;
			String[] vArr = null;
			
			int i = 0;
			for (i = 0; i < request.capacity(); i++){
				vArr = request.get(i);
				if (vArr[0].equals(to[0])){
					flag = false;
					break;
				}				
			}
			
			if (flag){
				
				String[] newArr = new String [2];
				newArr[0] = to[0];
				String newStr = "insert into "+ to[0] + " (" + to[1] + ") values ('"+ getArrayText() + "')"; 
				newArr[1] = newStr;
				request.add(newArr);
				
			}
			if (!flag){
				String str = vArr[i+1];
				String newStr = str.substring(0, str.indexOf(") values")) + ", " + to[1] + ") values" + 
									str.substring(str.indexOf(" ('"), str.indexOf("')")) + "', '" + getArrayText() + "')";
				vArr[i+1] = newStr;
			}

		}
	
		public void setInserts(String[] to){

			if (inserts.capacity() == 0){
				newStr = "insert into "+ to[0] + " (" + to[1] + ") values (')";

			}else{
				newStr = newStr.substring(0, newStr.indexOf(") values")) + ", " + to[1] + ") values " + newStr.substring(newStr.indexOf(") values"));	
			}
			

			if (vText.capacity() == 1){
				
				if (inserts.capacity() > 0){
					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);
						currStr = newStr.substring(0, newStr.indexOf("')")) + "'" + vText.get(0) + "')";
						inserts.set(i, currStr);
					}
				}
				
				if (inserts.capacity() == 0){
					System.out.println( newStr.substring(0, newStr.indexOf("')")));
					String currStr = newStr.substring(0, newStr.indexOf("')")) + "'" + vText.get(0) + "')";
					inserts.addElement(currStr);
				}

			}
			//------------------------------------------------------------
			
			if (vText.capacity() > 1){
				
				if (inserts.capacity() > 0){
					for (int i = 0; i < inserts.capacity(); i++){
						String currStr = inserts.get(i);
						currStr = newStr.substring(0, newStr.indexOf("')")) + "'" + vText.get(i) + "')";
						inserts.set(i, currStr);
					}
				}
				
				if (inserts.capacity() == 0){
					
					for (int i = 0; i < vText.capacity(); i++){
						String currStr = newStr.substring(0, newStr.indexOf("')")) + "'" + vText.get(i) + "')";
						inserts.addElement(currStr);
					}
					
				}
				
			}
			for (int i = 0; i < inserts.capacity(); i++)
				System.out.println(inserts.get(i));
		}
		
	
	}
