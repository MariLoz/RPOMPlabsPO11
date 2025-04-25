package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {

    private VideoView videoView;
    private Uri videoUri;


    public VideoFragment(Uri videoUri) {
        this.videoUri = videoUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        videoView = rootView.findViewById(R.id.videoView);
        Button playButton = rootView.findViewById(R.id.playButton);
        Button pauseButton = rootView.findViewById(R.id.pauseButton);
        Button stopButton = rootView.findViewById(R.id.stopButton);

        if (videoUri != null) {
            videoView.setVideoURI(videoUri);
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                videoView.resume();
            }
        });

        return rootView;
    }
}
