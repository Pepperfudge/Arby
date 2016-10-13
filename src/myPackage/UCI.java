package myPackage;

import java.util.*;
public class UCI {
	static Game currGame = new Game();
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		while (true)
	        {
	            
	            String inputString=input.nextLine();
	            if (inputString.equals("uci"))
	            {
	                System.out.println("id name Whitie");
	                System.out.println("id author LuckyAC");
	                System.out.println("uciok");
	            }  else if (inputString.equals("ucinewgame")){
	            	currGame = new Game();
	            }  else if (inputString.equals("isready")){
	            	System.out.println("readyok");
	            } else if (inputString.equals("stop")){
	            	System.out.println("Not implemented");
	            }  else if (inputString.startsWith("position")){
	            	String[] moves = inputString.split(" ");
	            	Move lastMove = new Move(moves[moves.length-1]); 
	            	currGame = new Game(currGame, lastMove );
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
    	currGame = new Game(currGame, nextMove);
    	return nextMove.convertToUCIFormat();
	}

}
