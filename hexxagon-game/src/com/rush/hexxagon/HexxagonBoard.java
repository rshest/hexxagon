package com.rush.hexxagon;

import java.util.ArrayList;

public class HexxagonBoard {

    public static final int BIG_VALUE = 1000000;
    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;
    public static final int NUM_CELLS = WIDTH * HEIGHT;

    public static final byte CELL_NONE = 0;
    public static final byte CELL_EMPTY = 1;
    public static final byte CELL_BLACK = 2;
    public static final byte CELL_WHITE = 3;

    public static byte getFoeColor(byte color) {
        return (color == HexxagonBoard.CELL_BLACK) ? HexxagonBoard.CELL_WHITE : HexxagonBoard.CELL_BLACK;
    }

    // game board cells array
    private byte[] mCells;

    public HexxagonBoard() {
        mCells = new byte[NUM_CELLS];
    }

    public HexxagonBoard(HexxagonBoard b) {
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

    public void copy(HexxagonBoard b) {
        System.arraycopy(b.mCells, 0, mCells, 0, NUM_CELLS);
    }

    public static class Extents {
        public Extents() { left = 0; top = 0; bottom = HEIGHT - 1; right = WIDTH - 1; }
        public Extents(int _left, int _top, int _bottom, int _right) {
            left = _left; top = _top; bottom = _bottom; right = _right;
        }

        public int left, top, bottom, right;
    }

    public Extents getBoardExtents() {
        Extents r = new Extents(HexxagonBoard.WIDTH, HexxagonBoard.HEIGHT, 0, 0);
        for (int j = 0; j < HexxagonBoard.HEIGHT; j++) {
            for (int i = 0; i < HexxagonBoard.WIDTH; i++) {
                if (mCells[i + j* HexxagonBoard.WIDTH] != CELL_NONE) {
                   r.top = Math.min(r.top, j);
                   r.left = Math.min(r.left, i);
                   r.bottom = Math.max(r.bottom, j);
                   r.right = Math.max(r.right, i);
                }
            }
         }
        return r;
    }

    public byte cell(int i, int j) {
        return mCells[i + j * WIDTH];
    }

    public byte cell(int idx) {
        return mCells[idx];
    }

    public boolean isCellFree(int idx) {
        return (mCells[idx] != HexxagonBoard.CELL_BLACK && mCells[idx] != HexxagonBoard.CELL_WHITE);
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

    public int move(int from, int to) {
        int dist = HexGridCell.walkDistance(from % HexxagonBoard.WIDTH, from
                / HexxagonBoard.WIDTH, to % HexxagonBoard.WIDTH, to / HexxagonBoard.WIDTH);
        if (from >= 0 && to >= 0 && mCells[to] == HexxagonBoard.CELL_EMPTY && dist <= 2) {
            int numAdded = 1;
            byte myCell = mCells[from];
            setCell(to, myCell);
            if (dist == 2) {
                setCell(from, HexxagonBoard.CELL_EMPTY);
                numAdded = 0;
            }
            numAdded += captureNeighbors(to);
            return numAdded;
        }
        return -1;
    }

    public int getNumToCapture(int cellI, int cellJ, byte color) {
        int res = 0;
        for (int i = 0; i < HexGridCell.NUM_NEIGHBORS; i++) {
            int nI = HexGridCell.getNeighborI(cellI, cellJ, i);
            int nJ = HexGridCell.getNeighborJ(cellI, cellJ, i);
            if (inBoard(nI, nJ) && cell(nI, nJ) == color) {
                res++;
            }
        }
        return res;
    }

    public int getMoveAdded(short from, short to) {
        int toI = to % WIDTH;
        int toJ = to / WIDTH;
        int dist = HexGridCell.walkDistance(from % HexxagonBoard.WIDTH, from
                / HexxagonBoard.WIDTH, toI, toJ);
        if (from >= 0 && to >= 0 && mCells[to] == HexxagonBoard.CELL_EMPTY && dist <= 2 && dist > 0) {
            return 2 - dist + getNumToCapture(toI, toJ, getFoeColor(mCells[from]));
        }
        return 0;
    }

    public int getMoveDiff(short from, short to) {
        int toI = to % WIDTH;
        int toJ = to / WIDTH;
        int dist = HexGridCell.walkDistance(from % HexxagonBoard.WIDTH, from
                / HexxagonBoard.WIDTH, toI, toJ);
        if (from >= 0 && to >= 0 && mCells[to] == HexxagonBoard.CELL_EMPTY && dist <= 2 && dist > 0) {
            //  each captured cell counts for a difference of 2
            return 2 - dist + 2*(getNumToCapture(toI, toJ, getFoeColor(mCells[from])));
        }
        return 0;
    }

    public int captureNeighbors(int to) {
        int cI = to % WIDTH;
        int cJ = to / WIDTH;
        byte color = mCells[to];
        int numCaptured = 0;
        byte foeColor = HexxagonBoard.getFoeColor(color);
        for (int i = 0; i < HexGridCell.NUM_NEIGHBORS; i++) {
            int nI = HexGridCell.getNeighborI(cI, cJ, i);
            int nJ = HexGridCell.getNeighborJ(cI, cJ, i);
            if (inBoard(nI, nJ) && cell(nI, nJ) == foeColor) {
                setCell(nI + nJ * WIDTH, color);
                numCaptured++;
            }
        }
        return numCaptured;
    }

    public boolean inBoard(int nI, int nJ) {
        return nI >= 0 && nJ >= 0 && nI < WIDTH && nJ < HEIGHT;
    }

    public ArrayList<HexxagonMove> getPossibleMoves(byte player) {
        ArrayList<HexxagonMove> moves = new ArrayList<HexxagonMove>();
        for (byte j = 0; j < HexxagonBoard.HEIGHT; j++) {
            for (byte i = 0; i < HexxagonBoard.WIDTH; i++) {
                short cellIdx = (short)(i + j * HexxagonBoard.WIDTH);
                if (mCells[cellIdx] == HexxagonBoard.CELL_EMPTY) {
                    // the cell can be moved to
                    // check the near neighbors (a single one of the is enough)
                    for (int k = 0; k < HexGridCell.NUM_NEIGHBORS; k++) {
                        int ni = HexGridCell.getNeighborI(i, j, k);
                        int nj = HexGridCell.getNeighborJ(i, j, k);
                        if (inBoard(ni, nj)) {
                            short n = (short)(ni + nj * HexxagonBoard.WIDTH);
                            if (mCells[n] == player) {
                                moves.add(new HexxagonMove(n, cellIdx));
                                break;
                            }
                        }
                    }
                    for (int k = 0; k < HexGridCell.NUM_NEIGHBORS2; k++) {
                        int ni = HexGridCell.getNeighbor2I(i, j, k);
                        int nj = HexGridCell.getNeighbor2J(i, j, k);
                        if (inBoard(ni, nj)) {
                            short n = (short)(ni + nj * HexxagonBoard.WIDTH);
                            if (mCells[n] == player) {
                                moves.add(new HexxagonMove(n, cellIdx));
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
        int myCells = getNumCells(color);
        int foeCells = getNumCells(getFoeColor(color));
        return myCells > 0 ? myCells - foeCells : -HexxagonBoard.BIG_VALUE;
    }
}
