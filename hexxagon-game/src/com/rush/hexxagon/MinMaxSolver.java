package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Random;

public class MinMaxSolver extends BaseSolver {
    private int mMaxDepth = 2;

    public MinMaxSolver(int maxDepth) {
        mMaxDepth = maxDepth;
    }

    @Override
    protected void evaluateMoves(GameBoard board, byte color, ArrayList<Move> moves) {
//        GameBoard b = new GameBoard();
//        int baseValue = board.getValue(color);
//        byte foeColor = (color == GameBoard.CELL_BLACK) ? GameBoard.CELL_WHITE : GameBoard.CELL_BLACK;
//
//        Random rnd = new Random();
//        int bestValue = Integer.MIN_VALUE;
//        int nMax = 0;
//        Move bestMove = null;
//        for (Move move : moves) {
//            mTotalMovesSearched++;
//            int value;
//            b.copy(board);
//            int numAdded = b.move(move.from, move.to);
//            if (depth <= 1) {
//                value = numAdded + baseValue;
//            } else {
//                Move m = getBestMove(b, foeColor, depth - 1);
//                value = (m != null) ? -m.value : Integer.MAX_VALUE;
//            }
//            if (value > bestValue) {
//                bestValue = value;
//                bestMove = move;
//                nMax = 1;
//            } else if (value == bestValue) {
//                // perform "reservoir sampling" to choose the random move
//                // from the best ones
//                nMax++;
//                if (rnd.nextInt(nMax) == 0) {
//                    bestMove = move;
//                }
//            }
//        }
//        bestMove.value = bestValue;
//
//        return bestMove;
    }

    public Move getBestMove(GameBoard board, byte color, int depth) {
        ArrayList<Move> moves = board.getPossibleMoves(color);
        if (moves.size() == 0) {
            return null;
        }

        GameBoard b = new GameBoard();
        int baseValue = board.getValue(color);
        byte foeColor = GameBoard.getFoeColor(color);

        Random rnd = new Random();
        int bestValue = Integer.MIN_VALUE;
        int nMax = 0;
        Move bestMove = null;
        for (Move move : moves) {
            mTotalMovesSearched++;
            int value;
            b.copy(board);
            int numAdded = b.move(move.from, move.to);
            if (depth <= 1) {
                value = numAdded + baseValue;
            } else {
                Move m = getBestMove(b, foeColor, depth - 1);
                value = (m != null) ? -m.value : Integer.MAX_VALUE;
            }
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
                nMax = 1;
            } else if (value == bestValue) {
                // perform "reservoir sampling" to choose the random move
                // from the best ones
                nMax++;
                if (rnd.nextInt(nMax) == 0) {
                    bestMove = move;
                }
            }
        }
        bestMove.value = bestValue;

        return bestMove;
    }

    public Move getBestMove(GameBoard board, byte color) {
        return getBestMove(board, color, mMaxDepth);
    }
}
