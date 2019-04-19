package hanlonglin.com.musicapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import hanlonglin.com.musicapp.model.Song;
import hanlonglin.com.musicapp.service.MyMusicService;

public class SongDetailActivity extends AppCompatActivity {
    private final static String TAG = "SongDetailActivity";
    TextView txt_title;
    SeekBar seekBar;
    TextView txt_time;
    TextView txt_current;
    ImageView img_previous;
    ImageView img_start;
    ImageView img_next;

    MyMusicService.MyMusicControl musicControl;
    private int currentIndex = -1;
    MyMusicListener myMusicListener = new MyMusicListener();
    private String from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SongDetailActivity", "onCreate()");
        setContentView(R.layout.activity_song_detail);
        initView();
        getIntentData();

        Intent intent = new Intent(SongDetailActivity.this, MyMusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("SongDetailActivity", "onResume()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        getIntentData();
        Log.e("SongDetailActivity", "onNewIntent(), index:"+currentIndex);
        //重新绑定
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("SongDetailActivity", "onDestory()");
        unbindService(serviceConnection);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra("index", -1);
        from = intent.getStringExtra("from");
        if (from == null)
            from = "";

        Log.e("getIntentData()","currentIndex:"+currentIndex);
    }


    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txt_time = (TextView) findViewById(R.id.txt_time);
        txt_current = (TextView) findViewById(R.id.txt_current);
        img_previous = (ImageView) findViewById(R.id.img_previous);
        img_next = (ImageView) findViewById(R.id.img_next);
        img_start = (ImageView) findViewById(R.id.img_start);

        //seekBar.setMax(100);

    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected 已连接");
            musicControl = (MyMusicService.MyMusicControl) service;
            if (musicControl != null) {
                musicControl.setCurrentSongList(MusicApplication.getInstance().getCurrentSonglist());
                //播放
                if (currentIndex == -1) {
                    Toast.makeText(SongDetailActivity.this, "播放失败！index=-1", Toast.LENGTH_SHORT).show();
                    return;
                }
                musicControl.addOnMusicListener(myMusicListener);
                musicControl.setCurrentIndex(currentIndex);
//                if (!from.equals("notification"))
                    musicControl.play();
                musicControl.bindView(img_previous, img_start, img_next, seekBar);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected 已断开");
        }
    };


    private class MyMusicListener implements MyMusicService.OnMusicListener {

        @Override
        public void onUpdateProgress(int progress) {
            seekBar.setProgress(progress);
            Log.e("TAG", "onUpdateProgress:" + progress);
            txt_current.setText(secondsToTime(progress / 1000));
        }

        @Override
        public void onStart(Song song) {
            Log.e("MyMusicListener","onStart()");
            txt_title.setText(song.getName());
            txt_time.setText(secondsToTime(musicControl.getCurrentDuration() / 1000));
            img_start.setImageResource(R.drawable.pause);
            seekBar.setMax(musicControl.getCurrentDuration());
        }

        private String secondsToTime(int allSeconds) {
            int hours = allSeconds / 60 / 60;
            int minutes = allSeconds / 60;
            int seconds = allSeconds % 60;
            String hourStr = "";
            String minuteStr = "";
            String secondStr = "";
            if (hours != 0) {
                if (hours >= 10 && hours < 60)
                    hourStr = hours + ":";
                else if (hours < 10)
                    hourStr = "0" + hours + ":";
            }
            // if (minutes != 0) {
            if (minutes >= 10 && minutes < 60)
                minuteStr = minutes + ":";
            else if (minutes < 10)
                minuteStr = "0" + minutes + ":";
            //  }
            //  if (seconds != 0) {
            if (seconds >= 10 && seconds < 60)
                secondStr = seconds + "";
            else if (minutes < 10)
                secondStr = "0" + seconds;
            //  }
            return hourStr + minuteStr + secondStr;
        }

        @Override
        public void onPause(Song song) {
            txt_title.setText(song.getName());
            txt_time.setText(secondsToTime(musicControl.getCurrentDuration() / 1000));
            seekBar.setMax(musicControl.getCurrentDuration());
            img_start.setImageResource(R.drawable.start);
        }

        @Override
        public void onNext(Song old, Song now) {
            txt_title.setText(now.getName());
        }

        @Override
        public void onPrevious(Song old, Song now) {
            txt_title.setText(now.getName());
        }
    }

    public static void actionStart(Context context, int index) {
        Intent intent = new Intent(context, SongDetailActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }
}
