package com.example.magicmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.magicmusic.R;
import com.example.magicmusic.models.JamendoResponse;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<JamendoResponse.Track> trackList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public SongAdapter(Context context, List<JamendoResponse.Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        JamendoResponse.Track track = trackList.get(position);
        holder.bind(track);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(JamendoResponse.Track track);
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        ImageView songImage;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_title);
            artistName = itemView.findViewById(R.id.song_artist);
            songImage = itemView.findViewById(R.id.song_image);
        }

        public void bind(JamendoResponse.Track track) {
            songName.setText(track.getName());
            artistName.setText(track.getArtist_name());
            Glide.with(itemView.getContext())
                    .load(track.getImage()) // URL từ API
                    .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh chờ mặc định
                    .error(android.R.drawable.ic_menu_report_image) // Ảnh lỗi mặc định
                    .into(songImage);
        }
    }
}
