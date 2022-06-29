import java.util.*;
import java.io.*;

public class DP{

	//Sorts input ArrayList, used to preserve numerical order of literals
	public static void sort(ArrayList<Integer> list){
        for (int i=1; i<list.size(); ++i){
            int key = list.get(i);
            int j = i-1;             
            while (j>=0 && list.get(j) > key){
                list.set(j+1, list.get(j));
                j--;
            }
            list.set(j+1, key);
        }
    }    

    //Scans clause table input to see if the passed literal has consistent signage or not
    public static boolean isPure(ArrayList<ArrayList<Integer>> clauses, int literal){    	 
     	
    	int sign=0;
    	for(int i=0; i<clauses.size(); i++){
    		if(sign==0){    			
    			sign=clauses.get(i).get(literal);
    		}
    		else{
    			if((sign>0 && clauses.get(i).get(literal)<0) || (sign<0 && clauses.get(i).get(literal)>0)){    				
    				return false;
    			}
    		}
    	}
    	if(sign!=0){
	    	return true;
	    }
	    else{
	    	return false;
	    }
    }

    //Scans given clause to see if literal is the only one in it, flags the singleton clause (if present) for deletion
    public static boolean isLonely(ArrayList<ArrayList<Integer>> clauses, int literal){
		boolean alone=true;
    	for(int i=0; i<clauses.size(); i++){
    		alone=true;
    		for(int j=0; j<clauses.get(i).size(); j++){
    			if(clauses.get(i).get(j)!=0 && j!=literal){    				
    				alone=false;
    			}
    		}
    		if(alone){    			    			
    			clauses.get(i).set(0, -1);
    			return true;
    		}    		
    	}   
    	return false; 	
    }

    //Propogates a literal value through the clause table
    public static void propogate(ArrayList<ArrayList<Integer>> clauses, int literal, int value){
    	for(int i=0; i<clauses.size(); i++){
			if(clauses.get(i).get(literal)==value){							
				clauses.remove(i);
				i--;
			}
			else if(clauses.get(i).get(literal)==(value*-1)){							
				clauses.get(i).set(literal, 0);								
			}
		}
    }

    //Checks if there are any empty clauses in the table
    public static boolean zeroCheck(ArrayList<ArrayList<Integer>> clauses){
    	boolean zero=true;
    	for(int i=0; i<clauses.size(); i++){
    		zero=true;
    		for(int j=0; j<clauses.get(i).size(); j++){
    			if(clauses.get(i).get(j)!=0){
    				zero=false;
    			}
    		}   
    		if(zero){    			
    			return true;
    		} 		
    	}
    	return false;
    }

    //Used to copy 2x2 ArrayList to detatch references
    public static ArrayList<ArrayList<Integer>> copy(ArrayList<ArrayList<Integer>> old){
    	ArrayList<ArrayList<Integer>> newList = new ArrayList<ArrayList<Integer>>(old.size());    	
    	for(int i=0; i<old.size(); i++){    		
    		newList.add((ArrayList<Integer>) old.get(i).clone());
    	}
    	return newList;
    }

