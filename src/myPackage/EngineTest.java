package myPackage;

import static org.junit.Assert.*;

import org.junit.Test;

public class EngineTest {

	@Test
	public void test() {
		Game game = new Game();
		String move = UCI.findMove();
	}

}
