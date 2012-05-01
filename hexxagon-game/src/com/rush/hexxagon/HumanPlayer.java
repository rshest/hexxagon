package com.rush.hexxagon;

public class HumanPlayer extends Player {

    public HumanPlayer(byte cellColor) {
        super(cellColor);
    }

    public void onClickCell(int cellIdx, Game game) {
        GameBoard board = game.getBoard();
        byte cellContents = board.cell(cellIdx);
        if (game.getSelectedCell() == cellIdx) {
            // unselect the cell
            game.selectCell(-1);
        } else if (board.isCellFree(cellIdx)) {
            int numAdded = board.move(game.getSelectedCell(), cellIdx);
            if (numAdded >= 0) {
                game.nextMove();
                game.selectCell(-1);
            }
        } else if (cellContents == getColor()) {
            game.selectCell(cellIdx);
        }
    }

}
