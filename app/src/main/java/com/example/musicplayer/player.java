package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class player extends AppCompatActivity {
    ImageButton onOff;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Button button;
    ArrayList<File> mySongs;
    String current;
    TextView textView4;
    Thread updateSeek;
//    Timer timer;
    public int i;
    public int currentPosition;
    ImageButton next;
    ImageButton prev;
    Boolean play;
    Boolean loop;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateSeek.interrupt();
//        timer.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        onOff = findViewById(R.id.imageButton);
        seekBar = findViewById(R.id.seekBar);
        textView4 = findViewById(R.id.textView4);
        next = findViewById(R.id.imageButton3);
        prev = findViewById(R.id.imageButton2);
        currentPosition = 0;
        play=true;
        loop=false;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        current = intent.getStringExtra("current");
        mySongs = (ArrayList) bundle.getParcelableArrayList("songList");
        i = bundle.getInt("i");
        textView4.setText(current);
        textView4.setSelected(true);
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(mySongs.get(i)));


        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        updateSeek = new Thread(){
            @Override
            public void run() {
                try {while(true){
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    sleep(10);}
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//            }
//        },0,10);

        onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying() == true) {
                    mediaPlayer.pause();
                    onOff.setImageResource(android.R.drawable.ic_media_play);
                    play=false;
                } else if (mediaPlayer.isPlaying() == false) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (mediaPlayer.isLooping() == false) {
                                onOff.setImageResource(android.R.drawable.ic_media_play);
                                play=false;
                            }
                        }
                    });
                    onOff.setImageResource(android.R.drawable.ic_media_pause);
                    play=true;
                }
            }
        });

        updateSeek.start();

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isLooping() == true) {
                    mediaPlayer.setLooping(false);
                    loop=false;
                    Toast.makeText(player.this, "Song removed from loop", Toast.LENGTH_SHORT).show();
                    button.setText("Loop It");
                } else {
                    mediaPlayer.setLooping(true);
                    Toast.makeText(player.this, "Song set on loop", Toast.LENGTH_SHORT).show();
                    button.setText("Remove Loop");
                    loop=true;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer.isLooping() == false) {
                    onOff.setImageResource(android.R.drawable.ic_media_play);
                    play=false;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeek.interrupt();
                mediaPlayer.stop();
                mediaPlayer.release();
                i++;
                i%=mySongs.size();
                mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.fromFile(mySongs.get(i)));
                currentPosition = 0;
                if(play==true){mediaPlayer.start();
                    if(loop==true){mediaPlayer.setLooping(true);}
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (mediaPlayer.isLooping() == false) {
                            onOff.setImageResource(android.R.drawable.ic_media_play);
                            play=false;
                        }
                    }
                });}

                seekBar.setMax(mediaPlayer.getDuration());
                current = mySongs.get(i).getName().replace(".mp3","");
                textView4.setText(current);
                updateSeek.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSeek.interrupt();
                mediaPlayer.stop();
                mediaPlayer.release();
                if(i==0){
                    i=mySongs.size()-1;
                }
                else{
                    i--;
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.fromFile(mySongs.get(i)));
                currentPosition = 0;
                if(play==true){mediaPlayer.start();
                    if(loop==true){mediaPlayer.setLooping(true);}
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (mediaPlayer.isLooping() == false) {
                            onOff.setImageResource(android.R.drawable.ic_media_play);
                            play=false;
                        }
                    }
                });}
                seekBar.setMax(mediaPlayer.getDuration());
                current = mySongs.get(i).getName().replace(".mp3","");
                textView4.setText(current);
                updateSeek.start();
            }
        });
    }
}
