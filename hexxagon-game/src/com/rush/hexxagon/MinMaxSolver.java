package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Random;

public class MinMaxSolver extends BaseSolver {
    private int mMaxDepth = 2;

    public MinMaxSolver(int maxDepth) {
        mMaxDepth = maxDepth;
    }

    public void evaluateMoves(HexxagonBoard board, byte color, ArrayList<HexxagonMove> moves) {
//        HexxagonBoard b = new HexxagonBoard();
//        int baseValue = board.getValue(color);
//        byte foeColor = (color == HexxagonBoard.CELL_BLACK) ? HexxagonBoard.CELL_WHITE : HexxagonBoard.CELL_BLACK;
//
//        Random rnd = new Random();
//        int bestValue = -HexxagonBoard.BIG_VALUE;
//        int nMax = 0;
//        HexxagonMove bestMove = null;
//        for (HexxagonMove move : moves) {
//            mTotalMovesSearched++;
//            int value;
//            b.copy(board);
//            int numAdded = b.move(move.from, move.to);
//            if (depth <= 1) {
//                value = numAdded + baseValue;
//            } else {
//                HexxagonMove m = getBestMove(b, foeColor, depth - 1);
//                value = (m != null) ? -m.value : HexxagonBoard.BIG_VALUE;
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

    public HexxagonMove getBestMove(HexxagonBoard board, byte color, int depth) {
        ArrayList<HexxagonMove> moves = board.getPossibleMoves(color);
        if (moves.size() == 0) {
            return null;
        }

        HexxagonBoard b = new HexxagonBoard();
        int baseValue = board.getValue(color);
        byte foeColor = HexxagonBoard.getFoeColor(color);

        Random rnd = new Random();
        int bestValue = -HexxagonBoard.BIG_VALUE;
        int nMax = 0;
        HexxagonMove bestMove = null;
        for (HexxagonMove move : moves) {
            mTotalMovesSearched++;
            int value;
            b.copy(board);
            int numAdded = b.move(move.from, move.to);
            if (depth <= 1) {
                value = numAdded + baseValue;
            } else {
                HexxagonMove m = getBestMove(b, foeColor, depth - 1);
                value = (m != null) ? -m.value : HexxagonBoard.BIG_VALUE;
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

    public HexxagonMove getBestMove(HexxagonBoard board, byte color) {
        return getBestMove(board, color, mMaxDepth);
    }
}
