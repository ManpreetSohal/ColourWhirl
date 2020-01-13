package colourwhirl.manpreet.com.colourwhirl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread _thread;
    private GameState gameState;
    private Sound soundSystem;
    private Context context;
    private SurfaceHolder holder;
    private boolean isPaused = false;
    private int gamesPlayed = 1;
    private InterstitialAd interstitialAd;
    private boolean adIsLoaded = true;
    private float previousAngle, currentAngle, angleVariations = 0;
    private Bundle extras = new Bundle();

    public GameView(Context context, GameState gameState, Sound soundSystem) {
        super(context);
        this.context = context;
        this.gameState = gameState;
        this.gameState = gameState;
        this.soundSystem = soundSystem;

        extras.putBoolean("is_designed_for_families", true);
        // Initialize, set, and Load Ad
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId("admod_ad_id");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        this.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event){
                final float xc = getWidth()/ 2;
                final float yc = getHeight()/ 2;
                final float x = event.getX();
                final float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        currentAngle  = (float)Math.toDegrees(Math.atan2(x - xc, yc - y));
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        previousAngle = currentAngle;
                        currentAngle = (float)Math.toDegrees(Math.atan2(x - xc, yc - y));
                        angleVariations = currentAngle - previousAngle;
                        _thread.setAngleVariations(angleVariations);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP : {
                        angleVariations = 0;
                        _thread.setAngleVariations(0);
                        break;
                    }
                }
                return true;
            }
        });
        this._thread = new GameThread(holder, context, new Handler(), GameView.this, gameState);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(this._thread.getState() != Thread.State.TERMINATED) {
            _thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int x, int y, int z){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public void pauseDialogView(){
        if(!isPaused) {
            isPaused = true;
            soundSystem.stopBackgroundMusic();
            Activity activity= (Activity)this.context;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _thread.killThread();
                    pauseGameDialog();
                }});
        }
    }

    public void endDialogView(){
        isPaused = true;
        soundSystem.stopBackgroundMusic();
        Activity activity= (Activity)this.context;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _thread.killThread();

                if(!adIsLoaded){
                    interstitialAd.loadAd(new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .tagForChildDirectedTreatment(true)
                            .build());
                    adIsLoaded = true;
                }

                if(gamesPlayed%2==0 && (interstitialAd.isLoaded())){
                    interstitialAd.show();
                    adIsLoaded = false;

                    interstitialAd.setAdListener(new AdListener(){
                        public void onAdClosed(){
                            endGameDialog();
                        }
                    });
                }
                else {
                    endGameDialog();
                }
            }});
    }

    public void resumeGameView()
    {
        isPaused = false;
        soundSystem.resumeBackgroundMusic();
        _thread = new GameThread(holder, getContext(), new Handler(), GameView.this, gameState);
        _thread.run = true;
        _thread.start();
    }

    private void endGameDialog() {
        int score = gameState.getScore();
        gameState.setScore(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        LinearLayout dialogBoxLayout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogBoxLayout.setOrientation(LinearLayout.VERTICAL);
        dialogBoxLayout.setLayoutParams(params);

        TextView gameOverBanner = new TextView(this.context);
        gameOverBanner.setText("Gamer Over");
        gameOverBanner.setBackgroundColor(Color.argb(255, 255, 69, 5));
        gameOverBanner.setGravity(Gravity.CENTER);
        gameOverBanner.setTextColor(Color.WHITE);
        gameOverBanner.setTextSize(30);

        TextView scoreDisplayer= new TextView(this.context);
        scoreDisplayer.setText("Score: " + score);
        scoreDisplayer.setPadding(10, 10, 10, 10);
        scoreDisplayer.setBackgroundColor(Color.WHITE);
        scoreDisplayer.setGravity(Gravity.CENTER);
        scoreDisplayer.setTextColor(Color.argb(255, 255, 69, 5));
        scoreDisplayer.setTextSize(20);

        HighScoreDb db = new HighScoreDb(context);
        db.open();
        db.updateHighScore(score);

        TextView highScoreDisplayer= new TextView(this.context);
        highScoreDisplayer.setText("High Score: " + db.getHighScore());

        db.close();

        highScoreDisplayer.setPadding(10, 10, 10, 10);
        highScoreDisplayer.setBackgroundColor(Color.WHITE);
        highScoreDisplayer.setGravity(Gravity.CENTER);
        highScoreDisplayer.setTextColor(Color.argb(255, 255, 69, 5));
        highScoreDisplayer.setTextSize(20);

        dialogBoxLayout.addView(scoreDisplayer);
        dialogBoxLayout.addView(highScoreDisplayer);

        builder.setCustomTitle(gameOverBanner);
        builder.setView(dialogBoxLayout);
        builder.setCancelable(false);

        builder.setNegativeButton("Home", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                ((Activity)context).recreate();
            }
        });

        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ++gamesPlayed;
                resumeGameView();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        //get buttons
        Button homeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button retryButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        homeButton.setBackgroundColor(Color.DKGRAY);
        retryButton.setBackgroundColor(Color.DKGRAY);

        homeButton.setTextSize(15);
        retryButton.setTextSize(15);

        homeButton.setPadding(10,10,10,10);
        retryButton.setPadding(10,10,10,10);

        homeButton.setTextColor(Color.argb(255, 255, 69, 5));
        retryButton.setTextColor(Color.argb(255, 255, 69, 5));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(20,0,0,0);

        homeButton.setLayoutParams(buttonParams);
        retryButton.setLayoutParams(buttonParams);
    }

    private void pauseGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        LinearLayout dialogBoxLayout = new LinearLayout(context);
        dialogBoxLayout.setBackgroundColor(Color.BLUE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogBoxLayout.setOrientation(LinearLayout.VERTICAL);
        dialogBoxLayout.setLayoutParams(params);


        TextView titleBanner = new TextView(this.context);
        titleBanner.setText("Game Paused");
        titleBanner.setBackgroundColor(Color.argb(255, 255, 69, 5));
        titleBanner.setGravity(Gravity.CENTER);
        titleBanner.setTextColor(Color.WHITE);
        titleBanner.setTextSize(30);


        builder.setCustomTitle(titleBanner);
        builder.setView(dialogBoxLayout);
        builder.setCancelable(false);

        builder.setPositiveButton("Resume", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                resumeGameView();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        //get buttons
        Button resumeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        resumeButton.setBackgroundColor(Color.DKGRAY);
        resumeButton.setTextSize(15);
        resumeButton.setTextColor(Color.argb(255, 255, 69, 5));
        resumeButton.setLayoutParams(params);
    }
}