package myPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class MoveGenerationTest {
	
	private static boolean checkLegalMoves(String position, String squareString
									, String destinations){
		Game game = new Game(position);
		ArrayList<Move> legalMoves = makeMoves(squareString, destinations);
		boolean flag = true;
		
		Square square = new Square(squareString);
		int row = square.row, col = square.col;
		ArrayList<Move> moves = game.generateLegalMoves();
		
		//check that all legal moves have been generated
		for(int i = 0; i < legalMoves.size(); i++){
			Move legalMove = legalMoves.get(i);
			if (legalMove.currRow == row && legalMove.currColumn == col){
				if (!moves.contains(legalMove)){
					System.out.format("Legal move %s not found in position %s \n",
							legalMove.convertToUCIFormat(), position);
					flag = false;
				}
			}
		}
		
		//check that no illegal moves have been generated
		for (int i = 0; i < moves.size(); i++){
			Move move = moves.get(i);
			//System.out.format("Move generated by engine: %s \n", move.convertToUCIFormat());
			if (move.currRow == row && move.currColumn == col && !legalMoves.contains(move)){
				System.out.format("Illegal move %s found in position %s \n", 
						move.convertToUCIFormat(), position);
				flag = false;
			}
		}
		return flag;
	}
	
	/*Creates a list of Move objects out of the starting square 
	 * and multiple destinations
	 */
	private static ArrayList<Move> makeMoves(String square, String destinations){
		ArrayList<Move> moves = new ArrayList<Move>();
		String[] destinationArray = destinations.split(" ");
		for( int i = 0; i < destinationArray.length; i++){
			moves.add(new Move(square + destinationArray[i]));
		}
		return moves;
	}
	@Test
	public void rookTest() {
		//rook can move in four directions 
		assertTrue("Rook does not move correctly",
				checkLegalMoves("4k3/8/8/3r4/8/8/8/4K3 b - -",
				"d5", "c5 b5 a5 e5 f5 g5 h5 d6 d7 d8 d4 d3 d2 d1"));	
	}
	
	@Test
	public void bishopTest(){
		assertTrue("Bishop does not move correctly",
				checkLegalMoves("4k3/8/8/3b4/8/8/8/4K3 b - -",
				"d5", "a8 b7 c6 e4 f3 g2 h1 a2 b3 c4 e6 f7 g8"));
	}
	
	@Test
	public void knightTest(){
		assertTrue("Knight does not move correctly",
				checkLegalMoves("4k3/8/8/3n4/8/8/8/4K3 b - - 0 1",
				"d5", "c7 b6 b4 c3 e3 f4 f6 e7"));
	}
	
	@Test
	public void queenTest(){
		assertTrue("Queen does not move correctly",
				checkLegalMoves("4k3/8/8/3q4/8/8/8/4K3 b - - 0 1",
				"d5", "c5 b5 a5 e5 f5 g5 h5 d6 d7 d8 d4 d3 d2 d1 "
						+ "a8 b7 c6 e4 f3 g2 h1 a2 b3 c4 e6 f7 g8"));
	}
	
	@Test
	public void kingTest(){
		assertTrue("King does not move correctly",
				checkLegalMoves("8/8/8/3k4/8/8/8/4K3 b - - 0 1",
				"d5", "c6 d6 e6 c5 e5 c4 d4 e4"));
		assertTrue("King cannot castle king side",
				checkLegalMoves(
				"rnbqk2r/ppppppbp/5n1N/6p1/8/8/PPPPPPPP/RNBQKB1R b KQkq - 5 4",
				"e8", "f8"));
	}
	
	@Test
	public void pawnTest(){
		
	}
		
}
