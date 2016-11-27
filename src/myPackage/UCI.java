package myPackage;

import java.util.*;
public class UCI {

	private static Game currGame = new Game();
	private static int num_moves;
	private static final int DEPTH = 6;
	private static final boolean QUIESCE = true;
	private static NegaMax negaMax;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		while (true)
	        {
	            
	            String inputString=input.nextLine();
	            if (inputString.equals("uci"))
	            {
	                System.out.println("id name Arby");
	                System.out.println("id author LuckyAC");
	                System.out.println("version backwards 2");
	                System.out.println("option name Hash type spin default 1 min 1 max 128");
	                System.out.println("uciok");
	            }  else if (inputString.equals("ucinewgame")){
	            	currGame = new Game();
	            }  else if (inputString.equals("isready")){
	            	System.out.println("readyok");
	            } else if (inputString.equals("stop")){
	            	System.out.println("Not implemented");
	            }  else if (inputString.startsWith("position")){
	            	String[] moves = inputString.split(" ");
	            	num_moves = 0;
	            	currGame = new Game();
	            	for(int i = 3; i < moves.length; i++){
	            		Move lastMove = new Move(moves[i]); 
	           			currGame = new Game(currGame, lastMove );
	           			num_moves++;
	           		}
	            	System.out.println(currGame);
	            }  else if (inputString.startsWith("setoption name Hash value")){
	            	int hashSize = Integer.parseInt(inputString.split(" ")[4]);
	            	negaMax = new NegaMax(hashSize, QUIESCE);
	            }else if (inputString.startsWith("go")){
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
		if (num_moves == 0){
			ArrayList<Move> possibleMoves = new ArrayList<>();
			possibleMoves.add(new Move(1, 3, 3, 3)); possibleMoves.add(new Move(1, 4, 3, 4)); possibleMoves.add(new Move(1, 5, 3, 5)); possibleMoves.add(new Move(0, 1, 2, 2));
			int rnd = new Random().nextInt(possibleMoves.size());
			Move nextMove =  possibleMoves.get(rnd);
			return nextMove.convertToUCIFormat();
		}
		else if (num_moves <=29){
			return negaMax.findBestMove(currGame, DEPTH).convertToUCIFormat();
			}
		else if (num_moves <=69){return negaMax.findBestMove(currGame, DEPTH+1).convertToUCIFormat();}
		else if (num_moves <=99){return negaMax.findBestMove(currGame, DEPTH+2).convertToUCIFormat();}
		else {return negaMax.findBestMove(currGame, DEPTH+3).convertToUCIFormat();}
	}

}
