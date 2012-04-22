package com.rush.hexxagon;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Applet for an "hexxagon" game implementation
 * 
 * @author Ruslan Shestopalyuk
 */
public class HexxagonApplet extends Applet implements MouseListener, Platform {
    private static final long serialVersionUID = 1L;

    private static final int CELL_RADIUS = 22;
    
    private boolean mIsShowGrid = false;
    private boolean mIsShowRows = false;
    
    private int[] mCornersX = new int[HexGridCell.NUM_CORNERS];
    private int[] mCornersY = new int[HexGridCell.NUM_CORNERS];

    private static HexGridCell mCellMetrics = new HexGridCell(CELL_RADIUS);

    private Game mGame = new Game(this);

    @Override
    public void init() {
        addMouseListener(this);
        mGame.loadLevelsData(null);
        mGame.startGame();
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

        int selI = mGame.getSelectedCell() % GameBoard.WIDTH;
        int selJ = mGame.getSelectedCell() / GameBoard.WIDTH;
        for (int j = 0; j < GameBoard.HEIGHT; j++) {
            for (int i = 0; i < GameBoard.WIDTH; i++) {
                byte c = mGame.getBoard().cell(i, j);

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
                    g.fillPolygon(mCornersX, mCornersY, HexGridCell.NUM_CORNERS);
                }

                if (c != 0) {
                    if (c == GameBoard.CELL_BLACK || c == GameBoard.CELL_WHITE) {
                        int R = CELL_RADIUS - 6;
                        g.setColor(mGame.getBoard().cell(i, j) == GameBoard.CELL_BLACK ? Color.BLACK
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
                    g.drawPolygon(mCornersX, mCornersY, HexGridCell.NUM_CORNERS);
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
            mGame.getCurrentPlayer().onClickCell(clickCell, mGame);
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

    @Override
    public void popup(String message, final Callback onClosedCallback) {
        JOptionPane.showMessageDialog(new JFrame(), message);
        if (onClosedCallback != null) onClosedCallback.call();
    }
}
