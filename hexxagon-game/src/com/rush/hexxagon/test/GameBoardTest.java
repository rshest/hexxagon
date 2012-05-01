package com.rush.hexxagon.test;


import com.rush.hexxagon.GameBoard;
import com.rush.hexxagon.Move;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class GameBoardTest {
    private static final String BOARD =
            "                "+
            "                "+
            "                "+
            "                "+
            "       ...      "+
            "     .OO.OO.    "+
            "     ..*O*..    "+
            "      *...*     "+
            "        .       "+
            "                "+
            "                "+
            "                ";

    @Test
    public void testGetMoveAdded() throws Exception {
        GameBoard board = new GameBoard();
        board.init(BOARD);

        ArrayList<Move> moves = board.getPossibleMoves(GameBoard.CELL_WHITE);
        for (Move move: moves) {
            GameBoard newBoard = new GameBoard(board);
            byte color = board.cell(move.from);
            int numAdded = board.getMoveAdded(move.from, move.to);
            int numAdded1 = newBoard.move(move.from, move.to);
            int cellDiff = newBoard.getNumCells(color) - board.getNumCells(color);
            Assert.assertEquals(numAdded, numAdded1);
            Assert.assertEquals(numAdded, cellDiff);
        }
    }
}
