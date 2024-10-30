package com.example.magicmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.R;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

  private List<Integer> imageList;

  public ImageSliderAdapter(List<Integer> imageList) {
    this.imageList = imageList;
  }

  @NonNull
  @Override
  public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_item, parent, false);
    return new SliderViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
    holder.bind(imageList.get(position));
  }

  @Override
  public int getItemCount() {
    return imageList.size();
  }

  public static class SliderViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;

    public SliderViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.imageView);
    }

    public void bind(int imageResId) {
      imageView.setImageResource(imageResId);
    }
  }

}

