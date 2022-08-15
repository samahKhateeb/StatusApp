package com.example.statusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Player extends AppCompatActivity {
    Intent intent;
    String path;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        videoView = findViewById(R.id.videoView);
        intent = getIntent();
        path = intent.getStringExtra("stu");
        try {
            PlayVideo(path, videoView);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // play Video
    public void PlayVideo(String path, VideoView player) {
        MediaController mediaController = new MediaController(player.getContext());
        Uri uri = Uri.parse(path);
        player.setVideoURI(uri);
        player.setMediaController(mediaController);
        mediaController.setAnchorView(player);
        player.start();

    }
}