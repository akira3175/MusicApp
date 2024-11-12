package com.example.magicmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.magicmusic.R;
import com.example.magicmusic.models.JamendoResponse;
import com.example.magicmusic.models.Track;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Track> trackList;
    private MusicAdapter.OnItemClickListener onItemClickListener;
    private Context context;

    public MusicAdapter(Context context, List<Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.trackName.setText(track.getName());
        holder.artistName.setText(track.getArtist_name());
        Glide.with(context).load(track.getImage()).placeholder(R.drawable.ic_music_note).error(R.drawable.ic_music_note).into(holder.imageSong);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(track);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setOnItemClickListener(MusicAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Track track);
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView trackName;
        TextView artistName;
        ImageView imageSong;

        public MusicViewHolder(View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.trackName);
            artistName = itemView.findViewById(R.id.artistName);
            imageSong = itemView.findViewById(R.id.imageSong);
        }
    }
}
