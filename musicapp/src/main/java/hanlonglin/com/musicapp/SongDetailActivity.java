package hanlonglin.com.musicapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import hanlonglin.com.musicapp.model.Song;
import hanlonglin.com.musicapp.service.MusicService;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "SongDetailActivity";
    TextView txt_title;
    ProgressBar progressBar;
    TextView txt_time;
    ImageView img_previous;
    ImageView img_start;
    ImageView img_next;

    MusicService.MusicControl musicControl;
    private int currentIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        initView();
        getIntentData();

        Intent intent = new Intent(SongDetailActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("index", -1);
    }

    //每次切换歌曲或点击按钮更新显示
    private void updateSongView() {
        if(musicControl!=null) {
            Song song = musicControl.getCurrentSongList().get(musicControl.getCurrentIndex());
            txt_title.setText(song.getName());
            if (musicControl.getState() == MusicService.MusicControl.STATE_PAUSE) {
                img_start.setImageResource(R.drawable.start);
            } else {
                img_start.setImageResource(R.drawable.pause);
            }
        }
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        progressBar = (ProgressBar) findViewById(R.id.seekBar);
        txt_time = (TextView) findViewById(R.id.txt_time);
        img_previous = (ImageView) findViewById(R.id.img_previous);
        img_next = (ImageView) findViewById(R.id.img_next);
        img_start = (ImageView) findViewById(R.id.img_start);

        progressBar.setMax(100);

        img_start.setOnClickListener(this);
        img_previous.setOnClickListener(this);
        img_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_next:
                if (musicControl != null) {
                    musicControl.next();
                    updateSongView();
                }
                break;
            case R.id.img_previous:
                if (musicControl != null) {
                    musicControl.previous();
                    updateSongView();
                }
                break;
            case R.id.img_start:
                if (musicControl != null) {
                    musicControl.play();
                    updateSongView();
                }
                break;
        }
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected 已连接");
            musicControl = (MusicService.MusicControl) service;
            if (musicControl != null) {
                musicControl.setCurrentSongList(MusicApplication.getInstance().getCurrentSonglist());
                //播放
                if (currentIndex == -1) {
                    Toast.makeText(SongDetailActivity.this, "播放失败！index=-1", Toast.LENGTH_SHORT).show();
                    return;
                }
                musicControl.setCurrentIndex(currentIndex);
                musicControl.play();
                txt_time.setText(musicControl.getCurrentDuration()+"");
                musicControl.setProgressHandler(ProgressHandler);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected 已断开");
        }
    };

    Handler ProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("TAG","接收到msg:"+msg.arg1);
            int progress=msg.arg1;
            progressBar.setProgress(progress);
        }
    };

    public static void actionStart(Context context, int index) {
        Intent intent = new Intent(context, SongDetailActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }
}
