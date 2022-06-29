import java.util.*;
import java.io.*;

public class PCP{

	//State class to hold string and path data for initial dominoes as well as their generated states
	public static class State{

		//Holds top and bottom strings; as per our definition of a state, one of these will be null for all non base domino states
		private String top;
		private String bottom;

		//Pointer to previous node for path recovery
		private State prev;	

		//The domino used before this state, used to recover solution sequence
		private int lastDomino;	

		public State(String top, String bottom, State prev, int lastDomino){			
			this.top=top;
			this.bottom=bottom;
			this.prev=prev;	
			this.lastDomino=lastDomino;					
		}

		public String getTop(){
			return top;
		}

		public String getBottom(){
			return bottom;
		}

	}

	//Given 2 input strings, cancels out shared elements and returns a state with the proper strings, with one of them being null
	//Returns null if a state cannot be made from the input strings
	public static State makeState(String top, String bottom){

		int i=0;		

		for(; i<top.length() && i<bottom.length(); i++){
			if(top.charAt(i)!=bottom.charAt(i)){
				return null;
			}
		}

		if(top.length()>bottom.length()){
			return new State(top.substring(i), "", null, 0);
		}
		else{
			return new State("", bottom.substring(i), null, 0);
		}


	}

	//Global states variable so that number of states can be accessed and continuously modified across functions
	public static int states=1;

	//Iterative Deepening Depth First Search
	//Accepts list of base dominoes, a state node, a maximum depth, the maximum number of states, a path array to populate when needed, and the print flag
	public static boolean IDS(ArrayList<State> base, State parent, int maxDepth, int maxStates, ArrayList<Integer> path, boolean print){		
		
		//Terminate if range parameters exceeded
		if(maxDepth<=0 || states==maxStates){
			return false;			
		}

		//Initialize as false; set to true when solution found to stop recursion
		boolean found=false;

		//Try adding all base dominoes to current node (parent)
		for(int i=0; i<base.size() && found==false; i++){

			//Create top and bottom strings by adding those of the current base domino
			String top=parent.getTop()+base.get(i).getTop();
			String bottom=parent.getBottom()+base.get(i).getBottom();

			//If they are equal, end search, trace path back up to fill path array for later printing, return true
			if(top.equals(bottom)){	
				path.add(i+1);	
				State curr=parent;									
				while(curr!=null){
					path.add(curr.lastDomino);
					curr=curr.prev;
				}							
				return true;
			}

			//Otherwise, attempt to make a state node from the generated strings
			State child=makeState(top, bottom);

			//Only proceed if chid node was successfully made
			if(child!=null){

				//Combine strings for easier reading and print if the flag is set				
				if(print==true){
					String stateString=child.getTop()+"/"+child.getBottom();
					System.out.println("State: "+stateString);
				}	

				//Since a state is made, iterate the counter and check to see if it's still in range							
				states++;
				if(states==maxStates){
					return false;
				}

				//Set the starting node as the parent of the newly generated one, then set new node's domino to the used one
				child.prev=parent;
				child.lastDomino=i+1;

				//Return recursion result of calling IDS on the newly generated node, at next depth level
				found=IDS(base, child, maxDepth-1, maxStates, path, print);
				
			}			
		}
		return found;
	}	

