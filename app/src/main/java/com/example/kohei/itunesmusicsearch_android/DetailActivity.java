package com.example.kohei.itunesmusicsearch_android;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String trackName = (String) getIntent().getExtras().get("track_name");
      //  getActionBar().setTitle(trackName);

        String previewUrl = (String) getIntent().getExtras().getString("preview_url");
        if (!TextUtils.isEmpty(previewUrl)) {
            VideoView videoView = (VideoView) findViewById(R.id.video_view);
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(Uri.parse(previewUrl));
            videoView.start();
        }
    }

}
