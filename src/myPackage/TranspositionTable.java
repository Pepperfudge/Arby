package myPackage;

public class TranspositionTable {
	private static final int BYTES_IN_MB = 1000000;
	private static final int MEMORY_OVERHEAD_SIZE = 1000000;
	private static final double ARRAY_ENTRY_SIZE = 440.055;
	private Move[] bestMoves;
	private Game[] storedPositions;
	private int arraySize;
	private float collisions = 0;
	private float entries = 0;
	
	
	public TranspositionTable(int hashSize){
		arraySize = (int) Math.floor((hashSize*BYTES_IN_MB - MEMORY_OVERHEAD_SIZE)
				/ ARRAY_ENTRY_SIZE); 
		bestMoves = new Move[arraySize];
		storedPositions = new Game[arraySize];
	}
	
	public Move get(Game position){
		int index = findIndexOf(position);
		Game storedPosition = storedPositions[index];
		if (position.equals(storedPosition)){
			return bestMoves[index];
		} else {
			return null;
		}
	}
	
	public void put(Game position, Move bestMove){
//		entries++;
		int index = findIndexOf(position);
//		if (storedPositions[index] != null){
//			collisions++;
//		}
		storedPositions[index] = position;
		bestMoves[index] = bestMove;
	}
	
	public float percentUsed(){
		float total = 0;
		for (int i = 0; i<arraySize; i++){
			if (storedPositions[i] != null){
				total += 1;
			}
		}
		return total/arraySize * 100;
	}
	
	public float percentCollisions(){
		return collisions/entries * 100;
	}
	
	private int findIndexOf(Game position){
		return Math.abs(position.hashCode()) % arraySize;
	}
}
