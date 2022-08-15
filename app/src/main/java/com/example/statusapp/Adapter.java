package com.example.statusapp;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Adapterviewholder> {
    private ArrayList<Status_Item> Status;

    @NonNull
    @Override
    public Adapterviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_row, parent, false);
        Adapterviewholder avh = new Adapterviewholder(v);
        return avh;
    }

    public Adapter(ArrayList<Status_Item> status_items) {
        Status = status_items;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapterviewholder holder, int position) {
        Status_Item currentItem = Status.get(position);
        holder.img.setImageDrawable(currentItem.getStatusData());
        holder.text.setText(currentItem.getStatusText());
        holder.username.setText(currentItem.getUserName());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentItem.getStatsType().equals("IMAGE")) {

                    Intent intent = new Intent(view.getContext(), ImgView.class);
                    intent.putExtra("stu", currentItem.getStatusPath());
                    view.getContext().startActivity(intent);
                } else if(currentItem.getStatsType().equals("VIDEO")){
                    Intent intent = new Intent(view.getContext(), Player.class);
                    intent.putExtra("stu", currentItem.getStatusPath());
                    view.getContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(view.getContext(), HndelText.class);
                    intent.putExtra("stu", currentItem.getStatusText());
                    view.getContext().startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return Status.size();
    }

    public static class Adapterviewholder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text, username;

        public Adapterviewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgStatusImage);
            text = itemView.findViewById(R.id.tvStatusText);
            username = itemView.findViewById(R.id.senderName);

        }
    }


}
