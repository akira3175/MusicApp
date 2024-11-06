package com.example.magicmusic.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.magicmusic.R;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.Track;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

  private List<Playlist> dataList;
  private OnItemClickListener onItemClickListener;
  private Context context;

  public interface OnItemClickListener {
    void onItemClick(Playlist playlist);
  }

  public PlaylistAdapter(List<Playlist> dataList, Context context, OnItemClickListener onItemClickListener) {
    this.dataList = dataList;
    this.onItemClickListener = onItemClickListener;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.playlist_item, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Thiết lập nội dung cho TextView
    holder.title.setText(dataList.get(position).getName());

    Playlist playlist = dataList.get(position);
    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        Log.d("PlaylistAdapter", "Playlist tracks size:"+playlist.getTracks().size());
        onItemClickListener.onItemClick(playlist);
      }
    });
//    Glide.with(context)
//            .load(playlist.getImage())
//            .placeholder(R.drawable.ic_music_note)
//            .into(holder.backgroundImage);
  }


  @Override
  public int getItemCount() {
    return dataList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    ConstraintLayout backgroundImage;

    public ViewHolder(View view) {
      super(view);
      title = view.findViewById(R.id.title);
      backgroundImage = view.findViewById(R.id.background_image);
    }
  }
}
