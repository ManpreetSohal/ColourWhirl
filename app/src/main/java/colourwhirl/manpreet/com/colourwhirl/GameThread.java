package colourwhirl.manpreet.com.colourwhirl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Looper;
import android.view.SurfaceHolder;

import android.os.Handler;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private GameState gameState;
    private Canvas canvas;
    private boolean canvasLocked = true;
    private float angleVariation = 0;
    private float previousAngleVariation = -1;
    private Context context;
    private static final long SECOND = 1000;
    private static final long TARGET_FPS = 30;
    private static final long FRAME_PERIOD = SECOND / TARGET_FPS;
    private long time = System.currentTimeMillis();
    private GameView gameView;
    public boolean run = true;

    public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler, GameView gameView, GameState gameState)
    {
        this.context = context;
        this.surfaceHolder = surfaceHolder;
        this.paint = new Paint();
        this.gameState = gameState;
        this.gameView = gameView;
    }

    public void setAngleVariations(float variation){
        this.angleVariation= variation;
    }

    @Override
    public void run() {
        while(run)
        {
            long startTime = System.currentTimeMillis();
            canvas = surfaceHolder.lockCanvas();
            canvasLocked = true;
            if(angleVariation == previousAngleVariation){
                gameState.draw(canvas, paint);
            }
            else {
                gameState.draw(canvas, paint);
            }

            if(!(gameState.update(angleVariation))){
                gameView.endDialogView();
            }

            previousAngleVariation = angleVariation;

            if(canvasLocked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            doFpsCheck(startTime);
        }
    }

    public boolean doFpsCheck(long startTime) {

        if (System.currentTimeMillis() - time >= SECOND) {
            time = System.currentTimeMillis();
        }

        long sleepTime = FRAME_PERIOD
                - (System.currentTimeMillis() - startTime);

        if (sleepTime >= 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
            return true;
        } else {
            return false;
        }
    }

    public void killThread(){
        run = false;
        try
        {
            join();
        }
        catch (InterruptedException e) {
        }
    }
}