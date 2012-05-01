package com.rush.hexxagon;

public class AIPlayer extends Player {
    private BaseSolver mSolver = new AlphaBetaSolver(3, true);
    private int mTotalMoves = 0;
    private long mTotalTime = 0;

    public enum SolverType {
        AlphaBeta,
        AlphaBetaSort,
        MinMax,
        NegaScout,
        NegaScoutSort
    }

    public AIPlayer(byte color, SolverType solverType, int maxDepth) {
        super(color);
        switch (solverType) {
            case AlphaBeta: mSolver = new AlphaBetaSolver(maxDepth, false); break;
            case AlphaBetaSort: mSolver = new AlphaBetaSolver(maxDepth, true); break;
            case MinMax: mSolver = new MinMaxSolver(maxDepth); break;
            case NegaScout: mSolver = new NegaScoutSolver(maxDepth, false); break;
            case NegaScoutSort: mSolver = new NegaScoutSolver(maxDepth, true); break;
            default: mSolver = null;
        }
    }

    private class SolverTask implements Runnable {
        private Game _game;
        private BaseSolver _solver;
        SolverTask(Game game, BaseSolver solver) {
            _game = game;
            _solver = solver;
        }
        public void run() {
            GameBoard board = _game.getBoard();

            final long startTime = System.currentTimeMillis();
            Move bestMove = _solver.getBestMove(board, getColor());
            System.out.format("Color: %s; ", getColor() == GameBoard.CELL_BLACK ? "Black" : "White");
            if (bestMove != null) {
                board.move(bestMove.from, bestMove.to);
                System.out.format("Best move: %d->%d; ", bestMove.from, bestMove.to);
                System.out.format("Best value: %d; ", bestMove.value);
            }
            int totalMoves = _solver.getTotalMovesSearched();
            System.out.format("Moves: %d; ", totalMoves);
            final long deltaTime = System.currentTimeMillis() - startTime;
            mTotalMoves += totalMoves;
            mTotalTime += deltaTime;
            System.out.format("Time: %dms; Time per move: %fms\n", deltaTime,
                    mTotalMoves > 0 ? (float)mTotalTime/mTotalMoves: 0.0f);

            _game.nextMove();
        }
    }

    public void startMove(Game game) {
        new Thread(new SolverTask(game, mSolver)).start();
    }

}
