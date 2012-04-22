package com.rush.hexxagon;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.os.SystemClock;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;

public class HexxagonActivity extends Activity implements Platform
{
    DrawThread mDrawThread;
    MainView mView;

    private boolean mIsShowGrid = false;
    private boolean mIsShowRows = false;

    private int[] mCornersX = new int[HexGridCell.NUM_CORNERS];
    private int[] mCornersY = new int[HexGridCell.NUM_CORNERS];

    private int mCellRadius = 16;
    private float mBallRatio = 0.7f;

    private Game mGame = new Game(this);

    private int mBoardX = 0;
    private int mBoardY = 0;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //  game window setup
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mView = new MainView(this, null);
        setContentView(mView);
        mDrawThread = new DrawThread(mView.getHolder());

        mGame.init();
    }

    @Override
    public void popup(String message, final Callback onClosedCallback) {
        AlertDialog box = new AlertDialog.Builder(this).create();
        box.setMessage(message);
        box.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (onClosedCallback != null) {
                    onClosedCallback.call();
                }
            }
        });
        box.show();
    }

    public void repaint(){}

    void startDrawingThread() {
        mDrawThread.setRunning(true);
        mDrawThread.start();
    }

    void stopDrawingThread() {
        boolean retry = true;
        mDrawThread.setRunning(false);
        while (retry) {
            try {
                mDrawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // keep trying to stop the draw thread
            }
        }
    }

    public void update(float dt) {

    }
    class MainView extends SurfaceView implements SurfaceHolder.Callback {
        public MainView(Context context, AttributeSet attrs) {
            super(context, attrs);
            getHolder().addCallback(this);
        }

        @Override
        public void onDraw(Canvas canvas) {

            GameBoard.Extents ext = mGame.getBoard().getBoardExtents();
            int cellsW = ext.right - ext.left + 1;
            int cellsH = ext.bottom - ext.top + 1;

            mCellRadius = (int) Math.min((getWidth()/(cellsW + 0.25))*2.0f/3.0f, (getHeight()/(cellsH + 0.5))/Math.sqrt(3.0f));

            mBoardX = (int)((getWidth() - (cellsW + 0.25)*mCellRadius*3.0f/2.0f)/2.0f - ext.left*mCellRadius*3.0f/2.0f);
            mBoardY = (int)((getHeight() - (cellsH + 0.5)*mCellRadius*Math.sqrt(3.0f))/2.0f - ext.top*mCellRadius*Math.sqrt(3.0f));

            HexGridCell metrics = new HexGridCell(mCellRadius);

            canvas.drawColor(Color.DKGRAY);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            metrics.setCellIndex(0, 0);
            metrics.computeCorners(mCornersX, mCornersY);

            Path hexPath = new Path();
            hexPath.moveTo(mCornersX[0], mCornersY[0]);
            for (int k = 1; k < HexGridCell.NUM_CORNERS; k++) {
                hexPath.lineTo(mCornersX[k], mCornersY[k]);
            }
            hexPath.close();
            hexPath.offset(-metrics.getCenterX() + mBoardX, -metrics.getCenterY() + mBoardY);

            int cellColor = 0xFFAAAAAA;
            int selectedColor = 0xFF8888FF;
            int dist1Color = 0xFF3333AA;
            int dist2Color = 0xFF6666CC;

            int selI = mGame.getSelectedCell()%GameBoard.WIDTH;
            int selJ = mGame.getSelectedCell()/GameBoard.WIDTH;
            for (int j = 0; j < GameBoard.HEIGHT; j++) {
                for (int i = 0; i < GameBoard.WIDTH; i++) {
                    byte c = mGame.getBoard().cell(i, j);

                    metrics.setCellIndex(i, j);
                    metrics.computeCorners(mCornersX, mCornersY);
                    hexPath.offset(metrics.getCenterX(), metrics.getCenterY());

                    int dist = HexGridCell.walkDistance(i, j, selI, selJ);
                    int bgColor = 0;
                    if (mIsShowGrid && mIsShowRows) {
                        bgColor = (j%2 == 1) ? Color.YELLOW : Color.GREEN;
                    }
                    bgColor = (c != 0) ? cellColor : bgColor;
                    if (c == GameBoard.CELL_EMPTY) {
                        bgColor = (dist == 1) ? dist1Color : bgColor;
                        bgColor = (dist == 2) ? dist2Color : bgColor;
                    }
                    bgColor = (i == selI && j == selJ) ? selectedColor : bgColor;

                    if (bgColor != 0) {
                        paint.setColor(bgColor);
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawPath(hexPath, paint);
                    }

                    if (c != 0) {
                        if (c == GameBoard.CELL_BLACK || c == GameBoard.CELL_WHITE) {
                            float R = mCellRadius*mBallRatio;
                            paint.setStyle(Paint.Style.FILL);
                            paint.setColor(mGame.getBoard().cell(i, j) == GameBoard.CELL_BLACK ? Color.BLACK
                                    : Color.WHITE);
                            canvas.drawCircle(metrics.getCenterX() + mBoardX, metrics.getCenterY() + mBoardY, R, paint);
                            paint.setColor(Color.BLACK);
                            paint.setStyle(Paint.Style.STROKE);
                            canvas.drawCircle(metrics.getCenterX() + mBoardX, metrics.getCenterY() + mBoardY, R, paint);
                        }
                    }

                    if (c != 0 || mIsShowGrid) {
                        paint.setColor(Color.BLACK);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(hexPath, paint);
                        paint.setColor(Color.BLACK);
                        canvas.drawPath(hexPath, paint);
                    }
                    hexPath.offset(-metrics.getCenterX(), -metrics.getCenterY());
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                HexGridCell metrics = new HexGridCell(mCellRadius);
                metrics.setCellByPoint((int)event.getX() - mBoardX, (int)event.getY() - mBoardY);
                int clickI = metrics.getIndexI();
                int clickJ = metrics.getIndexJ();
                int clickCell = clickI + clickJ * GameBoard.WIDTH;

                if (clickI >= 0 && clickI < GameBoard.WIDTH && clickJ >= 0
                        && clickJ < GameBoard.HEIGHT) {
                    mGame.getCurrentPlayer().onClickCell(clickCell, mGame);
                }
                return false;
            }
            return true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startDrawingThread();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopDrawingThread();
        }
    }

    class DrawThread extends Thread {
        private final SurfaceHolder mSurfaceHolder;
        private boolean mIsRunning = false;
        private long mLastTime;

        public DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean isRunning) {
            mIsRunning = isRunning;
        }

        @Override
        public void run() {
            Canvas c;
            mLastTime = SystemClock.uptimeMillis();
            while (mIsRunning) {
                final long time = SystemClock.uptimeMillis();
                final float timeDelta = ((float)(time - mLastTime))*0.001f;
                mLastTime = time;
                update(timeDelta);
                c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        mView.onDraw(c);
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}




