package com.example.talk2me.fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talk2me.R;
import com.example.talk2me.adapter.AudioAdapter;
import com.example.talk2me.config.FirebaseConfig;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Chat;
import com.example.talk2me.model.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends Fragment {

    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private StorageReference audioStorageRef;
    private ValueEventListener mDBlistener;
    private DatabaseReference audioDatabaseRef;
    private FirebaseStorage mStorage;
    private List<Upload> mUploads;
    //cenas do fragment
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircleAudio;
    private AudioAdapter mAdapterAudio;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_audio);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mProgressCircleAudio  = view.findViewById(R.id.progress_circle_audio);

        mUploads = new ArrayList<>();

        mAdapterAudio = new AudioAdapter(getContext(), mUploads);
        mRecyclerView.setAdapter(mAdapterAudio);

        mStorage = FirebaseStorage.getInstance();

        //o Utilizador só pode ver as imagens correspondentes ao seu próprio nó
        Preferencias preferencias = new Preferencias(getActivity());
        String loggedUserIdentifier = preferencias.getIndentifier();

        //audioDatabaseRef = FirebaseConfig.getFirebase().child("Audio").child(loggedUserIdentifier);
        audioDatabaseRef = FirebaseConfig.getFirebase().child("Audio").child(loggedUserIdentifier);       //para aceder aos audios do naruto@gmail.com


        mDBlistener = audioDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());       //gravar a key para este item, preciso disto para depois o apagar
                    Log.i(String.valueOf(this), "espero obter um link para cada audio-->"+upload.getImageUrl().toString() );        //WORKS
                    mUploads.add(upload);
                }
                mAdapterAudio.notifyDataSetChanged();
                mProgressCircleAudio.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircleAudio.setVisibility(View.INVISIBLE);
            }
        });

        return view;
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
    public void onDestroyView() {
        super.onDestroyView();
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        audioDatabaseRef.removeEventListener(mDBlistener);
    }

    @Override
    public void onStop() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onDetach();
    }

}





























































