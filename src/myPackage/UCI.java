package myPackage;

import java.util.*;
public class UCI {
	static Game currGame = new Game();
	static int num_moves;
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Whitey has arrived");
		
		while (true)
	        {
	            
	            String inputString=input.nextLine();
	            if (inputString.equals("uci"))
	            {
	                System.out.println("id name Whitie");
	                System.out.println("id author LuckyAC");
	                System.out.println("version x");
	                System.out.println("uciok");
	            }  else if (inputString.equals("ucinewgame")){
	            	currGame = new Game();
	            	num_moves = 0;
	            }  else if (inputString.equals("isready")){
	            	System.out.println("readyok");
	            } else if (inputString.equals("stop")){
	            	System.out.println("Not implemented");
	            }  else if (inputString.startsWith("position")){
	            	String[] moves = inputString.split(" ");
	            	if (moves.length - 3 > num_moves){
	            		for(int i = num_moves + 3; i < moves.length; i++){
	            			Move lastMove = new Move(moves[i]); 
	            			currGame = new Game(currGame, lastMove );
	            			num_moves++;
	            		}
	            	}
	            }  else if (inputString.startsWith("go")){
	            	System.out.format("bestmove %s \n", findMove());
	            } else if (inputString.equals("quit")){
	            	System.out.println("engine quitting");
	            	break;
	            } else {
	            	System.out.println("Not implemented");
	            }
	            
	        }
		input.close();
	}
	
	public static String findMove(){
		ArrayList<Move> possibleMoves = currGame.generateLegalMoves();
    	int rnd = new Random().nextInt(possibleMoves.size());
    	Move nextMove =  possibleMoves.get(rnd);
    	return nextMove.convertToUCIFormat();
	}

}
