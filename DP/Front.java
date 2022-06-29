import java.util.*;
import java.io.*;

public class Front{

	//Based on the input pegs A, B, C, and the time I, creates a Jump() string (ie, Jump(1,2,3,1) )
	public static String makeJumpLiteral(int A, int B, int C, int I){
		String literal="Jump(";
		literal+=Integer.toString(A)+",";
		literal+=Integer.toString(B)+",";
		literal+=Integer.toString(C)+",";
		literal+=Integer.toString(I)+")";
		return literal;
	}

	//Based on the input peg A and the time I, creates a Peg() string (i.e., Peg(1,2) )
	public static String makePegLiteral(int A, int I){
		String literal="Peg(";
		literal+=Integer.toString(A)+",";
		literal+=Integer.toString(I)+")";
		return literal;
	}

	//Based on a passed atom, extracts the peg/time values and returns them as an array
	//Array order is either A,B,C,I for Jump or A,I for Peg
	public static int[] extract(String literal){
		String action=literal.substring(0, literal.indexOf("("));
		int start=literal.indexOf("(")+1;
		int [] pegs=new int[1];
		if(action.equals("Jump")){			
			pegs=new int[4];						
		}
		else{
			pegs=new int[2];				
		}
		for(int i=0; i<pegs.length; i++){
			int end=literal.indexOf(",", start);
			if(end<0){
				end=literal.length()-1;
			}
			pegs[i]=Integer.parseInt(literal.substring(start, end));
			start=end+1;
		}
		return pegs;
	}

	public static void main(String[] args)throws FileNotFoundException{

		//Read input, set number of holes and the current empty peg based on first 2 numbers
		File input = new File("input.txt");
		Scanner scan = new Scanner(input);
		int holes=scan.nextInt();
		int empty=scan.nextInt();
		int maxTime=holes-1;
		
		//List that will hold the atoms for the problem, stored as Strings
		ArrayList<String> atoms=new ArrayList<String>();

		//Indexes of the atoms will be used as their symbols for the DP input
		//Add null because the symbols should start at 1		
		atoms.add("");
		
		//Read each line into atom list a a Jump
		while(scan.hasNextInt()){
			int A=scan.nextInt();
			int B=scan.nextInt();
			int C=scan.nextInt();
			for(int i=1; i<maxTime; i++){
				String literal=makeJumpLiteral(A, B, C, i);
				if(!atoms.contains(literal)){
					atoms.add(literal);
				}
				
			}
			for(int i=1; i<maxTime; i++){
				String literal=makeJumpLiteral(C, B, A, i);
				if(!atoms.contains(literal)){
					atoms.add(literal);
				}				
			}
		}
		scan.close();

		//Input all Peg atoms 
		for(int i=1; i<holes+1; i++){
			for(int j=1; j<maxTime+1; j++){
				String literal=makePegLiteral(i, j);
				if(!atoms.contains(literal)){
					atoms.add(literal);
				}				
			}
		}
		
		//Initialize writer
		PrintWriter writer = new PrintWriter("DPinput.txt");	

		//Precondition Axioms	
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Jump")){				
				int [] arr=extract(atoms.get(i));
				for(int x=0; x<3; x++){					
					writer.print((i*-1)+" ");
					if(x!=2){
						writer.println(atoms.indexOf(makePegLiteral(arr[x], arr[3])));
					}
					else{
						writer.println((atoms.indexOf(makePegLiteral(arr[x], arr[3])))*-1);
					}
				}
			}
		}

		//Causal Axioms
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Jump")){				
				int [] arr=extract(atoms.get(i));
				for(int x=0; x<3; x++){								
					writer.print((i*-1)+" ");
					if(x!=2){
						writer.println((atoms.indexOf(makePegLiteral(arr[x], arr[3]+1)))*-1);
					}
					else{
						writer.println((atoms.indexOf(makePegLiteral(arr[x], arr[3]+1))));
					}
				}
			}
		}

		//Frame Axioms		
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Peg")){
				int [] peg=extract(atoms.get(i));
				if(peg[1]<maxTime){

					//Axiom A
					writer.print((i*-1)+" ");
					for(int j=1; j<atoms.size(); j++){
						String action2=atoms.get(j).substring(0, atoms.get(j).indexOf("("));
						if(action2.equals("Jump")){							
							int [] jump=extract(atoms.get(j));
							if(jump[3]==peg[1] && (jump[0]==peg[0] || jump[1]==peg[0])){
								writer.print(j+" ");
							}
						}
						else{
							int [] peg2=extract(atoms.get(j));
							if(peg2[0]==peg[0] && peg2[1]==(peg[1]+1)){
								writer.print(j+" ");
							}
						}						
					}
					writer.print("\n");

					//Axiom B
					writer.print((i*1)+" ");
					for(int j=1; j<atoms.size(); j++){
						String action2=atoms.get(j).substring(0, atoms.get(j).indexOf("("));
						if(action2.equals("Jump")){							
							int [] jump=extract(atoms.get(j));
							if(jump[3]==peg[1] && jump[2]==peg[0]){
								writer.print(j+" ");
							}
						}
						else{
							int [] peg2=extract(atoms.get(j));
							if(peg2[0]==peg[0] && peg2[1]==(peg[1]+1)){
								writer.print(j*-1+" ");
							}
						}						
					}
					writer.print("\n");

				}
			}
		}

		//One Action at a Time Axiom 
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Jump")){
				int [] jump=extract(atoms.get(i));
				for(int j=i+1; j<atoms.size(); j++){
					String action2=atoms.get(j).substring(0, atoms.get(j).indexOf("("));
					if(action2.equals("Jump")){
						int [] jump2=extract(atoms.get(j));
						if(jump[3]==jump2[3]){
							writer.println((i*-1)+" "+(j*-1));
						}
					}
				}
			}
		}

		//Starting State Axiom
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Peg")){
				int [] peg=extract(atoms.get(i));
				if(peg[1]==1){
					if(peg[0]==empty){
						writer.println((i*-1));
					}
					else{
						writer.println(i);
					}
				}
			}
		}

		//Ending State Axiom

			//Axiom A
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Peg")){
				int [] peg=extract(atoms.get(i));
				if(peg[1]==maxTime){
					writer.print(i+" ");
				}
			}
		}
		writer.print("\n");

			//Axiom B
		for(int i=1; i<atoms.size(); i++){
			String action=atoms.get(i).substring(0, atoms.get(i).indexOf("("));
			if(action.equals("Peg")){				
				int [] peg=extract(atoms.get(i));
				if(peg[1]==maxTime){
					for(int j=i+1; j<atoms.size(); j++){
						String action2=atoms.get(j).substring(0, atoms.get(j).indexOf("("));
						if(action2.equals("Peg")){							
							int [] peg2=extract(atoms.get(j));
							if(peg2[1]==maxTime){
								writer.println((i*-1)+" "+(j*-1));								
							}							
						}
					}
				}
			}
		}

		//Print 0, then print mapping of index to String form of atom for translation
		writer.println(0);
		for(int i=1; i<atoms.size(); i++){
			writer.print(i+" ");
			writer.println(atoms.get(i));
		}
		writer.close();		
	}
}