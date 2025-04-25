package com.example.laba5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentVideo extends Fragment {

    private static final int PICK_VIDEO_REQUEST = 2;
    private VideoView videoView;
    private MediaController mediaController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        videoView = view.findViewById(R.id.videoView);

        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        selectFileButton.setOnClickListener(v -> openFilePicker());

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && mimeType.startsWith("video/")) {
                videoView.setVideoURI(uri);
                videoView.start();
                videoView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "Выберите видеофайл", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        if (mediaController != null) {
            mediaController.hide();
        }
    }
}