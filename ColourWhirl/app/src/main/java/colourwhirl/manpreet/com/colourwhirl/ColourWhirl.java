package colourwhirl.manpreet.com.colourwhirl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;

public class ColourWhirl extends AppCompatActivity {//implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private Button leaderboardBtn, signInBtn, signOutBtn, playBtn, pauseBtn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private GameView gameView;
    private GameState gameState;
    private HomeView homeView;
    private boolean onHomeView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameState = new GameState(this);
        gameView = new GameView(this, gameState);
        homeView= new HomeView(this, gameState);

        final RelativeLayout mainView = new RelativeLayout(this);
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        playBtn = new Button(this);
        playBtn.setText("Play");
        playBtn.setBackgroundResource(R.drawable.shadow);
        playBtn.setLayoutParams(btnParams);
        playBtn.setGravity(Gravity.CENTER);

        pauseBtn = new Button(this);
        pauseBtn.setBackgroundResource(R.drawable.pause_btn_background);

        leaderboardBtn = new Button(this);
        leaderboardBtn.setText("Play");
        playBtn.setLayoutParams(btnParams);
        leaderboardBtn.setGravity(Gravity.CENTER);

        signInBtn = new Button(this);
        signInBtn.setText("Play");
        signInBtn.setLayoutParams(btnParams);
        signInBtn.setGravity(Gravity.CENTER);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.removeAllViews();
                mainView.addView(pauseBtn);
                mainView.addView(gameView);
                onHomeView = false;
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        mainView.addView(playBtn);
        mainView.addView(homeView);
        mainView.addView(leaderboardBtn);

        setContentView(mainView);
    }
    public void onPause(){
        super.onPause();
        if(!onHomeView) {
            pause();
        }
        else{
            gameView.getBackgroundMusic().pause();
        }
    }

    public void onResume(){
        super.onResume();
        if(onHomeView){
            gameView.getBackgroundMusic().start();
        }
    }

    public void pause(){
        gameView.pauseGame();
    }
}