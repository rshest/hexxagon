package com.rush.hexxagon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AIPlayer extends Player {
    private int mTotalMoves;
    private Solver mSolver = new AlphaBetaSolver(4, true);

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

    private interface Solver {
        public Move getBestMove(GameBoard board, byte color);
    }

    public class MinMaxSolver implements Solver {
        private int _maxDepth = 2;

        public MinMaxSolver(int maxDepth) {
            _maxDepth = maxDepth;
        }

        public Move getBestMove(GameBoard board, byte color, int depth) {
            ArrayList<Move> moves = board.getPossibleMoves(color);
            if (moves.size() == 0) {
                return null;
            }

            GameBoard b = new GameBoard();
            int baseValue = board.getValue(color);
            byte foeColor = (color == GameBoard.CELL_BLACK) ? GameBoard.CELL_WHITE : GameBoard.CELL_BLACK;

            Random rnd = new Random();
            int bestValue = Integer.MIN_VALUE;
            int nMax = 0;
            Move bestMove = null;
            for (Move move : moves) {
                mTotalMoves++;
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
            return getBestMove(board, color, _maxDepth);
        }
    }

    public class AlphaBetaSolver implements Solver {
        private int _maxDepth = 2;
        private boolean _orderMoves = true;

        public AlphaBetaSolver(int maxDepth, boolean orderMoves) {
            _maxDepth = maxDepth;
            _orderMoves = orderMoves;
        }

        public Move getBestMove(GameBoard board, byte color, int alpha, int beta, int depth) {
            byte foeColor = (color == GameBoard.CELL_BLACK) ? GameBoard.CELL_WHITE : GameBoard.CELL_BLACK;
            ArrayList<Move> moves = board.getPossibleMoves(color);
            GameBoard b = new GameBoard();

            if (moves.size() == 0) {
                return null;
            }

            int baseValue = board.getValue(color);

            if (_orderMoves) {
                //  order moves
                for (Move move : moves) {
                    b.copy(board);
                    int numAdded = b.move(move.from, move.to);
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
            for (Move move : moves) {
                mTotalMoves++;
                int value;
                if (depth <= 1) {
                    value = move.value;
                } else {
                    b.copy(board);
                    b.move(move.from, move.to);
                    Move m = getBestMove(b, foeColor, -beta, -alpha, depth - 1);
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
            return getBestMove(board, color, Integer.MIN_VALUE, Integer.MAX_VALUE, _maxDepth);
        }
    }

    public class NegaScoutSolver implements Solver {
        private int _maxDepth = 2;
        private boolean _orderMoves = true;

        public NegaScoutSolver(int maxDepth, boolean orderMoves) {
            _maxDepth = maxDepth;
            _orderMoves = orderMoves;
        }

        public Move getBestMove(GameBoard board, byte color, int alpha, int beta, int depth) {
            assert false; // not implemented !

            byte foeColor = (color == GameBoard.CELL_BLACK) ? GameBoard.CELL_WHITE : GameBoard.CELL_BLACK;
            ArrayList<Move> moves = board.getPossibleMoves(color);
            GameBoard curBoard = new GameBoard();

            if (moves.size() == 0) {
                return null;
            }

            int baseValue = board.getValue(color);

            if (_orderMoves) {
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
                mTotalMoves++;
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
            Move m = getBestMove(board, color, Integer.MIN_VALUE, Integer.MAX_VALUE, _maxDepth);
            return m.from >= 0 ? m : null;
        }
    }

    private class SolverTask implements Runnable {
        private Game _game;
        private Solver _solver;
        SolverTask(Game game, Solver solver) {
            _game = game;
            _solver = solver;
        }
        public void run() {
            GameBoard board = _game.getBoard();

            mTotalMoves = 0;
            final long startTime = System.currentTimeMillis();
            Move bestMove = _solver.getBestMove(board, getColor());
            System.out.format("Color: %s; ", getColor() == GameBoard.CELL_BLACK ? "Black" : "White");
            if (bestMove != null) {
                board.move(bestMove.from, bestMove.to);
                System.out.format("Best move: %d->%d; ", bestMove.from, bestMove.to);
                System.out.format("Best value: %d; ", bestMove.value);
            }
            System.out.format("Total moves: %d; ", mTotalMoves);
            final long endTime = System.currentTimeMillis();
            System.out.format("Time: %dms; Time per move: %fms\n", endTime - startTime,
                    mTotalMoves > 0 ? (float)(endTime - startTime)/ mTotalMoves : 0.0f);

            _game.nextMove();
        }
    }

    public void startMove(Game game) {
        new Thread(new SolverTask(game, mSolver)).start();
    }

}
