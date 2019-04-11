package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {


    ListView mylistviewforsong;
    String[] items;

    Button btn_next,btn_previous,btn_pause,btn_screen;
    TextView songtextlable1;
    SeekBar songseekbar;

    static MediaPlayer mymediaplayer;
    int position;
    ArrayList<File> mysongs;
    Thread updateseekbar;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player);


        btn_next=(Button)findViewById(R.id.btnnext);
        btn_pause=(Button)findViewById(R.id.btnplay);
        btn_previous=(Button)findViewById(R.id.btnback);
        btn_screen=(Button)findViewById(R.id.btnscreen);

        btn_screen.setBackgroundResource(R.drawable.arrow_back);
        btn_pause.setBackgroundResource(R.drawable.pause);

        songtextlable1=(TextView)findViewById(R.id.text);

        songseekbar=(SeekBar)findViewById(R.id.seekbar);

        updateseekbar = new Thread() {

            @Override
            public void run() {

                int totalduration = mymediaplayer.getDuration();
                int currentposition = 0;

                while (currentposition < totalduration) {
                    try {
                        sleep(500);
                        currentposition = mymediaplayer.getCurrentPosition();
                        songseekbar.setProgress(currentposition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mymediaplayer!=null){
            mymediaplayer.stop();
            mymediaplayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mysongs=(ArrayList) bundle.getParcelableArrayList("songs");

        final String[] sname = {mysongs.get(position).getName().toString()};

        final String songname = i.getStringExtra("songname");

        songtextlable1.setText(songname);
        songtextlable1.setSelected(true);

        position = bundle.getInt("pos",0);

        Uri u = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(getApplicationContext(),u);

        mymediaplayer.start();
        songseekbar.setMax(mymediaplayer.getDuration());

        updateseekbar.start();

        songseekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songseekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        songseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mymediaplayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                songseekbar.setMax(mymediaplayer.getDuration());

                if(mymediaplayer.isPlaying()){
                    btn_pause.setBackgroundResource(R.drawable.play);
                    mymediaplayer.pause();
                }
                else{
                    btn_pause.setBackgroundResource(R.drawable.pause);
                    mymediaplayer.start();
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mymediaplayer.stop();
                mymediaplayer.release();
                position=((position+1)%mysongs.size());

                Uri u = Uri.parse(mysongs.get(position).toString());

                mymediaplayer = MediaPlayer.create(getApplicationContext(),u);

                sname[0] = mysongs.get(position).getName().toString();
                songtextlable1.setText(sname[0]);

                mymediaplayer.start();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mymediaplayer.stop();
                mymediaplayer.release();
                position=((position-1)<0)?(mysongs.size()-1):(position-1);

                Uri u = Uri.parse(mysongs.get(position).toString());

                mymediaplayer = MediaPlayer.create(getApplicationContext(),u);

                sname[0] = mysongs.get(position).getName().toString();
                songtextlable1.setText(sname[0]);

                mymediaplayer.start();

            }
        });

        btn_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent (getApplicationContext(),HomeActivity.class));
                btn_screen.setBackgroundResource(R.drawable.arrow);

            }
        });


        RelativeLayout constraintLayout = findViewById(R.id.player);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();



    }
}
