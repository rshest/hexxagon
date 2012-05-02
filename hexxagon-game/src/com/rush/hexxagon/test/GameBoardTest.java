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
            Assert.assertEquals(numAdded, numAdded1);

            int cellsAdded = newBoard.getNumCells(color) - board.getNumCells(color);
            Assert.assertEquals(numAdded, cellsAdded);

            int moveDiff = board.getMoveDiff(move.from, move.to);
            int oldDiff = board.getNumCells(color) - board.getNumCells(GameBoard.getFoeColor(color));
            int newDiff = newBoard.getNumCells(color) - newBoard.getNumCells(GameBoard.getFoeColor(color));
            int cellsDiff = newDiff - oldDiff;
            Assert.assertEquals(cellsDiff, moveDiff);
        }
    }
}
