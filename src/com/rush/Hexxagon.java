package com.rush;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Applet for an "hexxagon" game implementation
 * 
 * @author Ruslan Shestopalyuk
 */
public class Hexxagon extends Applet implements MouseListener {
    private static final long serialVersionUID = 1L;

    private static final int NUM_HEX_CORNERS = 6;
    private static final int CELL_RADIUS = 22;
    private static final int NUM_PLAYERS = 2;

    private boolean mIsShowGrid = false;
    private boolean mIsShowRows = false;
    private int mSelectedCell = -1;
    private int mCurrentLevel = 2;

    private GameBoard mBoard = new GameBoard();
    private ArrayList<String> mLevels = new ArrayList<String>();

    private int[] mCornersX = new int[NUM_HEX_CORNERS];
    private int[] mCornersY = new int[NUM_HEX_CORNERS];
    private Player[] mPlayers = new Player[NUM_PLAYERS];
    private int mCurPlayer = -1;

    private static HexGridCell mCellMetrics = new HexGridCell(CELL_RADIUS);

    private void startGame() {
        mPlayers[0] = new HumanPlayer(GameBoard.CELL_WHITE);
        mPlayers[1] = new AIPlayer(GameBoard.CELL_BLACK);
        mBoard.init(mLevels.get(mCurrentLevel));
        mCurPlayer = -1;
        nextMove();
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
            repaint();

            JOptionPane.showMessageDialog(new JFrame(),
                    winner == GameBoard.CELL_BLACK ? "Black won!"
                            : "White won!");
            mCurrentLevel++;
            startGame();
        }
    }

    public Player getCurrentPlayer() {
        return mPlayers[mCurPlayer];
    }

    public int getSelectedCell() {
        return mSelectedCell;
    }

    public GameBoard getBoard() {
        return mBoard;
    }

    public void selectCell(int cellIdx) {
        mSelectedCell = cellIdx;
        repaint();
    }

    @Override
    public void init() {
        addMouseListener(this);

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
                    if (level.length() >= GameBoard.NUM_CELLS)
                        mLevels.add(level);
                    level = "";
                } else {
                    level += line;
                }
            }
        } catch (IOException iox) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "Failed to load levels file!");
        }

        startGame();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        setBackground(Color.DARK_GRAY);
        Color cellColor = new Color(0xAAAAAA);
        Color selectedColor = new Color(0x8888FF);
        Color dist1Color = new Color(0x3333AA);
        Color dist2Color = new Color(0x6666CC);

        int selI = mSelectedCell % GameBoard.WIDTH;
        int selJ = mSelectedCell / GameBoard.WIDTH;
        for (int j = 0; j < GameBoard.HEIGHT; j++) {
            for (int i = 0; i < GameBoard.WIDTH; i++) {
                byte c = mBoard.cell(i, j);

                mCellMetrics.setCellIndex(i, j);
                mCellMetrics.computeCorners(mCornersX, mCornersY);

                int dist = HexGridCell.walkDistance(i, j, selI, selJ);
                Color bgColor = Color.DARK_GRAY;
                if (mIsShowGrid && mIsShowRows) {
                    bgColor = (j % 2 == 1) ? Color.YELLOW : Color.GREEN;
                }
                bgColor = (c != 0) ? cellColor : bgColor;
                if (c == GameBoard.CELL_EMPTY) {
                    bgColor = (dist == 1) ? dist1Color : bgColor;
                    bgColor = (dist == 2) ? dist2Color : bgColor;
                }
                bgColor = (i == selI && j == selJ) ? selectedColor : bgColor;

                if (bgColor != null) {
                    g.setColor(bgColor);
                    g.fillPolygon(mCornersX, mCornersY, NUM_HEX_CORNERS);
                }

                if (c != 0) {
                    if (c == GameBoard.CELL_BLACK || c == GameBoard.CELL_WHITE) {
                        int R = CELL_RADIUS - 6;
                        g.setColor(mBoard.cell(i, j) == GameBoard.CELL_BLACK ? Color.BLACK
                                : Color.WHITE);
                        g.fillArc(mCellMetrics.getCenterX() - R,
                                mCellMetrics.getCenterY() - R, R * 2, R * 2, 0,
                                360);
                        g.setColor(Color.BLACK);
                        g.drawArc(mCellMetrics.getCenterX() - R,
                                mCellMetrics.getCenterY() - R, R * 2, R * 2, 0,
                                360);
                    }
                }

                if (c != 0 || mIsShowGrid) {
                    g.setColor(Color.BLACK);
                    g.drawPolygon(mCornersX, mCornersY, NUM_HEX_CORNERS);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        mCellMetrics.setCellByPoint(arg0.getX(), arg0.getY());
        int clickI = mCellMetrics.getIndexI();
        int clickJ = mCellMetrics.getIndexJ();
        int clickCell = clickI + clickJ * GameBoard.WIDTH;

        if (clickI >= 0 && clickI < GameBoard.WIDTH && clickJ >= 0
                && clickJ < GameBoard.HEIGHT) {
            getCurrentPlayer().onClickCell(clickCell, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }
}
