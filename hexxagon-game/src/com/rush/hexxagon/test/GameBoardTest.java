package com.rush.hexxagon.test;


import com.rush.hexxagon.HexxagonMove;
import com.rush.hexxagon.HexxagonBoard;
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
        HexxagonBoard board = new HexxagonBoard();
        board.init(BOARD);

        ArrayList<HexxagonMove> moves = board.getPossibleMoves(HexxagonBoard.CELL_WHITE);
        for (HexxagonMove move: moves) {
            HexxagonBoard newBoard = new HexxagonBoard(board);
            byte color = board.cell(move.from);
            int numAdded = board.getMoveAdded(move.from, move.to);

            int numAdded1 = newBoard.move(move.from, move.to);
            Assert.assertEquals(numAdded, numAdded1);

            int cellsAdded = newBoard.getNumCells(color) - board.getNumCells(color);
            Assert.assertEquals(numAdded, cellsAdded);

            int moveDiff = board.getMoveDiff(move.from, move.to);
            int oldDiff = board.getNumCells(color) - board.getNumCells(HexxagonBoard.getFoeColor(color));
            int newDiff = newBoard.getNumCells(color) - newBoard.getNumCells(HexxagonBoard.getFoeColor(color));
            int cellsDiff = newDiff - oldDiff;
            Assert.assertEquals(cellsDiff, moveDiff);
        }
    }
}
