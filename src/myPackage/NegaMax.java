package myPackage;

import java.util.ArrayList;

public final class NegaMax {

	//static class so constructor is private
	private NegaMax(){
		
	}
	
	public static Move findBestMove(Game position, int depth, boolean quiesce) {
		ArrayList<Move> moves = position.generateLegalMoves();

		double maxValue = Double.NEGATIVE_INFINITY;
		Move bestMove = null;
		for (int i = 0; i < moves.size(); i++) {
			// for (int i = 0; i < 2; i++){
			Move move = moves.get(i);
			// Move move = moves.get(new Random().nextInt(moves.size()));
			Game newPosition = new Game(position, move);
			double moveValue = -evaluatePosition(newPosition, depth - 1, 
					Double.NEGATIVE_INFINITY, -maxValue, quiesce);
			// double moveValue = -evaluateMove(move,depth - 1);
			if (moveValue >= maxValue) {
				maxValue = moveValue;
				bestMove = move;
			}
		}
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("W, depth %d: %f\n", depth, maxValue);
		return bestMove;
	}
	
	private static double evaluatePosition(Game position, int depth, 
			double alpha, double beta, boolean quiesce) {

		if (depth == 0) {
			// System.out.println(move.convertToUCIFormat());
			// System.out.println("quiesce");
			// System.out.println(newPosition);
			double score;
			if (quiesce){
				score = quiesce(position, alpha, beta);
			} else {
				score = position.evaluateBoard();
				if (position.sideToMove == 'b'){
					score = -1*score;
				}
			}
			// System.out.printf("%s depth %d: %f\n", newPosition.sideToMove,
			// depth, score);
			return score;
		} else {
			// System.out.printf("depth %d, alpha %f, beta %f \n", depth, alpha,
			// beta);
			ArrayList<Move> opponentMoves = position.generateLegalMoves();
			double maxValue = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < opponentMoves.size(); i++) {
				// for (int i = 0; i < 2; i++){
				Move opponentMove = opponentMoves.get(i);
				Game newPosition = new Game(position, opponentMove);
				// Move opponentMove = opponentMoves.get(
				// new Random().nextInt(opponentMoves.size()));
				double moveValue = -evaluatePosition(newPosition, depth - 1, -beta,
						-Math.max(alpha, maxValue), quiesce);
				if (moveValue > beta) {
					// System.out.println(move.convertToUCIFormat());
					// System.out.printf("Trim beta %f depth %d\n", beta,
					// depth);
					// System.out.printf("W, depth %d: %f\n", depth, moveValue);
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
				}
			}
			// System.out.println(move.convertToUCIFormat());
			// System.out.printf("%s, depth %d: %f\n",
			// newPosition.sideToMove, depth, maxValue);
			return maxValue;
		}
	}
	
	private static double quiesce(Game position, double alpha, double beta) {
		double stand_pat = position.evaluateBoard();
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

		double maxValue = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < captures.size(); i++) {
			// for (int i = 0; i < captures.size(); i++){
			Move capture = captures.get(i);
			// Move capture = captures.get(
			// (new Random()).nextInt(captures.size()));
			// delta pruning
			char piece = position.getPieceAt(capture.currRow,capture.currColumn);
			char pieceCaptured = position.getPieceAt(capture.newRow,capture.newColumn);
			Integer pieceValue;
			if ((piece == 'P' || piece == 'p') && Math.abs(capture.currColumn - capture.newColumn) == 1
					&& pieceCaptured == 'x'
					&& (capture.newRow == 2 || capture.newRow == 5)) {
				pieceValue = 1;
			} else {
				pieceValue = Utils.pieceValues.get(pieceCaptured);
				if (pieceValue == null) {
					String errorMessage = String.format(
							"%s not a valid piece to take. \n move %s" + "\n position \n %s", pieceCaptured,
							capture.convertToUCIFormat(), position);
					throw new RuntimeException(errorMessage);
				}
			}
			if (((double) pieceValue + stand_pat + 0.7) > alpha) {

				// System.out.println(pieceCaptured);
				Game newPosition = new Game(position, capture);
				double moveValue = -quiesce(newPosition, -beta, -alpha);
				if (moveValue >= beta) {
					return moveValue;
				}
				if (moveValue > maxValue) {
					maxValue = moveValue;
				}
				if (moveValue > alpha) {
					alpha = moveValue;
				}
			}
		}
		return Math.max(maxValue, stand_pat);

	}

}
