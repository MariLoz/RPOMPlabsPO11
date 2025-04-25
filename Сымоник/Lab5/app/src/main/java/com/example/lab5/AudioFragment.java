package com.example.lab5;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class AudioFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private Uri audioUri;

    // Конструктор для инициализации с Uri
    public AudioFragment(Uri audioUri) {
        this.audioUri = audioUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);

        Button playButton = rootView.findViewById(R.id.playButton);
        Button pauseButton = rootView.findViewById(R.id.pauseButton);
        Button stopButton = rootView.findViewById(R.id.stopButton);

        Context context = getContext();
        if (context != null && audioUri != null) {
            mediaPlayer = MediaPlayer.create(context, audioUri);
            if (mediaPlayer == null) {
                Log.e("AudioFragment", "Ошибка: MediaPlayer.create() вернул null");
            }
        } else {
            Log.e("AudioFragment", "Ошибка: Контекст или URI null");
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.prepareAsync();
            }
        });

        return rootView;
    }
}
