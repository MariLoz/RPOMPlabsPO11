package com.example.laba5;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAudio extends Fragment {

    private static final int PICK_AUDIO_REQUEST = 3;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Button selectFileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        selectFileButton = view.findViewById(R.id.selectFileButton);
        selectFileButton.setOnClickListener(v -> openFilePicker());

        mediaController = new MediaController(getActivity()) {
            @Override
            public void hide() {
            }
        };

        mediaController.setAnchorView(view);

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && mimeType.startsWith("audio/")) {
                initializeMediaPlayer(uri);
            } else {
                Toast.makeText(getActivity(), "Выберите аудиофайл", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMediaPlayer(Uri uri) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getActivity(), uri);

        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                    @Override
                    public void start() {
                        mediaPlayer.start();
                    }

                    @Override
                    public void pause() {
                        mediaPlayer.pause();
                    }

                    @Override
                    public int getDuration() {
                        return mediaPlayer.getDuration();
                    }

                    @Override
                    public int getCurrentPosition() {
                        return mediaPlayer.getCurrentPosition();
                    }

                    @Override
                    public void seekTo(int pos) {
                        mediaPlayer.seekTo(pos);
                    }

                    @Override
                    public boolean isPlaying() {
                        return mediaPlayer.isPlaying();
                    }

                    @Override
                    public int getBufferPercentage() {
                        return 0;
                    }

                    @Override
                    public boolean canPause() {
                        return true;
                    }

                    @Override
                    public boolean canSeekBackward() {
                        return true;
                    }

                    @Override
                    public boolean canSeekForward() {
                        return true;
                    }

                    @Override
                    public int getAudioSessionId() {
                        return mediaPlayer.getAudioSessionId();
                    }
                });

                mediaController.setEnabled(true);
                mediaController.show(0);
                mediaPlayer.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.seekTo(0);
                mediaController.show(0);
            });
        } else {
            Toast.makeText(getActivity(), "Ошибка при воспроизведении аудио", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaController != null) {
            mediaController.hide();
        }
    }
}