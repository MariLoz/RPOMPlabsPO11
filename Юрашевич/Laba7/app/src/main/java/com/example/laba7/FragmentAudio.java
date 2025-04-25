package com.example.laba7;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FragmentAudio extends Fragment {
    private static final int PICK_AUDIO_REQUEST = 3;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private HistoryDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        dbHelper = new HistoryDatabaseHelper(getActivity());

        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        Button helpButton = view.findViewById(R.id.helpButton);
        Button historyButton = view.findViewById(R.id.historyButton);

        mediaController = new MediaController(getActivity()) {
            @Override public void hide() {}
        };
        mediaController.setAnchorView(view);

        selectFileButton.setOnClickListener(v -> openFilePicker());
        helpButton.setOnClickListener(v -> showHelp());
        historyButton.setOnClickListener(v -> showAudioHistory());

        return view;
    }

    private void showAudioHistory() {
        List<HistoryDatabaseHelper.HistoryItem> history = dbHelper.getHistoryByType("Аудио");

        StringBuilder historyText = new StringBuilder();
        for (HistoryDatabaseHelper.HistoryItem item : history) {
            historyText.append("Имя: ").append(item.name)
                    .append("\nФормат: ").append(item.format)
                    .append("\nВремя: ").append(item.timestamp)
                    .append("\n\n");
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("История аудио")
                .setMessage(historyText.length() > 0 ? historyText.toString() : "История пуста")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showHelp() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Справка: Работа с аудио")
                .setMessage("1. Нажмите 'Выберите аудио'\n" +
                        "2. Выберите аудиофайл (MP3)\n" +
                        "3. Управляйте воспроизведением\n\n" +
                        "Поддерживаемые форматы:\n- MP3")
                .setPositiveButton("OK", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && mimeType.equals("audio/mpeg")) {
                try {
                    initializeMediaPlayer(uri);

                    String fileName = getFileName(uri);
                    String fileFormat = "MP3";

                    dbHelper.addToHistory("Аудио", fileName, fileFormat);
                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            "Ошибка загрузки аудио",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(),
                        "Поддерживается только MP3 формат",
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

    private void initializeMediaPlayer(Uri uri) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(getActivity(), uri);

        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                    @Override public void start() { mediaPlayer.start(); }
                    @Override public void pause() { mediaPlayer.pause(); }
                    @Override public int getDuration() { return mediaPlayer.getDuration(); }
                    @Override public int getCurrentPosition() { return mediaPlayer.getCurrentPosition(); }
                    @Override public void seekTo(int pos) { mediaPlayer.seekTo(pos); }
                    @Override public boolean isPlaying() { return mediaPlayer.isPlaying(); }
                    @Override public int getBufferPercentage() { return 0; }
                    @Override public boolean canPause() { return true; }
                    @Override public boolean canSeekBackward() { return true; }
                    @Override public boolean canSeekForward() { return true; }
                    @Override public int getAudioSessionId() { return mediaPlayer.getAudioSessionId(); }
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
            Toast.makeText(getActivity(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}