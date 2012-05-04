package com.rush.hexxagon;

import java.util.ArrayList;

public class AlphaBetaSolver extends BaseSolver {
    private int mMaxDepth = 2;
    private boolean mOrderMoves = true;

    public AlphaBetaSolver(int maxDepth, boolean orderMoves) {
        mMaxDepth = maxDepth;
        mOrderMoves = orderMoves;
    }

    public int evaluateMoves(HexxagonBoard board, ArrayList<HexxagonMove> moves, byte color, int alpha, int beta, int depth) {
        byte foeColor = HexxagonBoard.getFoeColor(color);

        if (moves.size() == 0) {
            //  no possible moves left
            //  depending on the ratio of black/white/empty it can mean win or lose
            int nOwn = board.getNumCells(color);
            int nFoe = board.getNumCells(foeColor);
            int nEmpty = board.getNumCells(HexxagonBoard.CELL_EMPTY);
            return (nFoe + nEmpty >= nOwn) ? -HexxagonBoard.BIG_VALUE : HexxagonBoard.BIG_VALUE;
        }

        if (depth == 0) {
            //  leaf node, evaluate by counting the balls difference
            super.evaluateMoves(board, color, moves);
            mTotalMovesSearched += moves.size();
            HexxagonMove m = getBestMove(moves, false);
            return m.value;
        }

        if (mOrderMoves) {
            super.evaluateMoves(board, color, moves);
            sortMoves(moves);
        }

        alpha = -HexxagonBoard.BIG_VALUE;
        HexxagonBoard b = new HexxagonBoard();
        for (HexxagonMove move : moves) {
            mTotalMovesSearched++;
            b.copy(board);
            b.move(move.from, move.to);
            ArrayList<HexxagonMove> m = b.getPossibleMoves(foeColor);
            move.value = -evaluateMoves(b, m, foeColor, -beta, -alpha, depth - 1);
            alpha = Math.max(alpha, move.value);
            if (alpha >= beta) {
                //return alpha;
            }
        }
        return alpha;
    }

    @Override
    public HexxagonMove getBestMove(HexxagonBoard board, byte color) {
        ArrayList<HexxagonMove> moves = board.getPossibleMoves(color);
        evaluateMoves(board, moves, color, -HexxagonBoard.BIG_VALUE, HexxagonBoard.BIG_VALUE, mMaxDepth);
        return getBestMove(moves, false);
    }
}
