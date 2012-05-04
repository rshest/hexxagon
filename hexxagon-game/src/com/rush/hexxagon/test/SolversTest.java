package com.rush.hexxagon.test;

import com.rush.hexxagon.AlphaBetaSolver;
import com.rush.hexxagon.BaseSolver;
import com.rush.hexxagon.HexxagonBoard;
import com.rush.hexxagon.HexxagonMove;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

interface GameBoard {
    static final int BIG_VALUE = 1000000;

    ArrayList<GameMove> getPossibleMoves(byte playerID);
    int evaluate(byte playerID, boolean canMove);
    boolean move(GameMove move);

    byte getOtherPlayerID(byte playerID);

    void copy(GameBoard board);
    GameBoard clone();
}

interface GameMove {

}

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
        HexxagonBoard board = new HexxagonBoard();
        board.init(BOARD);
        ArrayList<HexxagonMove> moves = board.getPossibleMoves(HexxagonBoard.CELL_WHITE);

        Assert.assertEquals(10, moves.size());

        AlphaBetaSolver solver = new AlphaBetaSolver(3, true);
        solver.evaluateMoves(board, moves, HexxagonBoard.CELL_WHITE,
                -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, 0);
        HexxagonMove m = BaseSolver.getBestMove(moves, false);
        Assert.assertEquals(2, m.value);

        solver.evaluateMoves(board, moves, HexxagonBoard.CELL_WHITE,
                -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, 1);
        m = BaseSolver.getBestMove(moves, false);
        Assert.assertEquals(0, m.value);
    }

    private static final String GAME_TREE =
            "B.D.1=-6, B.D.2=-5, B.D.3=-3, B.E.1=-7, B.E.2=0, " +
            "C.F.1=-1, C.F.2=-4, C.F.3=-2, C.G.1=-8, C.G.2=-9";


    private static int evalBoardNegaMax(GameBoard board, byte playerID, int depth) {
        ArrayList<GameMove> moves = board.getPossibleMoves(playerID);

        if (moves.size() == 0) return board.evaluate(playerID, false);

        GameBoard b = board.clone();
        int val = -GameBoard.BIG_VALUE;
        byte otherPlayerID = board.getOtherPlayerID(playerID);
        for (GameMove move: moves) {
            b.copy(board);
            b.move(move);
            val = Math.max(val, -evalBoardNegaMax(b, otherPlayerID, depth + 1));
        }
        return val;
    }

    @Test
    public void testNegaMax() throws Exception {
        MockGame game = new MockGame(GAME_TREE);
        final byte playerID = 0;

        int minMaxVal = evalBoardNegaMax(game.mRootBoard, playerID, 0);
        Assert.assertEquals(6, minMaxVal);
        Assert.assertEquals(0, game.mRootBoard.position.countVisited(false));
    }

    private static int evalBoardAlphaBeta(GameBoard board, byte playerID,
                                          int alpha, int beta, int depth) {
        ArrayList<GameMove> moves = board.getPossibleMoves(playerID);

        if (moves.size() == 0) return board.evaluate(playerID, false);

        GameBoard b = board.clone();
        byte otherPlayerID = board.getOtherPlayerID(playerID);
        for (GameMove move: moves) {
            b.copy(board);
            b.move(move);
            int curVal = -evalBoardAlphaBeta(b, otherPlayerID, -beta, -alpha, depth + 1);
            alpha = Math.max(alpha, curVal);
            if (alpha >= beta) {
                //  a cutoff
                return alpha;
            }
        }
        return alpha;
    }

    @Test
    public void testAlphaBeta() throws Exception {
        MockGame game = new MockGame(GAME_TREE);
        MockGame.Position pos = game.mRootBoard.position;
        final byte playerID = 0;

        int minMaxVal = evalBoardAlphaBeta(game.mRootBoard, playerID,
                -GameBoard.BIG_VALUE, GameBoard.BIG_VALUE, 0);
        Assert.assertEquals(6, minMaxVal);
        Assert.assertEquals(4, pos.countVisited(false));
        Assert.assertEquals(true, pos.find("B.D.1").visited);

        Assert.assertEquals(false, pos.find("B.E.2").visited);
        Assert.assertEquals(false, pos.find("C.G").visited);
        Assert.assertEquals(false, pos.find("C.G.1").visited);
        Assert.assertEquals(false, pos.find("C.G.2").visited);
    }



}
