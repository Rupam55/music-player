package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView mylistviewforsong;
    String[] items;

    Button btn_next,btn_previous,btn_pause,btn_screen;
    SeekBar songseekbar;

    static MediaPlayer mymediaplayer;
    int position;
    Thread updateseekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);
        btn_next=(Button)findViewById(R.id.btnnext);
        btn_pause=(Button)findViewById(R.id.btnback);
        btn_previous=(Button)findViewById(R.id.btnplay);
        btn_screen=(Button)findViewById(R.id.btnscreen);

        songseekbar=(SeekBar)findViewById(R.id.seekbar);



        if(mymediaplayer!=null){
            mymediaplayer.stop();
            mymediaplayer.release();
        }
        mylistviewforsong = (ListView)  findViewById(R.id.listview);

        runtimepermission();

        RelativeLayout constraintLayout = findViewById(R.id.home);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        btn_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent (getApplicationContext(),PlayerActivity.class));
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


    }

    public void runtimepermission(){

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {


                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {



                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    public ArrayList<File> findsong(File file){

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File singleFile: files){

            if(singleFile.isDirectory() && !singleFile.isHidden()){

                arrayList.addAll(findsong(singleFile));

            }
            else{
                if(singleFile.getName().endsWith(".mp3")||
                singleFile.getName().endsWith("wav")){
                    arrayList.add(singleFile);
                }
            }

        }

        return arrayList;
    }

    void display() {

        final ArrayList<File> mysongs = findsong(Environment.getExternalStorageDirectory());

        items = new String[mysongs.size()];

        for (int i = 0; i < mysongs.size(); i++) {

            items[i] = mysongs.get(i).getName().toString().replace(".mp3", "").replace("wav", "");

        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        mylistviewforsong.setAdapter(myAdapter);

        mylistviewforsong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                btn_screen.setBackgroundResource(R.drawable.arrow_back);

                String songname = mylistviewforsong.getItemAtPosition(i).toString();

                startActivity(new Intent (getApplicationContext(),PlayerActivity.class)
                .putExtra("songs",mysongs).putExtra("songname",songname)
                .putExtra("pos",i));
            }
        });


    }


}
