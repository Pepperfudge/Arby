package myPackage;

import static org.junit.Assert.*;

import org.junit.Test;

public class EngineTest {

	@Test
	public void test() {
		Game game = new Game();
		game.findBestMove(4,false);
	}
	
	@Test
	public void quiescenceTest(){
		/*At depth 1 queen sees it can take a rook. But with a quiescence 
		 * check it should see that its queen will be taken back so the 
		 * capture is not a good move
		 */
		Game game = new Game("1k6/2r5/8/8/8/2Q5/8/1K6 w - -");
		assertFalse("Engine doesn't see past horizon",
				(game.findBestMove(1,true).convertToUCIFormat().equals("c3c7")));
	}

}
