package com.example.talk2me.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talk2me.R;
import com.example.talk2me.helper.Preferencias;
import com.example.talk2me.model.Upload;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

    //***********variaveis que fazem parte do teste:
    private ValueEventListener mDBlistener;
    private DatabaseReference audioDatabaseRef;
    private StorageReference audioStorageRef;
    private Handler handler = new Handler();
    //fim do teste

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

    public AudioAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //criar o view file
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_audio, parent, false);     //É AQUI QUE USO O XML INDIVIDUAL ! IMAGEM_ITEM !!!
        return new ImageViewHolder(v);

    }

    private void prepareMediaPlayer(String chosenMusic, ImageViewHolder holder){
        /*
        String chosenMusic = "http://infinityandroid.com/music/good_times.mp3";
        //o Utilizador só pode ver as imagens correspondentes ao seu próprio nó
        Preferencias preferencias = new Preferencias(mContext);
        String loggedUserIdentifier = preferencias.getIndentifier();
        Log.i(String.valueOf(this), "loggedUserIdentifier-->"+loggedUserIdentifier);        //YUP, devolve o identificador do logged user
         */
        try {
            holder.mediaPlayer.setDataSource(chosenMusic);       //URL tens que por a extensão senão o tlm crasha por inteiro! la perdi mais 1 hora neste bug....
            holder.mediaPlayer.prepare();
            holder.textTotalDuration.setText(milliSecondsToTimer(holder.mediaPlayer.getDuration()));
        }
        catch (Exception e){
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {       //este BindViewHolder é chamado cada vez que o notifyDataSetChanged é chamado também!
        //POSSIVEL SOLUÇÃO: PASSAR mUploads por intent e vir busca-los aqui
        Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        prepareMediaPlayer(uploadCurrent.getImageUrl(), holder );
        Log.i(String.valueOf(this), "mUploads-> "+mUploads );
        Log.i(String.valueOf(this), "uploadCurrent.getImageUrl() AUDIO --> "+uploadCurrent.getImageUrl());

        /*
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.usuario)      //enquanto nao faz load
                .fit()
                .centerInside()           //define a maneira como se ve as imagens
                .into(holder.imageView);
         */

    }

    @Override
    public int getItemCount() {
        return mUploads.size();     //mostrar quantos items quantos tiver na lista
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagePlayPause;
        public TextView textCurrentTime, textTotalDuration;
        public SeekBar playerSeekBar;
        public MediaPlayer mediaPlayer;
        public TextView textViewName;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.receveidFromAudio);       //titulo, de quem vem o audio
            //imageView = itemView.findViewById(R.id.image_view_upload);
            imagePlayPause = itemView.findViewById(R.id.imagePlayPause);
            textCurrentTime = itemView.findViewById(R.id.textCurrentTime);
            textTotalDuration = itemView.findViewById(R.id.textTotalDuration);
            playerSeekBar = itemView.findViewById(R.id.playerSeekBar);
            mediaPlayer = new MediaPlayer();

            //**********************************************************TESTE DIA 5 AGOSTO...NEM ACREDIO QUE E AGOSTO E EU A FAZER ISTO....CADA LINHA DE CODIGO QUE ESCREVO DETESTO MAIS ENGENHARIA....

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

            /*
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playerSeekBar.setProgress(0);
                    imagePlayPause.setImageResource(R.drawable.ic_play);
                    textCurrentTime.setText(R.string.zero);
                    textTotalDuration.setText(R.string.zero);
                    mediaPlayer.reset();
                    //prepareMediaPlayer( "http://infinityandroid.com/music/good_times.mp3" );
                }
            });
             */


            //*********************************************************FIM DO TESTE DE DIA 5 AGOSTO*****************************************************************************************************



        }
        private void updateSeekbar(){
            if(mediaPlayer.isPlaying()){
                playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100 ));
                handler.postDelayed(updater, 1000);
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

    }



}


































