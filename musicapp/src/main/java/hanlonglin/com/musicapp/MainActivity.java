package hanlonglin.com.musicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hanlonglin.com.musicapp.adapter.SongAdapter;
import hanlonglin.com.musicapp.model.Song;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setRecyclerViewData();

    }

    private void setRecyclerViewData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Song> songList=new ArrayList<>();
        songList.add(new Song("那个人","英子","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131232171.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("最美的期待","周笔畅","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131229550.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("情话微甜","杨光","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131227319.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("起风了","彭清","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131226156.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("佛系少女","冯提莫","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131202421.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("爱情","刘惜君","http://sc1.111ttt.cn:8282/2017/1/05m/09/298092038446.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("情歌","侧田","http://sc1.111ttt.cn:8282/2016/1/12m/09/205091952344.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("这是没主题的歌","无","http://sc1.111ttt.cn:8282/2016/1/12m/09/205091446234.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("最后我们没在一起","白小白","http://sc1.111ttt.cn:8282/2018/1/03m/13/396131155339.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));
        songList.add(new Song("追光者","岑宁儿","http://sc1.111ttt.cn:8282/2017/1/11m/11/304112002347.m4a?tflag=1546606800&pin=97bb2268ae26c20fe093fd5b0f04be80#.mp3",Song.FROM_HTTP));

        SongAdapter songAdapter=new SongAdapter(this,songList);
        recyclerView.setAdapter(songAdapter);

        MusicApplication.getInstance().setCurrentSonglist(songList);
    }
}
