package com.rush;

public class HumanPlayer extends Player {

    public HumanPlayer(byte cellColor) {
        super(cellColor);
    }

    public void onClickCell(int cellIdx, Hexxagon game) {
        GameBoard board = game.getBoard();
        byte cellContents = board.cell(cellIdx);
        if (game.getSelectedCell() == cellIdx) {
            // unselect the cell
            game.selectCell(-1);
        } else if (board.isCellFree(cellIdx)) {
            boolean moved = board.move(game.getSelectedCell(), cellIdx);
            if (moved) {
                game.nextMove();
                game.selectCell(-1);
            }
        } else if (cellContents == getColor()) {
            game.selectCell(cellIdx);
        }
    }

}
