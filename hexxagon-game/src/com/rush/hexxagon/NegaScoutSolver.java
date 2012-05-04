package com.rush.hexxagon;

public class NegaScoutSolver extends BaseSolver {
    private int mMaxDepth = 2;
    private boolean mOrderMoves = true;

    public NegaScoutSolver(int maxDepth, boolean orderMoves) {
        mMaxDepth = maxDepth;
        mOrderMoves = orderMoves;
    }

    public HexxagonMove getBestMove(HexxagonBoard board, byte color, int alpha, int beta, int depth) {
        assert false; // not implemented !
        return null;
    }

    public HexxagonMove getBestMove(HexxagonBoard board, byte color) {
        HexxagonMove m = getBestMove(board, color, -HexxagonBoard.BIG_VALUE, HexxagonBoard.BIG_VALUE, mMaxDepth);
        return m.from >= 0 ? m : null;
    }
}
