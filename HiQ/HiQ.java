import java.util.*;
import java.io.*;

public class HiQ{

	//Structure to represent a possible move on the board
	//A move on the board consists of 3 numbers: a, b, anc c
	//a is the peg jumping over b into hole c
	public static class move{
		private int a;
		private int b;
		private int c;
		public move(int a, int b, int c){
			this.a=a;
			this.b=b;
			this.c=c;
		}
	}

	public static void printInt(int [] a){
		for(int i=0 ; i<a.length; i++){
			System.out.print(a[i]+", ");
		}
		System.out.println(" ");
	}


	public static boolean solve(int[] board, move[] moveList, int pegs, ArrayList<move> path){

		if(pegs==1){
			return true;
		}
		else{
			for(int i=0; i<moveList.length; i++){
				move curr = moveList[i];
				if(board[curr.a]==1 && board[curr.b]==1 && board[curr.c]==0){					
					board[curr.a]=0;
					board[curr.b]=0;
					board[curr.c]=1;
					path.add(curr);
					if(solve(board, moveList, pegs-1, path)==true){						
						return true;
					}
					else{
						path.remove(path.size()-1);
						board[curr.a]=1;
						board[curr.b]=1;
						board[curr.c]=0;
					}
				}
			}
			return false;
		}
	}

	public static void main (String [] args)throws IOException{
		/*

		0   1   2   3   4
		  5   6   7   8
		    9   10  11
		      12  13
		        14

		*/

		move [] moveList = {
			new move(0, 1, 2), new move(2, 1, 0),
			new move(1, 2, 3), new move(3, 2, 1),
			new move(2, 3, 4), new move(4, 3, 2),
			new move(5, 6, 7), new move(7, 6, 5),
			new move(9, 10, 11), new move(11, 10, 9),
			new move(0, 5, 9), new move(9, 5, 0),
			new move(1, 6, 10), new move(10, 6, 1),
			new move(2, 7, 11), new move(11, 7, 2),
			new move(5, 9, 12), new move(12, 9, 5),
			new move(6, 10, 13), new move(13, 10, 6),
			new move(9, 12, 14), new move(14, 12, 9),
			new move(2, 6, 9), new move(9, 6, 2),
			new move(3, 7, 10), new move(10, 7, 3),
			new move(4, 8, 11), new move(11, 8, 4),
			new move(7, 10, 12), new move(12, 10, 7),
			new move(8, 11, 13), new move(13, 11, 8),
			new move(11, 13, 14), new move(14, 13, 11)
		};

		int [] board = new int [15];
		Arrays.fill(board, 1);

		int empty = -1;		

	    while(empty > 14 || empty < 0){
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	System.out.print("Enter the initial empty hole (0 - 14): ");
	    	String s = br.readLine();
	    	empty=Integer.parseInt(s);
	    	if(empty>14 || empty<0){
	    		System.out.println("Bad input. Empty hole must be from 0 to 14, inclusive.");
	    		System.out.println(" ");
	    	}
	    }

	    board[empty]=0;	    
	    ArrayList<move> path = new ArrayList<move>();
	    boolean success = solve(board, moveList, 14, path);
	 	
	 	if(!success){
	 		System.out.println("No solution found.");
	 	}
	 	else{
	 		System.out.println("Solution found!");
	 		for(int i=0; i<path.size(); i++){
	 			System.out.println("Jump the peg in "+path.get(i).a+" over the peg in "+path.get(i).b+", into hole "+path.get(i).c+".");
	 		}
	 	}
	}
}