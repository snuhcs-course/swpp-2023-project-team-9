package com.littlestudio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

public class DrawAdapter extends RecyclerView.Adapter<DrawAdapter.ViewHolder> {
    static final String IMAGE_PATH = "image_path";
    private final Context context;
    private ArrayList<String> imageList;

    public DrawAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
        Collections.reverse(imageList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String path = imageList.get(position);
        Glide.with(context).load(path).into(holder.drawImage);

        holder.drawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra(IMAGE_PATH, path);
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView drawImage;

        public ViewHolder(View itemView) {
            super(itemView);
            drawImage = itemView.findViewById(R.id.image_draw);
        }
    }

    public void updateItems(ArrayList<String> imageUrls) {
        imageList = imageUrls;
        notifyDataSetChanged();
    }
}