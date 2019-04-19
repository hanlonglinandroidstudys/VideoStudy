package hanlonglin.com.musicapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hanlonglin.com.musicapp.R;
import hanlonglin.com.musicapp.SongDetailActivity;
import hanlonglin.com.musicapp.model.Song;

public class SongAdapter extends RecyclerView.Adapter {
    List<Song> songList;
    Context context;
    public SongAdapter(Context context,List<Song> list){
        this.songList=list;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_song,parent,false);
        SongHolder holder=new SongHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SongHolder)holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class SongHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView txt_name;
        TextView txt_singer;

        public SongHolder(View itemView) {
            super(itemView);
            txt_name=(TextView)itemView.findViewById(R.id.txt_name);
            txt_singer=(TextView)itemView.findViewById(R.id.txt_singer);

            this.itemView=itemView;
        }

        public void bind(final int pos){
            txt_name.setText(songList.get(pos).getName());
            txt_singer.setText(songList.get(pos).getSinger());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongDetailActivity.actionStart(context,pos);
                }
            });
        }
    }
}
