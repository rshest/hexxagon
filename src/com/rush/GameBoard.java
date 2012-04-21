package com.rush;

import java.util.ArrayList;

public class GameBoard {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;
    public static final int NUM_CELLS = WIDTH * HEIGHT;

    public static final byte CELL_NONE = 0;
    public static final byte CELL_EMPTY = 1;
    public static final byte CELL_BLACK = 2;
    public static final byte CELL_WHITE = 3;

    // game board cells array
    private byte[] mCells;

    public GameBoard() {
        mCells = new byte[NUM_CELLS];
    }

    public GameBoard(GameBoard b) {
        mCells = b.mCells.clone();
    }

    public void init(String layout) {
        assert (layout.length() == NUM_CELLS);
        for (int i = 0; i < NUM_CELLS; i++) {
            switch (layout.charAt(i)) {
            case '.':
                mCells[i] = CELL_EMPTY;
                break;
            case '*':
                mCells[i] = CELL_BLACK;
                break;
            case 'O':
                mCells[i] = CELL_WHITE;
                break;
            default:
                mCells[i] = CELL_NONE;
                break;
            }
        }
    }

    public byte cell(int i, int j) {
        return mCells[i + j * WIDTH];
    }

    public byte cell(int idx) {
        return mCells[idx];
    }

    public boolean isCellFree(int idx) {
        return (mCells[idx] != GameBoard.CELL_BLACK && mCells[idx] != GameBoard.CELL_WHITE);
    }

    public void setCell(int idx, byte val) {
        mCells[idx] = val;
    }

    public int getNumCells(byte type) {
        int res = 0;
        for (byte c : mCells) {
            if (c == type)
                res++;
        }
        return res;
    }

    public boolean move(int from, int to) {
        int dist = HexGridCell.walkDistance(from % GameBoard.WIDTH, from
                / GameBoard.WIDTH, to % GameBoard.WIDTH, to / GameBoard.WIDTH);
        if (from > 0 && to > 0 && mCells[to] == GameBoard.CELL_EMPTY
                && dist <= 2) {
            byte myCell = mCells[from];
            setCell(to, myCell);
            if (dist == 2) {
                setCell(from, GameBoard.CELL_EMPTY);
            }
            captureNeighbors(to);
            return true;
        }
        return false;
    }

    public void captureNeighbors(int to) {
        int cI = to % WIDTH;
        int cJ = to / WIDTH;
        byte myCell = mCells[to];
        byte foeCell = (myCell == GameBoard.CELL_BLACK) ? GameBoard.CELL_WHITE
                : GameBoard.CELL_BLACK;
        for (int i = 0; i < HexGridCell.NUM_NEIGHBORS; i++) {
            int nI = HexGridCell.getNeighborI(cI, cJ, i);
            int nJ = HexGridCell.getNeighborJ(cI, cJ, i);
            if (cell(nI, nJ) == foeCell) {
                setCell(nI + nJ * WIDTH, myCell);
            }
        }
    }

    public ArrayList<Move> getPossibleMoves(byte player) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (int j = 0; j < GameBoard.HEIGHT; j++) {
            for (int i = 0; i < GameBoard.WIDTH; i++) {
                int cellIdx = i + j * GameBoard.WIDTH;
                if (mCells[cellIdx] == GameBoard.CELL_EMPTY) {
                    // the cell can be moved to
                    // check the near neighbors (a single one of the is enough)
                    for (int k = 0; k < HexGridCell.NUM_NEIGHBORS; k++) {
                        int ni = HexGridCell.getNeighborI(i, j, k);
                        int nj = HexGridCell.getNeighborJ(i, j, k);
                        if (ni >= 0 && nj >= 0 && ni < GameBoard.WIDTH
                                && nj < GameBoard.HEIGHT) {
                            int n = ni + nj * GameBoard.WIDTH;
                            if (mCells[n] == player) {
                                moves.add(new Move(n, cellIdx));
                                break;
                            }
                        }
                    }
                    for (int k = 0; k < HexGridCell.NUM_NEIGHBORS2; k++) {
                        int ni = HexGridCell.getNeighbor2I(i, j, k);
                        int nj = HexGridCell.getNeighbor2J(i, j, k);
                        if (ni >= 0 && nj >= 0 && ni < GameBoard.WIDTH
                                && nj < GameBoard.HEIGHT) {
                            int n = ni + nj * GameBoard.WIDTH;
                            if (mCells[n] == player) {
                                moves.add(new Move(n, cellIdx));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    public byte getWinner(byte currentMoveColor) {
        int numEmpty = getNumCells(CELL_EMPTY);
        int numMoves = getPossibleMoves(currentMoveColor).size();

        int numB = getNumCells(CELL_BLACK);
        int numW = getNumCells(CELL_WHITE);

        if (numMoves == 0) {
            if (currentMoveColor == CELL_BLACK)
                numW += numEmpty;
            else
                numB += numEmpty;
            numEmpty = 0;
        }

        if (numEmpty > 0)
            return CELL_EMPTY;
        return (numB > numW) ? CELL_BLACK : CELL_WHITE;
    }

    // evaluates the board value for the given player
    public int getValue(byte color) {
        byte foeColor = (color == CELL_BLACK) ? CELL_WHITE : CELL_BLACK;
        return getNumCells(color) - getNumCells(foeColor);
    }
}
