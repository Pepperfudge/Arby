package myPackage.Tests;




import org.junit.Test;

import myPackage.Game;
import myPackage.NegaMax;

public class EngineTiming {

	@Test
	public void timing() {
		
		NegaMax nm = new NegaMax(128,true);
		//last time: 22.427 seconds
		//boosts to king safety
		//21.574
		// use best capture in quiesce
		//17.89 seconds
		// update best move in quiesce
		Game game = new Game();
		for (int i = 0; i < 10; i++){
			game = new Game(game, nm.findBestMove(game, 5));
		}
//		Game[] storedPositions = new Game[1000000000]; // to create a memory error
		
	}

}
