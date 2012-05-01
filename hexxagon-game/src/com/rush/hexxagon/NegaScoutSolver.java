package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class NegaScoutSolver extends BaseSolver {
    private int mMaxDepth = 2;
    private boolean mOrderMoves = true;

    public NegaScoutSolver(int maxDepth, boolean orderMoves) {
        mMaxDepth = maxDepth;
        mOrderMoves = orderMoves;
    }

    public Move getBestMove(GameBoard board, byte color, int alpha, int beta, int depth) {
        assert false; // not implemented !

        byte foeColor = GameBoard.getFoeColor(color);
        ArrayList<Move> moves = board.getPossibleMoves(color);
        GameBoard curBoard = new GameBoard();

        if (moves.size() == 0) {
            return null;
        }

        int baseValue = board.getValue(color);

        if (mOrderMoves) {
            //  order moves
            for (Move move : moves) {
                curBoard.copy(board);
                int numAdded = curBoard.move(move.from, move.to);
                move.value = baseValue + numAdded;
            }
            Collections.sort(moves, new Comparator<Move>() {
                @Override
                public int compare(Move o1, Move o2) {
                    return o2.value - o1.value;
                }
            });
        }

        Random rnd = new Random();
        int nMax = 0;
        Move bestMove = null;
        int b = beta;
        for (Move move : moves) {
            mTotalMovesSearched++;
            int value;
            if (depth <= 1) {
                value = move.value;
            } else {
                curBoard.copy(board);
                curBoard.move(move.from, move.to);
                Move m = getBestMove(curBoard, foeColor, -b, -alpha, depth - 1);
                value = (m != null) ? -m.value : Integer.MAX_VALUE;
                if (value > beta) {
                    move.value = value;
                    return move;
                }
            }
            if (value > alpha) {
                alpha = value;
                bestMove = move;
                nMax = 1;
            } else if (value == alpha) {
                // perform "reservoir sampling" to choose the random move
                // from the best ones
                nMax++;
                if (rnd.nextInt(nMax) == 0) {
                    bestMove = move;
                }
            }
        }
        if (bestMove != null) {
            bestMove.value = alpha;
        }

        return bestMove;
    }

    public Move getBestMove(GameBoard board, byte color) {
        Move m = getBestMove(board, color, Integer.MIN_VALUE, Integer.MAX_VALUE, mMaxDepth);
        return m.from >= 0 ? m : null;
    }
}
