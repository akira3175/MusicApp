package com.example.magicmusic.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.magicmusic.Database.FavoriteTrackDAO;
import com.example.magicmusic.Database.FavoriteTrackDTO;
import com.example.magicmusic.R;
import com.example.magicmusic.models.JamendoResponse;
import com.example.magicmusic.models.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
  private List<Track> trackList;
  private Context context;
  private OnItemClickListener onItemClickListener;
  ExecutorService executorService = Executors.newSingleThreadExecutor();
  private FavoriteTrackDAO favoriteTrackDAO;


  public interface OnItemClickListener {
    void onItemClick(Track track, int index);
  }

  public TrackAdapter(List<Track> trackList, Context context, OnItemClickListener onItemClickListener) {
    this.trackList = trackList;
    this.context = context;
    this.onItemClickListener = onItemClickListener;
  }

  public static class TrackViewHolder extends RecyclerView.ViewHolder {
    ImageView albumCover;
    TextView trackTitle;
    TextView trackArtist;
    ImageView favButton;

    public TrackViewHolder(@NonNull View itemView) {
      super(itemView);
      albumCover = itemView.findViewById(R.id.album_cover);
      trackTitle = itemView.findViewById(R.id.track_title);
      trackArtist = itemView.findViewById(R.id.track_artist);
      favButton = itemView.findViewById(R.id.fav_button);
    }

    public void bind(final Track track, final OnItemClickListener listener, Context context, int position) {
      trackTitle.setText(track.getName());
      trackArtist.setText(track.getArtist_name());

      Glide.with(context)
              .load(track.getImage())
              .placeholder(R.drawable.ic_music_note)
              .into(albumCover);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onItemClick(track, position);
        }
      });

      favButton.setOnClickListener(new View.OnClickListener() {
        private boolean isFavorite = false;

        @Override
        public void onClick(View v) {
          isFavorite = !isFavorite; // Chuyển trạng thái yêu thích
          favButton.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_non_favorite);
        }
      });
    }
  }

  @NonNull
  @Override
  public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
    return new TrackViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
    holder.bind(trackList.get(position), onItemClickListener, context, position);
  }

  @Override
  public int getItemCount() {
    return trackList.size();
  }

  public void addItem(Track track) {
    trackList.add(track);
    notifyItemInserted(trackList.size()-1);
  }

  public void insertAll(List<FavoriteTrackDTO> favoriteTracks) {
    executorService.execute(() -> {
      try {
        favoriteTrackDAO.insertAllFavoriteTrack(favoriteTracks);
      } catch (Exception e) {
        Log.e("Database", "Có lỗi thực thi truy vấn");
      }
    });
  }
}
