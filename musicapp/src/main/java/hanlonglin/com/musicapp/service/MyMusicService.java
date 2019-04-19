package hanlonglin.com.musicapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hanlonglin.com.musicapp.R;
import hanlonglin.com.musicapp.SongDetailActivity;
import hanlonglin.com.musicapp.model.Song;

import static hanlonglin.com.musicapp.service.MyMusicService.MyMusicControl.STATE_PLAYING;

public class MyMusicService extends Service {

    private MediaPlayer mediaPlayer;
    Notification notification;
    private RemoteViews remoteViews;
    private NotificationManager notificationManager;
    private final static int NOTIFY_ID = 0;
    private MyMusicControl musicControl;


    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        musicControl = new MyMusicControl();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicControl;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getStringExtra("action");
        if (action != null) {
            Log.e("MyMusicService", "action:" + action);
            if (action.equals("previous"))
                musicControl.previous();
            else if (action.equals("next"))
                musicControl.next();
            else if (action.equals("start"))
                musicControl.play();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyMusicControl extends Binder {

        public final static int STATE_PLAYING = 0;
        public final static int STATE_PAUSE = 1;
        int state = STATE_PAUSE;

        Song currentSong = null; //正在播放的音乐
        List<Song> currentSongList = new ArrayList<>();
        private int currentIndex = 0;
        Timer timer = new Timer();
        UpdateProgressTask updateProgressTask = null;
        List<OnMusicListener> onMusicListenerList = new ArrayList<>();
        Handler progressHandler = new Handler(Looper.getMainLooper());

        public MyMusicControl() {
            restartTimer();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });
        }

        private void restartTimer() {
            if (updateProgressTask != null) {
                updateProgressTask.cancel();
            }
            updateProgressTask = new UpdateProgressTask();
            timer.schedule(updateProgressTask, 1000, 1000);
        }

        public void bindView(View v_previous, View v_start, View v_next, final SeekBar seekBar) {
            v_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previous();
                }
            });
            v_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next();
                }
            });
            v_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play();
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            });
        }

        public void setCurrentSongList(List<Song> currentSongList) {
            this.currentSongList = currentSongList;
        }

        public List<Song> getCurrentSongList() {
            return currentSongList;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int getState() {
            return state;
        }

        public int getCurrentDuration() {
            return mediaPlayer.getDuration();
        }

        public void addOnMusicListener(OnMusicListener onMusicListener) {
            if (null != onMusicListener)
                if (!onMusicListenerList.contains(onMusicListener))
                    onMusicListenerList.add(onMusicListener);
        }

        public void play() {
            Log.e("MyMusicService","play()");
            Song song = currentSongList.get(currentIndex);
            try {
                if (currentSong == null || !currentSong.equals(song)) {  //不同的歌

                    restartTimer();

                    currentSong = song;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(song.getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    state = STATE_PLAYING;

                    for (OnMusicListener onMusicListener : onMusicListenerList)
                        onMusicListener.onStart(song);
                } else {   //同一首歌
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        state = STATE_PAUSE;

                        for (OnMusicListener onMusicListener : onMusicListenerList)
                            onMusicListener.onPause(song);
                    } else {
                        mediaPlayer.start();
                        state = STATE_PLAYING;
                        for (OnMusicListener onMusicListener : onMusicListenerList)
                            onMusicListener.onStart(song);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("MyMusicService","exception:"+e.getMessage());
            }
            Notification notification=getNotification(song,state);
            notificationManager.notify(NOTIFY_ID, notification);
        }

        public void next() {
            if (currentSongList.size() != 0) {
                int oldIndex = currentIndex;
                currentIndex = (currentIndex + 1) > (currentSongList.size() - 1) ? 0 : currentIndex + 1;
                play();
                for (OnMusicListener onMusicListener : onMusicListenerList)
                    onMusicListener.onNext(currentSongList.get(oldIndex), currentSongList.get(currentIndex));
            }

        }

        public void previous() {
            if (currentSongList.size() != 0) {
                int oldIndex = currentIndex;
                currentIndex = (currentIndex - 1) < 0 ? currentSongList.size() - 1 : currentIndex - 1;
                play();
                for (OnMusicListener onMusicListener : onMusicListenerList)
                    onMusicListener.onNext(currentSongList.get(oldIndex), currentSongList.get(currentIndex));
            }
        }

        class UpdateProgressTask extends TimerTask {
            @Override
            public void run() {
                Log.e("TAG", "正在执行时间任务");

                progressHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (OnMusicListener onMusicListener : onMusicListenerList)
                            onMusicListener.onUpdateProgress(mediaPlayer.getCurrentPosition());
                    }
                });
            }
        }

    }

    public interface OnMusicListener {

        /**
         * This will be called when the progress is updated
         * you should update your progressBar here
         *
         * @param progress
         */
        public void onUpdateProgress(int progress);

        /**
         * This will be called when you start a new song or when you click pause/start
         *
         * @param song
         */
        public void onStart(Song song);

        /**
         * This will be called when pause the song
         *
         * @param song
         */
        public void onPause(Song song);

        /**
         * This will be called when click next song
         *
         * @param old
         * @param now
         */
        public void onNext(Song old, Song now);

        /**
         * This will be called when click last song
         *
         * @param old
         * @param now
         */
        public void onPrevious(Song old, Song now);
    }

    private Notification getNotification(Song song, int state) {
        if (remoteViews == null)
            remoteViews = new RemoteViews(getPackageName(), R.layout.view_music_control);
        remoteViews.setTextViewText(R.id.txt_title, song.getName());
        remoteViews.setTextViewText(R.id.txt_singer, song.getSinger());

        Intent mIntent = new Intent(this, MyMusicService.class);
        mIntent.putExtra("action", "previous");
        PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, mIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_previous, previousPendingIntent);

        Intent nextIntent = new Intent(this, MyMusicService.class);
        nextIntent.putExtra("action", "next");
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 1, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_next, nextPendingIntent);

        Intent startIntent = new Intent(this, MyMusicService.class);
        startIntent.putExtra("action", "start");
        PendingIntent startPendingIntent = PendingIntent.getService(this, 2, startIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_start, startPendingIntent);

        if (state == STATE_PLAYING)
            remoteViews.setImageViewResource(R.id.img_start, R.drawable.pause);
        else
            remoteViews.setImageViewResource(R.id.img_start, R.drawable.start);
        Notification.Builder builder = new Notification.Builder(MyMusicService.this);
        builder.setSmallIcon(R.drawable.start);
        builder.setContent(remoteViews);
        Intent intent = new Intent(MyMusicService.this, SongDetailActivity.class);
        intent.putExtra("index", musicControl.getCurrentIndex());
        intent.putExtra("from","notification");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), song.hashCode(), intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.getNotification();
    }

}
