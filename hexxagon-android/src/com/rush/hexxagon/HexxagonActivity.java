package com.rush.hexxagon;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.*;

public class HexxagonActivity extends Activity implements Platform
{
    DrawThread mDrawThread;
    MainView mView;

    private boolean mIsShowGrid = false;
    private boolean mIsShowRows = false;
    private boolean mEditorMode = false;

    private int[] mCornersX = new int[HexGridCell.NUM_CORNERS];
    private int[] mCornersY = new int[HexGridCell.NUM_CORNERS];

    private int mCellRadius = 16;
    HexGridCell mMetrics = new HexGridCell(mCellRadius);
    private float mBallRatio = 0.7f;

    Path mHexPath = new Path();
    Paint mPaint = new Paint();

    private Game mGame = new Game(this);

    private int mBoardX = 0;
    private int mBoardY = 0;
    private byte mCurEditorCellType = GameBoard.CELL_NONE;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_level:
                synchronized (mView) {
                    mGame.setCurrentLevel(Math.min(mGame.getNumLevels() - 1, mGame.getCurrentLevel() + 1));
                    mGame.startGame();
                    mView.focusBoardView(null);
                }
                break;
            case R.id.prev_level:
                synchronized (mView) {
                    mGame.setCurrentLevel(Math.max(0, mGame.getCurrentLevel() - 1));
                    mGame.startGame();
                    mView.focusBoardView(null);
                }
                break;
            case R.id.edit_level:
                synchronized (mView) {
                    mEditorMode = true;
                    mIsShowGrid = true;
                    findViewById(R.id.editor_hud).setVisibility(View.VISIBLE);
                    mView.focusBoardView(new GameBoard.Extents());
                }
                break;
            case R.id.play_level:
                synchronized (mView) {
                    mEditorMode = false;
                    mIsShowGrid = false;
                    findViewById(R.id.editor_hud).setVisibility(View.GONE);
                    mGame.storeLevel(mGame.getCurrentLevel(), mGame.getBoard());
                    mGame.startGame();
                    mView.focusBoardView(null);
                }
                break;
            case R.id.load_level:
                synchronized (mView) {
                    loadLevelsFromSD();
                }
                break;
            case R.id.save_level:
                saveLevelsToSD();
                break;
        }
        return true;
    }

    private void saveLevelsToSD() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        }

        if (!mExternalStorageAvailable) popup("No external storage available!", null);
        else if (!mExternalStorageWriteable) popup("External storage is not writable!", null);
        else {
            final File file = new File(Environment.getExternalStorageDirectory(), "levels.txt");
            if (file.exists()) {
                new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Save levels data")
                    .setMessage("Do you want to overwrite the existing data file?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                FileOutputStream os = new FileOutputStream(file, false);
                                mGame.saveLevelsData(os);
                                os.close();
                            } catch (IOException e) {
                                popup("Error writing " + file, null);
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            } else {
                try {
                    FileOutputStream os = new FileOutputStream(file, false);
                    mGame.saveLevelsData(os);
                    os.close();
                } catch (IOException e) {
                    popup("Error writing " + file, null);
                }
            }
        }
    }

    public void onRadioButtonClicked(View v) {
        RadioButton rb = (RadioButton) v;
        switch (rb.getId()) {
            case R.id.radio_none: mCurEditorCellType = GameBoard.CELL_NONE; break;
            case R.id.radio_empty: mCurEditorCellType = GameBoard.CELL_EMPTY; break;
            case R.id.radio_black: mCurEditorCellType = GameBoard.CELL_BLACK; break;
            case R.id.radio_white: mCurEditorCellType = GameBoard.CELL_WHITE; break;
        }
    }

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

        setContentView(R.layout.main);

        FrameLayout layout = (FrameLayout)findViewById(R.id.frame_layout);

        mView = new MainView(this, null);
        layout.addView(mView, 0);

        mDrawThread = new DrawThread(mView.getHolder());

        mGame.loadLevelsData(null);
        mGame.startGame();
        mView.focusBoardView(null);
    }

    public void loadLevelsFromSD() {
        File file = new File(Environment.getExternalStorageDirectory(), "levels.txt");
        FileInputStream is;
        try {
            is = new FileInputStream(file);
            mGame.loadLevelsData(is);
            mGame.startGame();
            mView.focusBoardView(null);
        } catch (IOException e) {
            popup("Error loading " + file, null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit Hexxagon")
                    .setMessage("Do you really want to exit the game?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HexxagonActivity.this.finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }

    }
//
//    @Override
//    public void onPause() {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//        SharedPreferences.Editor ed = pref.edit();
//        ed.putInt("CurrentLevel", mGame.getCurrentLevel());
//        ed.commit();
//    }
//
//    @Override
//    public void onResume() {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//        int curLevel = pref.getInt("CurrentLevel", 0);
//        //mGame.loadLevelsData();
//        //mGame.setCurrentLevel(curLevel);
//    }

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

    public void repaint(){
        mView.focusBoardView(null);
        // update the counters display
        String strNumBlack = Integer.toString(mGame.getBoard().getNumCells(GameBoard.CELL_BLACK));
        String strNumWhite = Integer.toString(mGame.getBoard().getNumCells(GameBoard.CELL_WHITE));

        ((TextView)findViewById(R.id.white_count_text)).setText(strNumWhite);
        ((TextView)findViewById(R.id.black_count_text)).setText(strNumBlack);
    }

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

        public synchronized void focusBoardView(GameBoard.Extents ext) {
            if (ext == null) {
                ext = mGame.getBoard().getBoardExtents();
            }
            int cellsW = ext.right - ext.left + 1;
            int cellsH = ext.bottom - ext.top + 1;

            int viewW = mView.getWidth();
            int viewH = mView.getHeight();

            mCellRadius = (int) Math.min((viewW/(cellsW + 0.25))*2.0f/3.0f, (viewH/(cellsH + 0.5))/Math.sqrt(3.0f));

            mBoardX = (int)((viewW - (cellsW + 0.25)*mCellRadius*3.0f/2.0f)/2.0f - ext.left*mCellRadius*3.0f/2.0f);
            mBoardY = (int)((viewH - (cellsH + 0.5)*mCellRadius*Math.sqrt(3.0f))/2.0f - ext.top*mCellRadius*Math.sqrt(3.0f));

            mMetrics = new HexGridCell(mCellRadius);

            mMetrics.setCellIndex(0, 0);
            mMetrics.computeCorners(mCornersX, mCornersY);

            mHexPath = new Path();
            mHexPath.moveTo(mCornersX[0], mCornersY[0]);
            for (int k = 1; k < HexGridCell.NUM_CORNERS; k++) {
                mHexPath.lineTo(mCornersX[k], mCornersY[k]);
            }
            mHexPath.close();
            mHexPath.offset(-mMetrics.getCenterX() + mBoardX, -mMetrics.getCenterY() + mBoardY);

            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public synchronized void onDraw(Canvas canvas) {
            //  update the board
            if (mCellRadius == 0) mView.focusBoardView(null);

            canvas.drawColor(Color.DKGRAY);

            int cellColor = 0xFFAAAAAA;
            int selectedColor = 0xFF8888FF;
            int dist1Color = 0xFF3333AA;
            int dist2Color = 0xFF6666CC;

            int selI = mGame.getSelectedCell()%GameBoard.WIDTH;
            int selJ = mGame.getSelectedCell()/GameBoard.WIDTH;
            for (int j = 0; j < GameBoard.HEIGHT; j++) {
                for (int i = 0; i < GameBoard.WIDTH; i++) {
                    byte c = mGame.getBoard().cell(i, j);

                    mMetrics.setCellIndex(i, j);
                    mMetrics.computeCorners(mCornersX, mCornersY);
                    mHexPath.offset(mMetrics.getCenterX(), mMetrics.getCenterY());

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
                        mPaint.setColor(bgColor);
                        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawPath(mHexPath, mPaint);
                    }

                    if (c != 0) {
                        if (c == GameBoard.CELL_BLACK || c == GameBoard.CELL_WHITE) {
                            float R = mCellRadius*mBallRatio;
                            mPaint.setStyle(Paint.Style.FILL);
                            mPaint.setColor(mGame.getBoard().cell(i, j) == GameBoard.CELL_BLACK ? Color.BLACK
                                    : Color.WHITE);
                            canvas.drawCircle(mMetrics.getCenterX() + mBoardX, mMetrics.getCenterY() + mBoardY, R, mPaint);
                            mPaint.setColor(Color.BLACK);
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawCircle(mMetrics.getCenterX() + mBoardX, mMetrics.getCenterY() + mBoardY, R, mPaint);
                        }
                    }

                    if (c != 0 || mIsShowGrid) {
                        mPaint.setColor(Color.BLACK);
                        mPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(mHexPath, mPaint);
                        mPaint.setColor(Color.BLACK);
                        canvas.drawPath(mHexPath, mPaint);
                    }
                    mHexPath.offset(-mMetrics.getCenterX(), -mMetrics.getCenterY());
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
                    if (mEditorMode) {
                        mGame.getBoard().setCell(clickCell, mCurEditorCellType);
                    } else {
                        mGame.getCurrentPlayer().onClickCell(clickCell, mGame);
                    }
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




