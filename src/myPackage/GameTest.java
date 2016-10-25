package myPackage;

import static org.junit.Assert.*;


import org.junit.Test;

public class GameTest {

	@Test
	public void findBlackCapturesTest() {
		Game game = new Game(
				"1b4NK/8/B2B4/k1p2n2/1R1RpP2/b7/8/Q2r2q1 b - f3");
		assertTrue("Does not correctly find captures for black king",
				Utils.checkLegalMoves(game, game.findCaptures(), "a5", "a6"));
		assertTrue("Does not correctly find captures for black pawn",
				Utils.checkLegalMoves(game, game.findCaptures(), "c5", "b4 d4"));
		assertTrue("Does not correctly find captures for black pawn en passant",
				Utils.checkLegalMoves(game, game.findCaptures(), "e4", "f3"));
		assertTrue("Does not correctly find captures for black queen",
				Utils.checkLegalMoves(game, game.findCaptures(), "g1", "d4 g8"));
		assertTrue("Does not correctly find captures for black rook",
				Utils.checkLegalMoves(game, game.findCaptures(), "d1", "a1 d4"));
		assertTrue("Does not correctly find captures for black bishop",
				Utils.checkLegalMoves(game, game.findCaptures(), "b8", "d6"));
		assertTrue("Allows capture into check",
				Utils.checkLegalMoves(game, game.findCaptures(), "a3", ""));
		assertTrue("Does not correctly find captures for black knight",
				Utils.checkLegalMoves(game, game.findCaptures(), "f5", "d4 d6"));
		
	}
	
	@Test
	public void findWhiteCapturesTest() {
		Game game = new Game(
				"1b4NK/8/B2B4/k1p2nn1/1R1RpP2/b7/8/Q2r2q1 w - -");
		assertTrue("Does not correctly find captures for white queen",
				Utils.checkLegalMoves(game, game.findCaptures(), "a1", "a3 d1"));
		assertTrue("Does not correctly find captures for white bishop",
				Utils.checkLegalMoves(game, game.findCaptures(), "d6", "c5 b8"));
		assertTrue("Does not correctly find captures for white rook",
				Utils.checkLegalMoves(game, game.findCaptures(), "d4", "d1 e4"));
		assertTrue("Does not correctly find captures for white pawn",
				Utils.checkLegalMoves(game, game.findCaptures(), "f4", "g5"));

		
	}

}
