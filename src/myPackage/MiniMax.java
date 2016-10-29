package myPackage;

public final class MiniMax {
	
	private MiniMax() {
		
	}
	
	// public Move findBestMove(int depth) {
		// if (sideToMove == 'w') {
		// return findBestMoveWhite(depth);
		// } else {
		// return findBestMoveBlack(depth);
		// }
		// }

		// public Move findBestMoveWhite(int depth) {
		// ArrayList<Move> moves = generateLegalMoves();
		//
		// int maxValue = Integer.MIN_VALUE;
		// Move bestMove = null;
		// for (int i = 0; i < moves.size(); i++) {
		// // for (int i = 0; i < 2; i++){
		// Move move = moves.get(i);
		// // Move move = moves.get(new Random().nextInt(moves.size()));
		// int moveValue = evaluateMoveWhite(move, depth - 1, maxValue,
		//INTEGER.MAX_VALUE);
		// if (moveValue > maxValue) {
		// maxValue = moveValue;
		// bestMove = move;
		// }
		// }
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("W, depth %d: %f\n", depth, maxValue);
		// if (bestMove != null){
		// return bestMove;
		// } else {
		// return moves.get(0);
		// }
		// }
		//
		// public Move findBestMoveBlack(int depth) {
		// ArrayList<Move> moves = generateLegalMoves();
		//
		// int minValue = Integer.MAX_VALUE;
		// Move bestMove = null;
		// for (int i = 0; i < moves.size(); i++) {
		// // for (int i = 0; i < 2; i++){
		// Move move = moves.get(i);
		// // Move move = moves.get(new Random().nextInt(moves.size()));
		// int moveValue = evaluateMoveBlack(move, depth - 1,
		// Integer.MIN_VALUE, minValue);
		//
		// if (moveValue < minValue) {
		// minValue = moveValue;
		// bestMove = move;
		// }
		// }
		// System.out.println(bestMove.convertToUCIFormat());
		// System.out.printf("B, depth %d: %f\n", depth, minValue);
		// if (bestMove != null){
		// return bestMove;
		// } else {
		// return moves.get(0);
		// }
		// }
		//
		// private int evaluateMoveWhite(Move whiteMove, int depth, int alpha,
		// int beta) {
		// Game newPosition = new Game(this, whiteMove);
		// if (depth == 0) {
		// //System.out.println(whiteMove.convertToUCIFormat());
		// //System.out.printf("W depth %d: %f\n", depth,
		// newPosition.evaluateBoard());
		// return newPosition.evaluateBoard();
		// } else {
		// // to evaluate whites move we must evaluate black's response
		// // Black should pick the move with the minimum value
		// ArrayList<Move> blackMoves = newPosition.generateLegalMoves();
		// int minValue = INTEGER.MAX_VALUE;
		// for (int i = 0; i < blackMoves.size(); i++) {
		// // for (int i = 0; i < 2; i++){
		// Move blackMove = blackMoves.get(i);
		// // Move blackMove = blackMoves.get(new
		// // Random().nextInt(blackMoves.size()));
		// int moveValue = newPosition.evaluateMoveBlack(blackMove, depth - 1,
		// alpha, Math.min(minValue, beta));
		//
		// if (moveValue < minValue) {
		// minValue = moveValue;
		// }
		// if (moveValue < alpha) {
		// //System.out.printf("Trim alpha %f depth %d\n", alpha, depth);
		// break;
		// }
		//
		// }
		// //System.out.println(whiteMove.convertToUCIFormat());
		// //System.out.printf("W, depth %d: %f\n", depth, minValue);
		// return minValue;
		// }
		//
		// }
		//
		// private int evaluateMoveBlack(Move blackMove, int depth, int alpha,
		// int beta) {
		// Game newPosition = new Game(this, blackMove);
		// if (depth == 0) {
		// // if the max depth has been reached we simply return
		// // the value of the board
		// //System.out.println(blackMove.convertToUCIFormat());
		// //System.out.printf("B, depth %d: %f\n", depth,
		// newPosition.evaluateBoard());
		// return newPosition.evaluateBoard();
		// } else {
		// // to evaluate blacks move we must evaluate whites's response
		// // White should pick the move with the maximum value
		// ArrayList<Move> whiteMoves = newPosition.generateLegalMoves();
		// int maxValue = INTEGER.MIN_VALUE;
		// for (int i = 0; i < whiteMoves.size(); i++) {
		// // for (int i = 0; i < 2; i++){
		// Move whiteMove = whiteMoves.get(i);
		// // Move whiteMove = whiteMoves.get(new
		// // Random().nextInt(whiteMoves.size()));
		// int moveValue = newPosition.evaluateMoveWhite(whiteMove, depth - 1,
		// Math.max(alpha, maxValue), beta);
		// if (moveValue > maxValue) {
		// maxValue = moveValue;
		// }
		// if (moveValue > beta) {
		// //System.out.printf("Trim beta %f depth %d\n", beta, depth);
		// break;
		// }
		// }
		// //System.out.println(blackMove.convertToUCIFormat());
		// //System.out.printf("B, depth %d: %f\n", depth, maxValue);
		// return maxValue;
		// }
		//
		// }

}
