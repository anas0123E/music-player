package com.example.lecteur_de_musique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ActivityLecteur extends AppCompatActivity {
    //Attributs********************
    AppCompatButton btnPlay,btnNext,btnPrev,btnFF,btnFR;
    TextView txtsname,txtsstart,txtsstop;
    SeekBar seekBar;
    ImageView imageView ;
    String sname;
    public static final String EXTRA_NAME = "chan_nom";
    static MediaPlayer monLecteur;
    int position;
    ArrayList <File> maChan;
    Thread uptSeekBar;
    //********************************************

    //Initialisations******************************
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);

        getSupportActionBar().setTitle("lecture encours de");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPrev = findViewById(R.id.previousButton);
        btnNext = findViewById(R.id.nextButton);
        btnFF = findViewById(R.id.buttonFastf);
        btnFR = findViewById(R.id.buttonFastr);
        btnPlay = findViewById(R.id.playButton);
        txtsname = findViewById(R.id.txtsn);
        txtsstart = findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        seekBar = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.imageVue);

        if(monLecteur != null){
            monLecteur.stop();
            monLecteur.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        maChan = (ArrayList) bundle.getParcelableArrayList("chansons");
        String chanNom = i.getStringExtra("nomChanson");
        position = bundle.getInt("pos",0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(maChan.get(position).toString());
        sname = maChan.get(position).getName();
        txtsname.setText(sname);

        monLecteur = MediaPlayer.create(getApplicationContext(),uri);
        monLecteur.start();

        uptSeekBar = new Thread(){
            @Override
            public void run() {
                int Totalduration = monLecteur.getDuration();
                int positionActu = 0;
                while (positionActu<Totalduration){
                    try{
                        sleep(500);
                        positionActu = monLecteur.getCurrentPosition();
                        seekBar.setProgress(positionActu);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(monLecteur.getDuration());
        uptSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                monLecteur.seekTo(seekBar.getProgress());
            }
        });

        String endTime = createTime(monLecteur.getDuration());
        txtsstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(monLecteur.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monLecteur.isPlaying())
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    monLecteur.pause();
                }else{
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    monLecteur.start();
                }
            }
        });
        //suivant listner
        monLecteur.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnNext.performClick();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monLecteur.stop();
                monLecteur.release();
                position = ((position+1)%maChan.size());
                Uri u = Uri.parse(maChan.get(position).toString());
                monLecteur = MediaPlayer.create(getApplicationContext(),u);
                sname = maChan.get(position).getName();
                txtsname.setText(sname);
                monLecteur.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monLecteur.stop();
                monLecteur.release();
                position = ((position-1)<0)?(maChan.size()-1):(position-1);
                Uri u = Uri.parse(maChan.get(position).toString());
                monLecteur = MediaPlayer.create(getApplicationContext(),u);
                sname = maChan.get(position).getName();
                txtsname.setText(sname);
                monLecteur.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
            }
        });

        btnFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monLecteur.isPlaying()){
                    monLecteur.seekTo(monLecteur.getCurrentPosition()+10000);
                }
            }
        });
        btnFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monLecteur.isPlaying()){
                    monLecteur.seekTo(monLecteur.getCurrentPosition()-10000);
                }
            }
        });
    }
    public void startAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if(sec<10){
            time+="0";
        }
        time+=sec;

        return time;
    }
    //********************************************


}