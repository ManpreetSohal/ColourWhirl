package colourwhirl.manpreet.com.colourwhirl;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {
    private Context context;
    private MediaPlayer backgroundMusic, scoredSound;

    public Sound(Context context){
        this.context = context;
        createBackgroundMusic();
        createScoredSound();
    }

    private void createBackgroundMusic(){
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
    }

    private void createScoredSound(){
        scoredSound = MediaPlayer.create(context, R.raw.success);
    }

    public void playBackgroundMusic(){
        backgroundMusic.start();
        backgroundMusic.setLooping(true);
    }

    public void stopBackgroundMusic(){
        backgroundMusic.pause();
    }

    public void resumeBackgroundMusic(){
        backgroundMusic.start();
    }

    public void playScoredSound(){
        scoredSound.start();
    }
}
