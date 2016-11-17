package myPackage.Tests;




import org.junit.Test;

import myPackage.Game;
import myPackage.NegaMax;

public class EngineTiming {

	@Test
	public void timing() {
		
		NegaMax nm = new NegaMax(128,true);
		//last time: 10.713 seconds
		//hash used to look at lastBestMove first
		Game game = new Game();
		for (int i = 0; i < 5; i++){
			game = new Game(game, nm.findBestMove(game, 5));
		}
//		Game[] storedPositions = new Game[1000000000]; // to create a memory error
		
	}

}
