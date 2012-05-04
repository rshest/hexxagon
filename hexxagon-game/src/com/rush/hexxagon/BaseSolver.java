package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class BaseSolver {
    protected int mTotalMovesSearched = 0;
    private static Random mRandom = new Random();

    public int getTotalMovesSearched() {
        return mTotalMovesSearched;
    }

    public void evaluateMoves(HexxagonBoard board, byte color, ArrayList<HexxagonMove> moves) {
        int baseValue = board.getValue(color);
        for (HexxagonMove move : moves) {
            move.value = baseValue + board.getMoveDiff(move.from, move.to);
        }
    }

    public static void sortMoves(ArrayList<HexxagonMove> moves) {
        Collections.sort(moves, new Comparator<HexxagonMove>() {
            @Override
            public int compare(HexxagonMove o1, HexxagonMove o2) {
                return o2.value - o1.value;
            }
        });
    }

    public static HexxagonMove getBestMove(ArrayList<HexxagonMove> moves, boolean isOrdered) {
        HexxagonMove bestMove = null;
        if (isOrdered && moves.size() > 0) {
            int bestValue = moves.get(0).value;
            int nMoves = moves.size();
            for (int i = 1; i < nMoves; i++) {
                if (moves.get(i).value < bestValue) {
                    return moves.get(mRandom.nextInt(i));
                }
            }
            return moves.get(mRandom.nextInt(nMoves));
        }
        else {
            // if the moves are not ordered, then perform "reservoir sampling"
            // to choose the random move from the best ones
            int bestValue = -HexxagonBoard.BIG_VALUE;
            int nMax = 0;
            for (HexxagonMove move : moves) {
                if (move.value > bestValue) {
                    bestValue = move.value;
                    bestMove = move;
                    nMax = 1;
                } else if (move.value == bestValue) {
                    nMax++;
                    if (mRandom.nextInt(nMax) == 0) {
                        bestMove = move;
                    }
                }
            }
        }
        return bestMove;
    }

    public HexxagonMove getBestMove(HexxagonBoard board, byte color) {
        return null;
    }
}
