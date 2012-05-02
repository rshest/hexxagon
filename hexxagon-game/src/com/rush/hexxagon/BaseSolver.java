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

    public void evaluateMoves(GameBoard board, byte color, ArrayList<Move> moves) {
        int baseValue = board.getValue(color);
        for (Move move : moves) {
            move.value = baseValue + board.getMoveDiff(move.from, move.to);
        }
    }

    public static void sortMoves(ArrayList<Move> moves) {
        Collections.sort(moves, new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                return o2.value - o1.value;
            }
        });
    }

    public static Move getBestMove(ArrayList<Move> moves, boolean isOrdered) {
        Move bestMove = null;
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
            int bestValue = -GameBoard.BIG_VALUE;
            int nMax = 0;
            for (Move move : moves) {
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

    public Move getBestMove(GameBoard board, byte color) {
        return null;
    }
}
