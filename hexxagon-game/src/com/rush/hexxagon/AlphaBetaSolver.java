package com.rush.hexxagon;

import java.util.ArrayList;

public class AlphaBetaSolver extends BaseSolver {
    private int mMaxDepth = 2;
    private boolean mOrderMoves = true;

    public AlphaBetaSolver(int maxDepth, boolean orderMoves) {
        mMaxDepth = maxDepth;
        mOrderMoves = orderMoves;
    }

    public int evaluateMoves(GameBoard board, ArrayList<Move> moves, byte color, int alpha, int beta, int depth) {
        byte foeColor = GameBoard.getFoeColor(color);

        if (moves.size() == 0) {
            //  no possible moves left
            //  depending on the ratio of black/white/empty it can mean win or lose
            int nOwn = board.getNumCells(color);
            int nFoe = board.getNumCells(foeColor);
            int nEmpty = board.getNumCells(GameBoard.CELL_EMPTY);
            return (nFoe + nEmpty >= nOwn) ? -GameBoard.BIG_VALUE : GameBoard.BIG_VALUE;
        }

        if (depth == 0) {
            //  leaf node, evaluate by counting the balls difference
            super.evaluateMoves(board, color, moves);
            mTotalMovesSearched += moves.size();
            Move m = getBestMove(moves, false);
            return m.value;
        }

        if (mOrderMoves) {
            super.evaluateMoves(board, color, moves);
            sortMoves(moves);
        }

        alpha = -GameBoard.BIG_VALUE;
        GameBoard b = new GameBoard();
        for (Move move : moves) {
            mTotalMovesSearched++;
            b.copy(board);
            b.move(move.from, move.to);
            ArrayList<Move> m = b.getPossibleMoves(foeColor);
            move.value = -evaluateMoves(b, m, foeColor, -beta, -alpha, depth - 1);
            alpha = Math.max(alpha, move.value);
            if (alpha >= beta) {
                //return alpha;
            }
        }
        return alpha;
    }

    @Override
    public Move getBestMove(GameBoard board, byte color) {
        ArrayList<Move> moves = board.getPossibleMoves(color);
        evaluateMoves(board, moves, color, -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, mMaxDepth);
        return getBestMove(moves, false);
    }
}
