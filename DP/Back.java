import java.util.*;
import java.io.*;

public class Back{

	//Based on a passed Jump atom, extracts the peg/time values and returns them as an array	
	public static int[] extract(String literal){		
		int start=literal.indexOf("(")+1;
		int [] pegs=new int[4];
		
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

	//Sorts input based on time value
	public static void sort(ArrayList<String> list){		
		for (int i=1; i<list.size(); i++){

			String temp=list.get(i);
			int key=extract(list.get(i))[3];
			int j=i-1; 

			while (j>=0 && extract(list.get(j))[3]>key){
				list.set(j+1, list.get(j));				
				j--;
			}

			list.set(j+1, temp);			
		}
	}

	public static void main(String[] args)throws FileNotFoundException{
		File input = new File("DPoutput.txt");
		Scanner scan = new Scanner(input);
		ArrayList<Integer> values = new ArrayList<Integer>();	
		ArrayList<String> jumps = new ArrayList<String>();
		values.add(0);
		jumps.add("");

		if(!scan.hasNextInt()){
			System.out.println("NO SOLUTION FOUND.");
		}
		else{
			while(scan.hasNext()){			
				int index=scan.nextInt();
				if(index==0){
					break;
				}
				String val=scan.next();				
				if(val.equals("F")){
					values.add(-1);
				}
				else if(val.equals("T")){
					values.add(1);
				}
				else{
					values.add(0);
				}
			}
			while(scan.hasNext()){
				int index=scan.nextInt();
				String atom=scan.next();				
				if(values.get(index)>0){
					String action=atom.substring(0, atom.indexOf("("));
					if(action.equals("Jump")){
						jumps.add(atom);
					}
				}

			}
			scan.close();
			jumps.remove(0);			
			sort(jumps);
			for(int i=0; i<jumps.size(); i++){				
				System.out.println(jumps.get(i));
				// int [] arr=extract(jumps.get(i));				
				// System.out.println(arr[3]+": Jump the peg in Hole "+arr[0]+" into Hole "+
				// 	arr[2]+", removing the peg in Hole "+arr[1]+".");
			}

		}
	}
}