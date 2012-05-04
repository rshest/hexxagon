package com.rush.hexxagon;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

/**
 * Applet for an "hexxagon" game implementation
 * 
 * @author Ruslan Shestopalyuk
 */
public class HexxagonApplet extends Applet implements MouseListener, Platform {
    static final float BALL_RATIO = 0.8f;
    static final int CELL_RADIUS = 22;

    static final Color mCellColor = new Color(0xAAAAAA);
    static final Color mSelectedColor = new Color(0x8888FF);
    static final Color mSelectedColor1 = new Color(0x3333AA);
    static final Color mSelectedColor2 = new Color(0x6666CC);

    int[] mCornersX = new int[HexGridCell.NUM_CORNERS];
    int[] mCornersY = new int[HexGridCell.NUM_CORNERS];

    boolean mIsShowGrid = false;
    boolean mIsShowRows = false;
    boolean mIsShowIdx = false;

    HexxagonGame mGame = new HexxagonGame(this);

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


    private void drawBoard(Graphics g, HexxagonBoard board, Rectangle bounds, int selI, int selJ) {
        int cellRadius = CELL_RADIUS;

        HexGridCell cellMetrics = new HexGridCell(cellRadius);
        for (int j = 0; j < HexxagonBoard.HEIGHT; j++) {
            for (int i = 0; i < HexxagonBoard.WIDTH; i++) {
                byte c = board.cell(i, j);

                cellMetrics.setCellIndex(i, j);
                cellMetrics.computeCorners(mCornersX, mCornersY);
                for (int cX: mCornersX) cX += bounds.getX();
                for (int cY: mCornersY) cY += bounds.getY();

                int dist = HexGridCell.walkDistance(i, j, selI, selJ);
                Color bgColor = Color.DARK_GRAY;
                if (mIsShowGrid && mIsShowRows) {
                    bgColor = (j % 2 == 1) ? Color.YELLOW : Color.GREEN;
                }
                bgColor = (c != 0) ? mCellColor : bgColor;
                if (c == HexxagonBoard.CELL_EMPTY) {
                    bgColor = (dist == 1) ? mSelectedColor1 : bgColor;
                    bgColor = (dist == 2) ? mSelectedColor2 : bgColor;
                }
                bgColor = (dist == 0) ? mSelectedColor : bgColor;

                if (bgColor != null) {
                    g.setColor(bgColor);
                    g.fillPolygon(mCornersX, mCornersY, HexGridCell.NUM_CORNERS);
                }

                if (c != 0) {
                    if (c == HexxagonBoard.CELL_BLACK || c == HexxagonBoard.CELL_WHITE) {
                        int R = (int) (cellRadius* BALL_RATIO);
                        int cx = (int) (cellMetrics.getCenterX() - R + bounds.getX());
                        int cy = (int) (cellMetrics.getCenterY() - R + bounds.getY());
                        g.setColor(board.cell(i, j) == HexxagonBoard.CELL_BLACK ? Color.BLACK
                                : Color.WHITE);
                        g.fillArc(cx, cy, R * 2, R * 2, 0, 360);
                        g.setColor(Color.BLACK);
                        g.drawArc(cx, cy, R * 2, R * 2, 0, 360);
                    }
                }

                if (c != 0 || mIsShowGrid) {
                    g.setColor(Color.BLACK);
                    g.drawPolygon(mCornersX, mCornersY, HexGridCell.NUM_CORNERS);
                }

                if (mIsShowIdx) {
                    g.setColor(Color.BLUE);
                    Font font = new Font("Serif", Font.PLAIN, 9);
                    String str = "" + (i + j* HexxagonBoard.WIDTH);
                    FontMetrics fm = getFontMetrics(font);
                    Rectangle2D rect = fm.getStringBounds(str, g);
                    int w = (int)rect.getWidth();
                    int h = (int)rect.getHeight();
                    g.setFont(font);
                    g.drawString(str, cellMetrics.getCenterX() - w/2, cellMetrics.getCenterY() + h/2);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        setBackground(Color.DARK_GRAY);

        Rectangle bounds = getBounds();
        int selIdx = mGame.getSelectedCell();
        int selI = selIdx >= 0 ? selIdx % HexxagonBoard.WIDTH : -1;
        int selJ = selIdx >= 0 ? selIdx / HexxagonBoard.WIDTH : -1;
        drawBoard(g, mGame.getBoard(), bounds, selI, selJ);
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        HexGridCell cellMetrics = new HexGridCell(CELL_RADIUS);
        cellMetrics.setCellByPoint(arg0.getX(), arg0.getY());
        int clickI = cellMetrics.getIndexI();
        int clickJ = cellMetrics.getIndexJ();
        int clickCell = clickI + clickJ * HexxagonBoard.WIDTH;

        if (clickI >= 0 && clickI < HexxagonBoard.WIDTH && clickJ >= 0
                && clickJ < HexxagonBoard.HEIGHT) {
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
