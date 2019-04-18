package hanlonglin.com.musicapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hanlonglin.com.musicapp.model.Song;

public class MusicService extends Service {
    MusicControl musicControl = new MusicControl();
    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicControl;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicControl extends Binder {

        public final static int STATE_PLAYING = 0;
        public final static int STATE_PAUSE = 1;
        int state = STATE_PAUSE;

        Song currentSong = null; //正在播放的音乐
        List<Song> currentSongList = new ArrayList<>();
        private int currentIndex = 0;
        Handler progressHandler;
        Timer timer=new Timer();
        UpdateProgressTask updateProgressTask=null;

        public MusicControl(){
            restartTimer();
        }

        private void restartTimer(){
            if(updateProgressTask!=null){
                updateProgressTask.cancel();
            }
            updateProgressTask=new UpdateProgressTask();
            timer.schedule(updateProgressTask,1000,500);
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

        public void setProgressHandler(Handler progressHandler) {
            this.progressHandler = progressHandler;
        }

        public int getCurrentDuration() {
            return mediaPlayer.getDuration();
        }

        public void play() {
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
                } else {   //同一首歌
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        state = STATE_PAUSE;
                    } else {
                        mediaPlayer.start();
                        state = STATE_PLAYING;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void next() {
            if (currentSongList.size() != 0) {
                currentIndex = (currentIndex + 1) > (currentSongList.size() - 1) ? 0 : currentIndex + 1;
                play();
            }

        }

        public void previous() {
            if (currentSongList.size() != 0) {
                currentIndex = (currentIndex - 1) < 0 ? currentSongList.size() - 1 : currentIndex - 1;
                play();
            }
        }

        class UpdateProgressTask extends TimerTask {
            @Override
            public void run() {
                Log.e("TAG","正在执行时间任务");
                if (progressHandler != null) {
                    Message mes = Message.obtain();
                    mes.what = 0;
                    mes.arg1 = (int)(((float)mediaPlayer.getCurrentPosition()/(float)mediaPlayer.getDuration())*100);
                    progressHandler.sendMessage(mes);
                }
            }
        };
    }

}
