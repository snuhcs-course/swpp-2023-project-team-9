package com.littlestudio.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.littlestudio.R;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.ui.constant.IntentExtraKey;

import java.util.ArrayList;
import java.util.Collections;

public class DrawAdapter extends RecyclerView.Adapter<DrawAdapter.ViewHolder> {
    static final String IMAGE_PATH = "image_path";
    private final Context context;
    private ArrayList<Drawing> drawingList;

    public DrawAdapter(Context context, ArrayList<Drawing> drawingList) {
        this.context = context;
        this.drawingList = drawingList;
        Collections.reverse(drawingList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return drawingList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Drawing drawing = drawingList.get(position);
        Glide.with(context).load(drawing.image_url).into(holder.drawImage);

        holder.drawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra(IntentExtraKey.DRAWING_ID, drawing.id);
                intent.putExtra(IntentExtraKey.DRAWING_IMAGE_URL, drawing.image_url);
                context.startActivity(intent);
            }
        });
        holder.titleTextView.setText(drawing.title);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView drawImage;
        final TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            drawImage = itemView.findViewById(R.id.image_draw);
            titleTextView = itemView.findViewById(R.id.gallery_title);
        }
    }

    public void updateItems(ArrayList<Drawing> drawingList) {
        this.drawingList = drawingList;
        notifyDataSetChanged();
    }
}