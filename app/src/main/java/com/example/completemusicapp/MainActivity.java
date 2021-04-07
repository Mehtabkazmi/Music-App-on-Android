package com.example.completemusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView prev,play,next,imageView;
    SeekBar seekBarTime,seekBarVol;
    TextView songTitle;
    static MediaPlayer mediaPlayer;
    private Runnable runnable;
    private AudioManager audioManager;
    int currentIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // find view by id
        play=findViewById(R.id.play);
        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        songTitle=findViewById(R.id.songTitle);
        imageView=findViewById(R.id.imageView);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVol=findViewById(R.id.seekBarVol);

        // create arraylist to store songs

        ArrayList<Integer> songs=new ArrayList<>();
        songs.add(0,R.raw.zindadili);
        songs.add(1,R.raw.newsong);


        mediaPlayer=MediaPlayer.create(getApplicationContext(),songs.get(currentIndex));
        mediaPlayer.start();
        songsName();
        int maxV=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarVol.setMax(maxV);
        seekBarVol.setProgress(curV);

        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarTime.setMax(mediaPlayer.getDuration());
                if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
                else{
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.baseline_pause_24);
                }
                songsName();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    play.setImageResource(R.drawable.baseline_pause_24);
                }
                if (currentIndex <songs.size()-1){
                    currentIndex++;
                }else{
                    currentIndex =0;
                }

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer=MediaPlayer.create(getApplicationContext(),songs.get(currentIndex));
                mediaPlayer.start();
                songsName();
            }
        });
//
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null){
                    play.setImageResource(R.drawable.baseline_pause_24);
                }
                if (currentIndex>0){
                    currentIndex--;
                }else{
                    currentIndex=songs.size()-1;
                }

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer=MediaPlayer.create(getApplicationContext(),songs.get(currentIndex));
                mediaPlayer.start();
                songsName();
            }
        });
    }
    private void songsName(){
        if (currentIndex==0){
            songTitle.setText("Zinda Dilli New Song");
            imageView.setImageResource(R.drawable.zindadili);
        }
        if (currentIndex==1){
            songTitle.setText("new song");
            imageView.setImageResource(R.drawable.music);
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBarTime.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
            }
        });
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBarTime.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer!=null){
                    try{
                        if (mediaPlayer.isPlaying()){
                            Message message=new Message();
                            message.what=mediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
     @SuppressLint("HandlerLeak") Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            seekBarTime.setProgress(msg.what);
        }
    };
}