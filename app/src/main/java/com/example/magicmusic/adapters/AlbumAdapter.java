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
import com.example.magicmusic.models.AlbumResponse;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

  private List<AlbumResponse.Album> dataList;
  private OnItemClickListener onItemClickListener;
  private Context context;

  public interface OnItemClickListener {
    void onItemClick(AlbumResponse.Album album);
  }

  public AlbumAdapter(List<AlbumResponse.Album> dataList, Context context, OnItemClickListener onItemClickListener) {
    this.dataList = dataList;
    this.onItemClickListener = onItemClickListener;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.album_item, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Thiết lập nội dung cho TextView
    holder.title.setText(dataList.get(position).getName());
    holder.otherText.setText("Other Text");

    AlbumResponse.Album album = dataList.get(position);
    holder.itemView.setOnClickListener(v -> {
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(album);
      }
    });
    Glide.with(context)
            .load(album.getImage())
            .placeholder(R.drawable.ic_music_note)
            .into(holder.backgroundImage);
  }

  @Override
  public int getItemCount() {
    return dataList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView otherText;
    ImageView backgroundImage;

    public ViewHolder(View view) {
      super(view);
      title = view.findViewById(R.id.title);
      otherText = view.findViewById(R.id.other_text);
      backgroundImage = view.findViewById(R.id.background_image);
    }
  }
}
