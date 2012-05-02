package com.rush.hexxagon;

public class NegaScoutSolver extends BaseSolver {
    private int mMaxDepth = 2;
    private boolean mOrderMoves = true;

    public NegaScoutSolver(int maxDepth, boolean orderMoves) {
        mMaxDepth = maxDepth;
        mOrderMoves = orderMoves;
    }

    public Move getBestMove(GameBoard board, byte color, int alpha, int beta, int depth) {
        assert false; // not implemented !
        return null;
    }

    public Move getBestMove(GameBoard board, byte color) {
        Move m = getBestMove(board, color, -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, mMaxDepth);
        return m.from >= 0 ? m : null;
    }
}
