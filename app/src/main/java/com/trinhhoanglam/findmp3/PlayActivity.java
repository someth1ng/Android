package com.trinhhoanglam.findmp3;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayActivity extends Activity implements View.OnClickListener {
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    SeekBar seekbarSong;
    Button btnPlay, btnStop, btnNext, btnPrev;
    TextView curDur, maxDur, txtTitle;
    Uri u;
    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        seekbarSong = (SeekBar) findViewById(R.id.seekbarSong);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        curDur = (TextView) findViewById(R.id.txtTime1);
        maxDur = (TextView) findViewById(R.id.txtTime2);

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentDuration = 0;
                seekbarSong.setMax(totalDuration);
                setTime(1);
                while (currentDuration < totalDuration) {
                    try {
                        sleep(500);
                        currentDuration = mp.getCurrentPosition();
                        seekbarSong.setProgress(currentDuration);
                        setTime(0);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                mp.stop();
//                mp.release();
//                position = (position + 1 > mySongs.size() - 1) ? 0 : position + 1;
//                u = Uri.parse(mySongs.get(position ).toString());
//                mp = MediaPlayer.create(getApplicationContext(), u);
//                mp.start();
//                seekbarSong.setMax(mp.getDuration());
//                setTime(1);
            }
        };

        if (mp != null) {
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songList");
        position = b.getInt("pos", 0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        txtTitle.setText(mySongs.get(position).getName().replace(".mp3",""));

        seekbarSong.setMax(mp.getDuration());
        setTime(1);

        updateSeekBar.start();
        seekbarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnPlay:
                if (mp.isPlaying()){
                    mp.pause();
                    btnPlay.setText("▶");
                }
                else {
                    mp.start();
                    btnPlay.setText("||");
                }
                break;
            case R.id.btnStop:
                mp.stop();
                mp.release();
                btnPlay.setText("▶");
                break;
            case R.id.btnNext:
                {
                    mp.stop();
                    mp.release();
                    position = (position + 1 > mySongs.size() - 1) ? 0 : position + 1;
                    u = Uri.parse(mySongs.get(position ).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    txtTitle.setText(mySongs.get(position).getName().replace(".mp3",""));
                    seekbarSong.setMax(mp.getDuration());
                    setTime(1);
                }
                break;
            case R.id.btnPrev:
                {
                    mp.stop();
                    mp.release();
                    position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
                    u = Uri.parse(mySongs.get(position ).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    txtTitle.setText(mySongs.get(position).getName().replace(".mp3",""));
                    seekbarSong.setMax(mp.getDuration());
                    setTime(1);
                }
                break;
        }
    }

    public void setTime(int type) {
        SimpleDateFormat minFormat = new SimpleDateFormat("mm:ss");
        if (type == 1)
            maxDur.setText(minFormat.format(mp.getDuration()));
        else
            curDur.setText(minFormat.format(mp.getCurrentPosition()));
    }
}
