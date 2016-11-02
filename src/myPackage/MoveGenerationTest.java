package myPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class MoveGenerationTest {
	
	@Test
	public void rookTest() {
		//rook can move in four directions 
		Game game = new Game("8/1k3R2/2r5/8/8/8/8/4K3 b - - 0 1 ");
		assertTrue("Rook does not move correctly",
				Utils.checkLegalMoves(game,game.generateLegalMoves(),
				"c6", "c7"));	
	}
	
	@Test
	public void bishopTest(){
		Game game = new Game("4k3/8/8/3b4/8/8/8/4K3 b - -");
		assertTrue("Bishop does not move correctly",
				Utils.checkLegalMoves(game, game.generateLegalMoves(),
				"d5", "a8 b7 c6 e4 f3 g2 h1 a2 b3 c4 e6 f7 g8"));
	}
	
	@Test
	public void knightTest(){
		Game game = new Game("4k3/8/8/3n4/8/8/8/4K3 b - - 0 1");
		assertTrue("Knight does not move correctly",
				Utils.checkLegalMoves(game,game.generateLegalMoves(),
				"d5", "c7 b6 b4 c3 e3 f4 f6 e7"));
	}
	
	@Test
	public void queenTest(){
		Game game = new Game("4k3/8/8/3q4/8/8/8/4K3 b - - 0 1");
		assertTrue("Queen does not move correctly",
				Utils.checkLegalMoves(game,game.generateLegalMoves(),
				"d5", "c5 b5 a5 e5 f5 g5 h5 d6 d7 d8 d4 d3 d2 d1 "
						+ "a8 b7 c6 e4 f3 g2 h1 a2 b3 c4 e6 f7 g8"));
	}
	
	@Test
	public void kingTest(){
		Game game =new Game("3k4/8/1P3K2/8/8/8/8/8 b - - 0 1");
		assertTrue("King does not move correctly",
				Utils.checkLegalMoves(game,game.generateLegalMoves(),
				"d8", "c8 d7 e8"));
		Game game2 = new Game("rnbqk2r/ppppppbp/5n1N/6p1/8/8/PPPPPPPP/RNBQKB1R b KQkq - 5 4");
		assertTrue("King cannot castle king side",
				Utils.checkLegalMoves(game2,game2.generateLegalMoves(),
				"e8", "f8"));
	}
	
	@Test
	public void pawnTest(){
		Game game = new Game("8/2p2R2/1k6/8/8/8/8/4K3 b - - 0 1");
		assertTrue("Pawn does not move correctly",
				Utils.checkLegalMoves(game,game.generateLegalMoves(),
				"c7", "c6 c5"));
		Game pawnPromotionGame = new Game("8/P7/8/2K2k2/8/8/8/8 w - -");
		pawnPromotionGame = new Game(pawnPromotionGame, new Move("a7a8"));
		pawnPromotionGame = new Game(pawnPromotionGame, new Move("f5f6"));
		assertTrue("Pawn does not properly promote",
				Utils.checkLegalMoves(pawnPromotionGame,
						pawnPromotionGame.generateLegalMoves(),
						"a8", "a7 a6 a5 a4 a3 a2 a1 b8 c8 d8 e8 f8 g8 h8" 
						+ " b7 c6 d5 e4 f3 g2 h1"));
		
	}
	
	@Test
	public void castleToggleTest(){
		
		Game castleGame = new Game();
		castleGame = new Game(castleGame, new Move("e2e4"));
		castleGame = new Game(castleGame, new Move("e7e5"));
		castleGame = new Game(castleGame, new Move("f1e2"));
		castleGame = new Game(castleGame, new Move("d7d5"));
		castleGame = new Game(castleGame, new Move("g1h3"));
		castleGame = new Game(castleGame, new Move("g8h6"));
;
//		System.out.print(castleGame);
		ArrayList<Move> moves = castleGame.generateLegalMoves();
		assertTrue("Castling move not found", 
				moves.contains(new Move("e1g1")));	
		
		Game game2 = new Game("rn1qkbnr/ppp1pppp/2bp4/8/7N/6PB/PPPPPP1P/RNBQK2R b KQkq -");
		game2 = new Game(game2, new Move("c6h1"));
		game2 = new Game(game2, new Move("b1c3"));
		game2 = new Game(game2, new Move("h1c6"));
		moves = game2.generateLegalMoves();
		assertTrue("Illegal to castle after rook is taken",
				!moves.contains(new Move("e1g1")));
	}
		
}
