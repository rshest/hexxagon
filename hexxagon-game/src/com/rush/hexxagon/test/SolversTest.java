package com.rush.hexxagon.test;

import com.rush.hexxagon.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SolversTest {
    private static final String BOARD =
            "                " +
            "                " +
            "                " +
            "                " +
            "      O. .O     " +
            "      .....     " +
            "      .....     " +
            "        *       " +
            "                " +
            "                " +
            "                " +
            "                ";

    @Test
    public void testEvaluateMoves() throws Exception {
        GameBoard board = new GameBoard();
        board.init(BOARD);
        ArrayList<Move> moves = board.getPossibleMoves(GameBoard.CELL_WHITE);

        Assert.assertEquals(10, moves.size());

        AlphaBetaSolver solver = new AlphaBetaSolver(3, true);
        solver.evaluateMoves(board, moves, GameBoard.CELL_WHITE,
                -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, 0);
        Move m = BaseSolver.getBestMove(moves, false);
        Assert.assertEquals(2, m.value);

        solver.evaluateMoves(board, moves, GameBoard.CELL_WHITE,
                -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, 1);
        m = BaseSolver.getBestMove(moves, false);
        Assert.assertEquals(0, m.value);
    }

    private static final String GAME_TREE =
            "B.D.1=6, B.D.2=5, B.D.3=3, B.E.1=7, B.E.2=0, C.F.1=1, C.F.2=4, C.F.3=2, C.G.1=8, C.G.2=9";

    interface GameBoardInterface {
        public ArrayList<MoveInterface> getPossibleMoves(byte playerID);
        int evaluate(byte playerID);
        boolean move(MoveInterface move);
    }

    interface MoveInterface {

    }

    private class FakeGame {
        private FakeBoard mRootBoard = new FakeBoard(this);
        FakeGame(String gameTree) {

        }
    }

    private class FakeBoard implements GameBoardInterface {
        FakeGame mGame;
        FakeBoard(FakeGame game) {
            mGame = game;
        }

        @Override
        public ArrayList<MoveInterface> getPossibleMoves(byte playerID) {
            return null;
        }

        @Override
        public int evaluate(byte playerID) {
            return 0;
        }

        @Override
        public boolean move(MoveInterface move) {
            return false;
        }
    }

    private class FakeMove implements MoveInterface {

    }

}
