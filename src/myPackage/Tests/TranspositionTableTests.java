package myPackage.Tests;

import static org.junit.Assert.*;
import myPackage.Game;
import myPackage.TranspositionTable;
import myPackage.Move;

import org.junit.Test;

public class TranspositionTableTests {

	@Test
	public void test() {
		Game game = new Game();
		TranspositionTable t = new TranspositionTable(32);
		assertTrue("TranspositionTable should return null if"
				+ " the position is not found",
				t.get(game) == null);
		t.put(game, new Move("c2c3"));
		assertTrue("Transposition table doesn't save positions",
				new Move("c2c3").equals(t.get(game)));
		t.put(game, new Move("c2c4"));
		assertTrue("TranspsitionTable entry not "
				+ "overwritten correctly",
				new Move("c2c4").equals(t.get(game)));
	}

}
