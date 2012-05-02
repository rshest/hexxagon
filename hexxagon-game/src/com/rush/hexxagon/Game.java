package com.rush.hexxagon;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Game {
    private static final int NUM_PLAYERS = 2;

    private int mSelectedCell = -1;
    private int mCurrentLevel = 0;

    private GameBoard mBoard = new GameBoard();
    private ArrayList<GameBoard> mLevels = new ArrayList<GameBoard>();

    private Player[] mPlayers = new Player[NUM_PLAYERS];
    private int mCurPlayer = -1;

    private Platform mPlatform;
    
    public Game(Platform platform) {
        mPlatform = platform;
    }
    
    public void loadLevelsData(InputStream stream) {
        ArrayList<GameBoard> levels = new ArrayList<GameBoard>();
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
                    if (level.length() >= GameBoard.NUM_CELLS) {
                        GameBoard board = new GameBoard();
                        board.init(level);
                        levels.add(board);
                    }
                    level = "";
                } else {
                    level += String.format("%1$-" + GameBoard.WIDTH + "s", line); // pad with spaces
                }
            }
        } catch (IOException iox) {
            mPlatform.popup("Failed to load levels file!", null);
        }
        mLevels = levels;
    }
    
    public void startGame() {
        mPlayers[0] = new HumanPlayer(GameBoard.CELL_WHITE);
        mPlayers[0] = new AIPlayer(GameBoard.CELL_WHITE, AIPlayer.SolverType.MinMax, 3);
        mPlayers[1] = new AIPlayer(GameBoard.CELL_BLACK, AIPlayer.SolverType.AlphaBetaSort, 3);
        mBoard = new GameBoard(mLevels.get(mCurrentLevel));
        mCurPlayer = -1;
        nextMove();
    }

    public void storeLevel(int levelIdx, GameBoard board) {
        mLevels.set(levelIdx, new GameBoard(board));
    }

    void nextMove() {
        mPlatform.repaint();
        mCurPlayer = (mCurPlayer + 1) % mPlayers.length;
        byte winner = mBoard.getWinner(getCurrentPlayer().getColor());
        if (winner == GameBoard.CELL_EMPTY) {
            getCurrentPlayer().startMove(this);
        } else {
            for (int i = 0; i < GameBoard.NUM_CELLS; i++) {
                if (mBoard.cell(i) == GameBoard.CELL_EMPTY) {
                    mBoard.setCell(i, winner);
                }
            }
            mPlatform.repaint();

            String message = (winner == GameBoard.CELL_BLACK) ? "Black won!" : "White won!";
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

    public GameBoard getBoard() {
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
            for (GameBoard b: mLevels) {
                for (int j = 0; j < GameBoard.HEIGHT; j++) {
                    for (int i = 0; i < GameBoard.WIDTH; i++) {
                        byte c = ' ';
                        switch (b.cell(i, j)) {
                            case GameBoard.CELL_EMPTY: c = '.'; break;
                            case GameBoard.CELL_WHITE: c = 'O'; break;
                            case GameBoard.CELL_BLACK: c = '*'; break;
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
