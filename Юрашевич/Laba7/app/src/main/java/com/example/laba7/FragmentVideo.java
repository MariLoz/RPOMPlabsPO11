package com.example.laba7;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FragmentVideo extends Fragment {
    private static final int PICK_VIDEO_REQUEST = 2;
    private VideoView videoView;
    private MediaController mediaController;
    private HistoryDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        dbHelper = new HistoryDatabaseHelper(getActivity());

        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        Button helpButton = view.findViewById(R.id.helpButton);
        Button historyButton = view.findViewById(R.id.historyButton);
        videoView = view.findViewById(R.id.videoView);

        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        selectFileButton.setOnClickListener(v -> openFilePicker());
        helpButton.setOnClickListener(v -> showHelp());
        historyButton.setOnClickListener(v -> showVideoHistory());

        return view;
    }

    private void showVideoHistory() {
        List<HistoryDatabaseHelper.HistoryItem> history = dbHelper.getHistoryByType("Видео");

        StringBuilder historyText = new StringBuilder();
        for (HistoryDatabaseHelper.HistoryItem item : history) {
            historyText.append("Имя: ").append(item.name)
                    .append("\nФормат: ").append(item.format)
                    .append("\nВремя: ").append(item.timestamp)
                    .append("\n\n");
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("История видео")
                .setMessage(historyText.length() > 0 ? historyText.toString() : "История пуста")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showHelp() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Справка: Работа с видео")
                .setMessage("1. Нажмите 'Выберите видео'\n" +
                        "2. Выберите видеофайл (MP4)\n" +
                        "3. Управляйте воспроизведением\n\n" +
                        "Поддерживаемые форматы:\n- MP4")
                .setPositiveButton("OK", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/mp4");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && mimeType.equals("video/mp4")) {
                try {
                    videoView.setVideoURI(uri);
                    videoView.start();
                    videoView.setVisibility(View.VISIBLE);

                    String fileName = getFileName(uri);
                    String fileFormat = "MP4";

                    dbHelper.addToHistory("Видео", fileName, fileFormat);
                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            "Ошибка загрузки видео",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(),
                        "Поддерживается только MP4 формат",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getActivity().getContentResolver().query(
                    uri,
                    new String[]{android.provider.OpenableColumns.DISPLAY_NAME},
                    null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}