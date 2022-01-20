package com.example.talk2me.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AudioActivity extends AppCompatActivity {

    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private StorageReference audioStorageRef;
    private ValueEventListener mDBlistener;
    private DatabaseReference audioDatabaseRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        //sacar o id logged User que vem do mainActivity:
        Bundle extras = getIntent().getExtras();
        String idLoggedUser = extras.getString("idLoggedUser");
        Log.i(String.valueOf(this), "idLoggedUser recebido...:"+idLoggedUser);

        imagePlayPause = findViewById(R.id.imagePlayPauseActivity);
        textCurrentTime = findViewById(R.id.textCurrentTimeActivity);
        textTotalDuration = findViewById(R.id.textTotalDurationActivity);
        playerSeekBar = findViewById(R.id.playerSeekBarActivity);
        mediaPlayer = new MediaPlayer();

        playerSeekBar.setMax(100);

        audioStorageRef = FirebaseStorage.getInstance().getReference("Audio");

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.ic_play);
                }
                else{
                    mediaPlayer.start();
                    imagePlayPause.setImageResource(R.drawable.ic_pause);
                    updateSeekbar();
                }
            }
        });

        prepareMediaPlayer();

        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar = (SeekBar) v;
                int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        //barra de buffering:
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekBar.setSecondaryProgress(percent);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekBar.setProgress(0);
                imagePlayPause.setImageResource(R.drawable.ic_play);
                textCurrentTime.setText(R.string.zero);
                textTotalDuration.setText(R.string.zero);
                mediaPlayer.reset();
                prepareMediaPlayer();
            }
        });

    }

    private void prepareMediaPlayer(){
        String chosenMusic = "http://infinityandroid.com/music/good_times.mp3";
        //o Utilizador só pode ver as imagens correspondentes ao seu próprio nó
        Preferencias preferencias = new Preferencias(this);
        String loggedUserIdentifier = preferencias.getIndentifier();

        try {
            mediaPlayer.setDataSource(chosenMusic);       //URL tens que por a extensão senão o tlm crasha por inteiro! la perdi mais 1 hora neste bug....
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }
        catch (Exception e){
            Toast.makeText(this, "VVVERRRRRRRRRRO: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100 ));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds){
        String timerString = "";
        String secondsString;

        int hours = (int)(milliSeconds / (1000 * 60 * 60));
        int minutes = (int)(milliSeconds % (1000 * 60 * 60 )) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 *60 )/ 1000);

        if(hours > 0 ){
            timerString = hours + ":";
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }
        else{
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
    }
}





























