    //The algorithm
    //Inputs are the clause table, the list of variables, and a mapping of their corresponding values
    public static ArrayList<Integer> DPA(ArrayList<ArrayList<Integer>> clauses, ArrayList<Integer> variables, ArrayList<Integer> values){
    	  
    	//Easy cases loop
    	//Flag used to determine when things stop happening in easy case loop
    	boolean checkAgain=true;
    	
    	while(checkAgain){ 
    		checkAgain=false;  		

	    	//Success, assign any lingering unbound variables to false since they indicate moves or situations that need not be made
	    	if(clauses.isEmpty()){
	    		for(int i=0; i<values.size(); i++){
	    			if(values.get(i)==0){	    				
	    				values.set(i, -1);
	    			}
	    		}
	    		return values;
	    	}

	    	//Failure
	    	if(zeroCheck(clauses)){	    		
	    		return null;
	    	}
	    	
	    	//Locate pure or isolated variables, and if they exist, assign proper value and modify clauses accordingly
	    	for(int i=0; i<variables.size(); i++){	

	    		//Check again for 0 clauses in case a previous propogation created one
		    	if(zeroCheck(clauses)){	    		
		    		return null;
		    	}    		
	    		
	    		//Unbound variable
	    		if(values.get(i)==0){

	    			//Pure literal
	    			if(isPure(clauses, variables.get(i))){	   			    				 
	    				checkAgain=true;   				   				
	    				for(int x=0; x<clauses.size(); x++){
	    					if(clauses.get(x).get(variables.get(i))!=0){	    						
	    						values.set(i, clauses.get(x).get(variables.get(i)));
	    						propogate(clauses, variables.get(i), clauses.get(x).get(variables.get(i)));
	    					}
	    				}
	    				
	    			}	    			
	    	
	    			//Singleton literal
					if(isLonely(clauses, variables.get(i))){															
						checkAgain=true;
						for(int y=0; y<clauses.size(); y++){
							if(clauses.get(y).get(0)==-1){								
								clauses.get(y).set(0, 0);								
								values.set(i, clauses.get(y).get(variables.get(i)));
								propogate(clauses, variables.get(i), clauses.get(y).get(variables.get(i)));
							
							}														
						}
					} 					    			
	    		}
	    	}
	    }	

	    //Hard cases
	    //Clone everything first to create local copies without ruining everything
    	ArrayList<ArrayList<Integer>> newClauses = copy(clauses);
    	ArrayList<Integer> newvariables = (ArrayList<Integer>) variables.clone(); 
    	ArrayList<Integer> newValues = (ArrayList<Integer>) values.clone(); 

    	//Select first variable that is unbound, try it with true
    	int A=values.indexOf(0);
    	newValues.set(A, 1);
    	propogate(newClauses, newvariables.get(A), 1);    	
    	ArrayList<Integer> solution = DPA(newClauses, newvariables, newValues);    	
    	
    	//If faillure, recopy everything to restore their values before the recursion, then retry with false
    	if(solution==null){
    		newClauses = copy(clauses);
    		newvariables = (ArrayList<Integer>) variables.clone();
    		newValues = (ArrayList<Integer>) values.clone();
    		
    		newValues.set(A, -1);    		
    		propogate(newClauses, newvariables.get(A), -1);    		
    		return DPA(newClauses, newvariables, newValues);
    	}

    	//If success, return the values, make some quesadillas to celebrate
    	else{
    		return solution;
    	}
    }
  
	public static void main(String[] args)throws FileNotFoundException{

		//Read input parameters, prepare clause table
		File input = new File("DPinput.txt");
		Scanner scan = new Scanner(input);
		ArrayList<ArrayList<Integer>> clauses = new ArrayList<ArrayList<Integer>>();		
		int max=0;			

		//Will store the literals that are variables for more efficient processing
		ArrayList<Integer> variables = new ArrayList<Integer>();
		variables.add(-5);

		while(scan.hasNextLine()){

			//Extract clause in form of a line
			String line = scan.nextLine();						
			if(line.equals("0")){				
				break;
			}	
			Scanner lineScan = new Scanner(line);

			//Create clause based on line
			ArrayList<Integer> curr=new ArrayList<Integer>();

			while(lineScan.hasNextInt()){
				int literal=lineScan.nextInt();
				if(Math.abs(literal)>max){
					max=Math.abs(literal);
				}	

				//If list is too small, add 0's until the corresponding index is created, then add to list		
				if(curr.size()<=Math.abs(literal)){
					int x=curr.size();
					while(x!=Math.abs(literal)){
						curr.add(0);
						x++;
					}
					curr.add((literal/Math.abs(literal)));
				}

				//Otherwise jsut set the proper index
				else{
					curr.set(Math.abs(literal), (literal/Math.abs(literal)));
				}

				//Add literal to list of variables literals if not already there
				if(variables.contains(Math.abs(literal))==false){
					variables.add(Math.abs(literal));
				}
			}

			lineScan.close();

			//Add current clause to clause table			
			clauses.add(curr);					
		}

		//Sort variables list to preserve numerical order of clause for convention
		sort(variables);	

		//List to hold values of atoms in corresponding literals
		ArrayList<Integer> values = new ArrayList<Integer>();
		values.add(-5);	
		for (int i = 0; i < variables.size(); i++) {
			values.add(0);
		}	

		//Even out sizes of clauses for simplicity's sake (i.e., make the clause table an even rectangle)
		for(int i=0; i<clauses.size(); i++){
			while(clauses.get(i).size()!=(max+1)){
				clauses.get(i).add(0);
			}
		}		

		//Call DPA	
		values=DPA(clauses, variables, values);

		//Print results
		PrintWriter writer = new PrintWriter("DPoutput.txt");

		if(values==null){
			writer.println("NO SOLUTION.");
		}
		else{				

			for(int v=1; v<values.size(); v++){
				writer.print(v+" ");
				if(values.get(v)==1){
					writer.println("T");
				}
				else if(values.get(v)==-1){
					writer.println("F");
				}	

				//Should never print, used to indicate variables who did not receive either truth value
				//Used mainly for debugging, kept in the code for easy error location
				else{
					writer.println("Value not found");
				}		
			}

			//Append mapping of numbers to atoms for interpretation
			writer.println("0");
			while(scan.hasNextLine()){
				String line=scan.nextLine();
				writer.println(line);
			}

			scan.close();			
		}
		writer.close();

	}
}