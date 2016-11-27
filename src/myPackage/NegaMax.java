package myPackage;


import java.util.ArrayList;

public final class NegaMax {

	//static class so constructor is private
	private TranspositionTable pastSearches; 
	private boolean quiesce;

	public NegaMax(int hashSize, boolean quiesce){
		pastSearches = new TranspositionTable(hashSize);
		this.quiesce = quiesce;
	}
	
	private static final int CHECKMATE_VALUE = -20000; 
	
	public Move findBestMove(Game position, int depth) {
		ArrayList<Move> moves = position.generateLegalMoves();

		int maxValue = CHECKMATE_VALUE;
		Move bestMove = null;
		for (int i = 0; i < moves.size(); i++) {
			// for (int i = 0; i < 2; i++){
			Move move = moves.get(i);
			// Move move = moves.get(new Random().nextInt(moves.size()));
			Game newPosition = new Game(position, move);
			int moveValue = -evaluatePosition(newPosition, depth - 1, 
					CHECKMATE_VALUE, -maxValue);
			// int moveValue = -evaluateMove(move,depth - 1);
			if (moveValue >= maxValue) {
				maxValue = moveValue;
				bestMove = move;
			}
		}
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("W, depth %d: %f\n", depth, maxValue);
//		System.out.println(pastSearches.percentUsed());
//		System.out.println(pastSearches.percentCollisions());
		return bestMove;
	}
	
	private int evaluatePosition(Game position, int depth, 
			int alpha, int beta) {
		if (depth == 0) {
			int score;
			if (quiesce){
				score = quiesce(position, alpha, beta);
			} else {
				score = position.evaluateBoard();
				if (position.sideToMove == 'b'){
					score = -1*score;
				}
			}
			return score;
		} else {
			ArrayList<Move> opponentMoves = position.generateLegalMoves();
			if (opponentMoves.isEmpty()){
				if (position.isKingInCheck()){
					return CHECKMATE_VALUE - depth;
				} else {
					return 0; //stalemate 
				}
			}
			Move lastBestMove = pastSearches.get(position);
			int maxValue = CHECKMATE_VALUE;
			if (lastBestMove != null){
				Game newPosition = new Game(position, lastBestMove);
				maxValue = -evaluatePosition(newPosition, depth - 1, 
						-beta, -alpha);
				if (maxValue > beta) {
					return maxValue;
				}
			}
			Move bestMove = lastBestMove;
			for (int i = 0; i < opponentMoves.size(); i++) {
				Move opponentMove = opponentMoves.get(i);
				if (opponentMove.equals(lastBestMove)){
					//lastBestMove is the first move we searched before the for loop
					//so it would be redundant to do it again
					continue;
				}
				Game newPosition = new Game(position, opponentMove);
				int moveValue = -evaluatePosition(newPosition, depth - 1, -beta,
						-Math.max(alpha, maxValue));
				if (moveValue > beta) {
					pastSearches.put(position, opponentMove);
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
					bestMove = opponentMove;
				}
			}
			pastSearches.put(position, bestMove);
			return maxValue;
		}
	}
	
	private int quiesce(Game position, int alpha, int beta) {
		int stand_pat = position.evaluateBoard();
		if (position.sideToMove == 'b') {
			stand_pat = -1 * stand_pat;
		}
		if (stand_pat >= beta) {
			return stand_pat;
		}
		if (stand_pat > alpha) {
			alpha = stand_pat;
		}
		ArrayList<Move> captures = position.findCaptures();

		
		int maxValue = CHECKMATE_VALUE;
		Move lastBestCapture = pastSearches.get(position);

		Move bestCapture = null;
		if (lastBestCapture != null ){
			int row = lastBestCapture.newRow;
			int col = lastBestCapture.newColumn;
			if (position.getPieceAt(row, col) != 'x'){
				Game newPosition = new Game(position, lastBestCapture);
				maxValue = -quiesce(newPosition, -beta, -alpha);
				if (maxValue > beta) {
					return maxValue;
				}
				bestCapture = lastBestCapture;
			} 
		}
		
		for (int i = 0; i < captures.size(); i++) {
			// for (int i = 0; i < captures.size(); i++){
			Move capture = captures.get(i);
			if (capture.equals(lastBestCapture)){
				continue;
			}
			// Move capture = captures.get(
			// (new Random()).nextInt(captures.size()));
			
			// delta pruning
			char piece = position.getPieceAt(capture.currRow,capture.currColumn);
			char pieceCaptured = position.getPieceAt(capture.newRow,capture.newColumn);
			Integer pieceValue;
			if ((piece == 'P' || piece == 'p') && Math.abs(capture.currColumn - capture.newColumn) == 1
					&& pieceCaptured == 'x'
					&& (capture.newRow == 2 || capture.newRow == 5)) {
				pieceValue = 100;
			} else {
				pieceValue = Utils.pieceValues.get(pieceCaptured);
				if (pieceValue == null) {
					String errorMessage = String.format(
							"%s not a valid piece to take. \n move %s" + "\n position \n %s", pieceCaptured,
							capture.convertToUCIFormat(), position);
					throw new RuntimeException(errorMessage);
				}
			}
			if (( pieceValue + stand_pat + 70) > alpha) {
//				System.out.println(capture.convertToUCIFormat());
//				System.out.println(position);
//				System.out.println();
				// System.out.println(pieceCaptured);
				Game newPosition = new Game(position, capture);
				int moveValue = -quiesce(newPosition, -beta, -alpha);
				if (moveValue >= beta) {
					pastSearches.put(position, capture);
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
					bestCapture= capture;
				}
				if (moveValue > alpha){
					alpha = moveValue;
				}
			}
		}
		if (bestCapture != null){
			pastSearches.put(position, bestCapture);
		}
		return Math.max(maxValue, stand_pat);

	}

}
