package colourwhirl.manpreet.com.colourwhirl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class ColourWhirl extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Button leaderboardBtn, signOutBtn, signInBtn, playBtn, pauseBtn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private GameView gameView;
    private GameState gameState;
    private HomeView homeView;
    private Sound soundSystem;
    private boolean onHomeView = true;
    private static final int RC_LEADERBOARD_UI = 9004;
    static boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("default_web_client_id")
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        soundSystem = new Sound(this);
        soundSystem.playBackgroundMusic();
        gameState = new GameState(this, soundSystem);
        gameView = new GameView(this, gameState, soundSystem);
        homeView= new HomeView(this, gameState);

        final RelativeLayout mainView = new RelativeLayout(this);

        RelativeLayout.LayoutParams playBtnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        playBtnParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        playBtn = new Button(this);
        playBtn.setId(R.id.playBtn);
        playBtn.setText("Play");
        playBtn.setBackgroundResource(R.drawable.shadow);
        playBtn.setLayoutParams(playBtnParams);
        playBtn.setGravity(Gravity.CENTER);


        pauseBtn = new Button(this);
        pauseBtn.setText("| |");

        RelativeLayout.LayoutParams leaderboardAndSignInBtnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        leaderboardAndSignInBtnParams.addRule(RelativeLayout.BELOW, playBtn.getId());
        leaderboardAndSignInBtnParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        leaderboardBtn = new Button(this);
        leaderboardBtn.setId(R.id.leaderboardBtn);
        leaderboardBtn.setText("LEADERBOARD");
        leaderboardBtn.setLayoutParams(leaderboardAndSignInBtnParams);
        leaderboardBtn.setGravity(Gravity.CENTER);

        signInBtn = new Button(this);
        signInBtn.setId(R.id.signInBtn);
        signInBtn.setText("CONNECT TO PLAY GAMES");
        signInBtn.setLayoutParams(leaderboardAndSignInBtnParams);
        signInBtn.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams signOutBtnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        signOutBtnParams.addRule(RelativeLayout.BELOW, leaderboardBtn.getId());
        signOutBtnParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        signOutBtn = new Button(this);
        signOutBtn.setText("SIGN OUT");
        signOutBtn.setLayoutParams(signOutBtnParams);
        signOutBtn.setGravity(Gravity.CENTER);

        if(signedIn){
            updateLeaderboard();
            signInBtn.setVisibility(View.GONE);
            leaderboardBtn.setVisibility(View.VISIBLE);
            signOutBtn.setVisibility(View.VISIBLE);
        }
        else{
            leaderboardBtn.setVisibility(View.GONE);
            signOutBtn.setVisibility(View.GONE);
            signInBtn.setVisibility(View.VISIBLE);
        }

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

        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signedIn) {
                    showLeaderboard();
                }
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!signedIn) {
                    signIn();
                }
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        mainView.addView(playBtn);
        mainView.addView(homeView);
        mainView.addView(leaderboardBtn);
        mainView.addView(signInBtn);
        mainView.addView(signOutBtn);

        setContentView(mainView);
    }

    public void updateLeaderboard(){
        HighScoreDb db = new HighScoreDb(this);
        db.open();
        int highScore = db.getHighScore();
        db.close();

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard_id), highScore);
    }

    public void signIn(){
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, REQ_CODE);
            googleApiClient.connect();
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                signedIn = false;
                signOutBtn.setVisibility(View.GONE);
                signInBtn.setVisibility(View.VISIBLE);
                leaderboardBtn.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()){
                signedIn = true;
                signOutBtn.setVisibility(View.VISIBLE);
                leaderboardBtn.setVisibility(View.VISIBLE);
                signInBtn.setVisibility(View.GONE);
                updateLeaderboard();
            }
        }
    }

    private void showLeaderboard() {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    public void onPause(){
        super.onPause();
        if(!onHomeView) {
            pause();
        }
        else{
            soundSystem.stopBackgroundMusic();
        }
    }

    public void onResume(){
        super.onResume();
        if(onHomeView){
            soundSystem.playBackgroundMusic();
        }
    }

    public void pause(){
        gameView.pauseDialogView();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}