	public static void main(String [] args)throws FileNotFoundException{		
		
		//Initialize parameters and prepare text file for reading 
		File input = new File("input.txt");
		Scanner scan = new Scanner(input);
		int qSize=0; //will hold max queue size
		int stateSize=0; //will hold maximum number of states
		boolean print=false; //determines if search sequence is printed	
		ArrayList<State> base = new ArrayList<State>();	//holds set of usable base dominoes

		//Load inputs and populate paramters, assuming perfect format in input
		while (scan.hasNextInt()){
			qSize=scan.nextInt();
			stateSize=scan.nextInt();
			String printFlag=scan.next();
			if (printFlag.equalsIgnoreCase("yes")){
				print=true;
			}
			else{
				print=false;
			}

			//Create states for the dominoes and load them into the base list
			while(scan.hasNext()){
				int name=scan.nextInt();
				String top=scan.next();
				String bottom=scan.next();
				if(top.equals("/")){
					top="";
				}
				else if(bottom.equals("/")){
					bottom="";
				}
				State dom = new State(top, bottom, null, 0);								
				base.add(dom);

			}
		}
		//At this point, base filled with available base dominoes, parameters initialized

		//Initialize search params
		Queue<State> frontier = new LinkedList<State>(); //holds nodes to be searched further
		Hashtable<String, String> visited= new Hashtable<String, String>();	//keeps track of used strings			
		State root=new State("", "", null, 0); //where to begin seach
		boolean solution=false; //flag to be set if/when solution is found
		ArrayList<Integer> path = new ArrayList<Integer>(); //will hold solution path, if applicable
		frontier.add(root); 
		
		//Stage 1: BFS, runs until either queue is exhausted, maxed out, or max state size reached
		while(frontier.peek()!=null && solution==false && frontier.size()<=qSize && states<=stateSize){
						
			State parent=frontier.remove();	

			//From first node removed from queue, try adding each domino to it 		
			for (int i=0; i<base.size() && solution==false; i++){

				//Add domino to state's strings
				String top=parent.getTop()+base.get(i).getTop();
				String bottom=parent.getBottom()+base.get(i).getBottom();								

				//Is the two are equal, solution flag set, path traced back and loaded for later printing
				if (top.equals(bottom)){											
					solution=true;
					path.add(i+1);										
					while(parent!=null){						
						path.add(parent.lastDomino);
						parent=parent.prev;
					}									
				}

				//Otherwise, attempt to make a state from the strings
				else{
					State child=makeState(top, bottom);

					//Only proceed if successful
					if(child!=null){

						//Create combined string to print if necessary and then store in hash table
						String stateString=child.getTop()+"/"+child.getBottom();
						if(print==true){
							System.out.println("State: "+stateString);
						}

						//Only proceed if the combined string is not already in the hash table to avoid cycles
						if(visited.containsKey(stateString)==false){
							visited.put(stateString, stateString);							
							frontier.add(child);

							//Set parent and domino used
							child.prev=parent;
							child.lastDomino=i+1;							
							states++;
						}
					}
				}				
			}
			
		}

		//If solution found in BFS, print path (in reverse, not counting root node), and use the dominoes to print string
		if(solution==true){
			String sol="";
			System.out.println("\nSolution Sequence: ");
			for(int x=path.size()-2; x>=0; x--){
				System.out.print("D"+path.get(x)+", ");
				sol+=base.get(path.get(x)-1).getTop();
			}
			System.out.println("\n\nSolution String: "+sol);

		}		
		else if(states>=stateSize){
			System.out.println("No solution found with given range parameters.");
		}

		//If queue capacity was not reached and neither was state size, no solution exists
		else if(qSize>frontier.size()){			
			System.out.println("No solution exists.");
		}

		//Stage 2: IDDFS from remaining frontier after BFS has terminated with a full queue and remaining state space available
		else{

			//Flag for when state size is reached
			boolean filled=false;

			//Begin searching at depth one, iterate indefinitely until space limit reached or solution found
			int depth=1;
			while(solution==false && filled==false){				
				
				int reset=states;
				int size=frontier.size();

				//Initiate DFS of every queued frontier node
				for(int i=0; i<size && solution==false && filled==false; i++){
					State curr=frontier.remove();

					//Result of IDFS from current node
					boolean result=IDS(base, curr, depth, stateSize, path, print);
					
					//Solution found					
					if(result==true){
						solution=true;
					}

					//IDS found no solution at given depth from given node, requeue for use in later depth
					else{
						if(states==stateSize){
							filled=true;
						}
						frontier.add(curr);
						
					}					
				}
				//States kept continously iterating within the same depth, but at a new depth the count must be reset, as the same states are traversed again
				states=reset;
				depth++;
			}

			//If solution found, print the path and solution sequence/string
			if(solution==true){
				String sol="";
				System.out.println("\nSolution Sequence: ");
				for(int x=path.size()-2; x>=0; x--){
					System.out.print("D"+path.get(x)+", ");
					sol+=base.get(path.get(x)-1).getTop();
				}
				System.out.println("\n\nSolution String: "+sol);

			}
			//If state space was reached
			else if(filled==true){
				System.out.println("No solution found in given range parameters.");
			}

			else{
				System.out.println("No solution exists for given dominoes.");
			}

		}		
		
	}

}