package com.rush.hexxagon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Game {
    private static final int NUM_PLAYERS = 2;

    private int mSelectedCell = -1;
    private int mCurrentLevel = 2;

    private GameBoard mBoard = new GameBoard();
    private ArrayList<GameBoard> mLevels = new ArrayList<GameBoard>();

    private Player[] mPlayers = new Player[NUM_PLAYERS];
    private int mCurPlayer = -1;

    private Platform mPlatform;
    
    public Game(Platform platform) {
        mPlatform = platform;
    }
    
    public void init() {
        try {
            URL url = this.getClass().getResource("/resource/levels.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String level = "";
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.contains("---")) {
                    if (level.length() >= GameBoard.NUM_CELLS) {
                        GameBoard board = new GameBoard();
                        board.init(level);
                        mLevels.add(board);
                    }
                    level = "";
                } else {
                    level += line;
                }
            }
        } catch (IOException iox) {
            mPlatform.popup("Failed to load levels file!", null);
        }
    }
    
    public void startGame() {
        mPlayers[0] = new HumanPlayer(GameBoard.CELL_WHITE);
        mPlayers[1] = new AIPlayer(GameBoard.CELL_BLACK);
        mBoard = new GameBoard(mLevels.get(mCurrentLevel));
        mCurPlayer = -1;
        nextMove();
    }

    public void storeLevel(int levelIdx, GameBoard board) {
        mLevels.set(levelIdx, new GameBoard(board));
    }

    void nextMove() {
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

            String message = winner == GameBoard.CELL_BLACK ? "Black won!" : "White won!";
            mPlatform.popup(message, new Platform.Callback(){
                @Override
                public void call() {
                    //To change body of implemented methods use File | Settings | File Templates.
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
}
