package myPackage;

//import static org.junit.Assert.*;

import org.junit.Test;

public class EngineTest {

	@Test
	public void test() {
		Game game = new Game();
		Move[] moves = {new Move("g1f3"),new Move("f7f6"), 
						new Move("b1c3"), new Move("c7c6"),
						new Move("e2e4")};
		for (int i = 0; i < 5; i++ ){
			game.findBestMove(4);
			game = new Game(game, moves[i]);
		}
	}

}
