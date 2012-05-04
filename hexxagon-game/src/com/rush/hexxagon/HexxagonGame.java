package com.rush.hexxagon;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class HexxagonGame {
    private static final int NUM_PLAYERS = 2;

    private int mSelectedCell = -1;
    private int mCurrentLevel = 0;

    private HexxagonBoard mBoard = new HexxagonBoard();
    private ArrayList<HexxagonBoard> mLevels = new ArrayList<HexxagonBoard>();

    private Player[] mPlayers = new Player[NUM_PLAYERS];
    private int mCurPlayer = -1;

    private Platform mPlatform;
    
    public HexxagonGame(Platform platform) {
        mPlatform = platform;
    }
    
    public void loadLevelsData(InputStream stream) {
        ArrayList<HexxagonBoard> levels = new ArrayList<HexxagonBoard>();
        try {
            if (stream == null) {
                URL url = this.getClass().getResource("/resource/levels.txt");
                stream = url.openStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String level = "";
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.contains("---")) {
                    if (level.length() >= HexxagonBoard.NUM_CELLS) {
                        HexxagonBoard board = new HexxagonBoard();
                        board.init(level);
                        levels.add(board);
                    }
                    level = "";
                } else {
                    level += String.format("%1$-" + HexxagonBoard.WIDTH + "s", line); // pad with spaces
                }
            }
        } catch (IOException iox) {
            mPlatform.popup("Failed to load levels file!", null);
        }
        mLevels = levels;
    }
    
    public void startGame() {
        mPlayers[0] = new HumanPlayer(HexxagonBoard.CELL_WHITE);
        mPlayers[0] = new AIPlayer(HexxagonBoard.CELL_WHITE, AIPlayer.SolverType.MinMax, 3);
        mPlayers[1] = new AIPlayer(HexxagonBoard.CELL_BLACK, AIPlayer.SolverType.AlphaBetaSort, 3);
        mBoard = new HexxagonBoard(mLevels.get(mCurrentLevel));
        mCurPlayer = -1;
        nextMove();
    }

    public void storeLevel(int levelIdx, HexxagonBoard board) {
        mLevels.set(levelIdx, new HexxagonBoard(board));
    }

    void nextMove() {
        mPlatform.repaint();
        mCurPlayer = (mCurPlayer + 1) % mPlayers.length;
        byte winner = mBoard.getWinner(getCurrentPlayer().getColor());
        if (winner == HexxagonBoard.CELL_EMPTY) {
            getCurrentPlayer().startMove(this);
        } else {
            for (int i = 0; i < HexxagonBoard.NUM_CELLS; i++) {
                if (mBoard.cell(i) == HexxagonBoard.CELL_EMPTY) {
                    mBoard.setCell(i, winner);
                }
            }
            mPlatform.repaint();

            String message = (winner == HexxagonBoard.CELL_BLACK) ? "Black won!" : "White won!";
            mPlatform.popup(message, new Platform.Callback(){
                @Override
                public void call() {
                    mCurrentLevel++;
                    startGame();
                    mPlatform.repaint();
                }});
        }
    }

    public Player getCurrentPlayer() {
        return mPlayers[mCurPlayer];
    }

    public int getSelectedCell() {
        return mSelectedCell;
    }
    
    public void selectCell(int cellIdx) {
        mSelectedCell = cellIdx;
        mPlatform.repaint();
    }

    public HexxagonBoard getBoard() {
        return mBoard;
    }

    public int getCurrentLevel() {
        return mCurrentLevel;
    }

    public void setCurrentLevel(int level) {
        mCurrentLevel = level;
    }

    public int getNumLevels() {
        return mLevels.size();
    }

    public boolean saveLevelsData(OutputStream stream) {
        final String sep = "----------------\n";
        try {
            stream.write(sep.getBytes());
            for (HexxagonBoard b: mLevels) {
                for (int j = 0; j < HexxagonBoard.HEIGHT; j++) {
                    for (int i = 0; i < HexxagonBoard.WIDTH; i++) {
                        byte c = ' ';
                        switch (b.cell(i, j)) {
                            case HexxagonBoard.CELL_EMPTY: c = '.'; break;
                            case HexxagonBoard.CELL_WHITE: c = 'O'; break;
                            case HexxagonBoard.CELL_BLACK: c = '*'; break;
                        }
                        stream.write(c);
                    }
                    stream.write('\n');
                }
                stream.write(sep.getBytes());
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
