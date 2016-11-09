package myPackage;



import org.junit.Test;

public class EngineTiming {

	@Test
	public void timing() {
		//last time: 10.713 seconds
		//hash used to look at lastBestMove first
		Game game = new Game();
		for (int i = 0; i < 3; i++){
			game = new Game(game, NegaMax.findBestMove(game, 5, true));
		}
	}

}
