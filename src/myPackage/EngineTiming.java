package myPackage;

import static org.junit.Assert.*;

import org.junit.Test;

public class EngineTiming {

	@Test
	public void timing() {
		Game game = new Game();
		for (int i = 0; i < 3; i++){
			game = new Game(game, NegaMax.findBestMove(game, 5, true));
		}
	}

}
