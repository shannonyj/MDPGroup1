package lab.mdp.grp01.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.LinkedList;
import java.util.Queue;

import static lab.mdp.grp01.main.Utils.*;

public class MapSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "MapSurface class: ";
    private final int SCREEN_PADDING = 30;
    private final int SCREEN_WIDTH = 720;
    private final int SCREEN_HEIGHT = 570;

    private SurfaceHolder sh;
    private Canvas canvas;
    private Paint paint;

    private int mapStartX = 0, mapStartY = 0;
    private float cellWidth;

    private static Bitmap robotUp;
    private static Bitmap robotDown;
    private static Bitmap robotLeft;
    private static Bitmap robotRight;
    public static Queue<String> robotActionQueue;
    private RobotThread robotThread;



    public MapSurface(Context context){
        super(context);
        Log.d(TAG, "1");
        sh = getHolder();
        sh.addCallback(this);
        paint = new Paint();
        this.setKeepScreenOn(true);
    }

    public MapSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "2");
        sh = getHolder();
        sh.addCallback(this);
        paint = new Paint();
        this.setKeepScreenOn(true);
    }

    public MapSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "3");
        sh = getHolder();
        sh.addCallback(this);
        paint = new Paint();
        this.setKeepScreenOn(true);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        robotActionQueue = new LinkedList<>();
        float tmpWidth = (SCREEN_WIDTH - 2 * SCREEN_PADDING) / MAP_COLS;
        float tmpHeight = (SCREEN_HEIGHT - 2 * SCREEN_PADDING) / MAP_ROWS;
        cellWidth = tmpWidth < tmpHeight ? tmpWidth : tmpHeight;
        mapStartX = SCREEN_PADDING;
        mapStartY = SCREEN_PADDING;
        Bitmap tmpRobotUp = BitmapFactory.decodeResource(getResources(), R.drawable.bot_up);
        Bitmap tmpRobotDown = BitmapFactory.decodeResource(getResources(), R.drawable.bot_down);
        Bitmap tmpRobotLeft = BitmapFactory.decodeResource(getResources(), R.drawable.bot_left);
        Bitmap tmpRobotRight = BitmapFactory.decodeResource(getResources(), R.drawable.bot_right);
        robotUp = Bitmap.createScaledBitmap(tmpRobotUp, (int)cellWidth*2, (int)cellWidth*2, true);
        robotDown = Bitmap.createScaledBitmap(tmpRobotDown, (int)cellWidth*2, (int)cellWidth*2, true);
        robotLeft = Bitmap.createScaledBitmap(tmpRobotLeft, (int)cellWidth*2, (int)cellWidth*2, true);
        robotRight = Bitmap.createScaledBitmap(tmpRobotRight, (int)cellWidth*2, (int)cellWidth*2, true);
        initiateMap();

        robotThread = new RobotThread(sh, this, mapStartX + SCREEN_PADDING, mapStartY + SCREEN_PADDING, cellWidth);
        robotThread.setRunning(true);
        robotThread.start();
    }

    public void robotChange(String newMap){
        if(newMap != null && newMap.length() > 4 && newMap.substring(0, 4).toUpperCase().equals("GRID"))
            robotActionQueue.add(newMap);
    }

    public void updateMap(float posX, float posY, String headPos, String mapInfo){
        drawMapGrid(mapInfo);
        switch(headPos){
            case HEAD_POS_UP:
                canvas.drawBitmap(robotUp, posX, posY, null);
                break;
            case HEAD_POS_DOWN:
                canvas.drawBitmap(robotDown, posX, posY, null);
                break;
            case HEAD_POS_LEFT:
                canvas.drawBitmap(robotLeft, posX, posY, null);
                break;
            case HEAD_POS_RIGHT:
                canvas.drawBitmap(robotRight, posX, posY, null);
                break;
            default:
                break;
        }
    }

    private void initiateMap(){
        canvas = sh.lockCanvas();
        robotChange(defaultMap);
        if(canvas != null)
            sh.unlockCanvasAndPost(canvas);
    }

    public void drawMapGrid(String mapInfo){
        float currentX = mapStartX + SCREEN_PADDING, currentY = mapStartY + SCREEN_PADDING;
        float mapWidth = currentX + cellWidth * MAP_COLS;
        float mapHeight = currentY + cellWidth * MAP_ROWS;

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(mapStartX, mapStartY, mapWidth+SCREEN_PADDING, mapHeight+SCREEN_PADDING), 10, 10, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        for(int i = 0; i < MAP_COLS+1; i++){
            canvas.drawLine(currentX, currentY, currentX, mapHeight, paint);
            currentX += cellWidth;
        }
        currentX = mapStartX + SCREEN_PADDING;
        for(int j = 0; j < MAP_ROWS+1; j++){
            canvas.drawLine(currentX, currentY, mapWidth, currentY, paint);
            currentY += cellWidth;
        }

        String[] info = mapInfo.split(" ");
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF();
        for(int i = 0; i < info.length; i++){
            if(Integer.valueOf(info[i]) == 1){
                int currentRow = i / MAP_COLS;
                int currentCol = i - MAP_COLS * currentRow;

                float left = mapStartX + SCREEN_PADDING + currentCol * cellWidth;
                float top = mapStartY + SCREEN_PADDING + currentRow * cellWidth;
                float right = left + cellWidth;
                float bottom = top + cellWidth;

                Log.d(TAG, "paint block: " + currentRow + ", " + currentCol);
                rect.set(left, top, right, bottom);
                canvas.drawRect(rect, paint);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}
