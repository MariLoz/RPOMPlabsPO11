package com.example.mynotes;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;

public class FragmentShow extends Fragment {

    private NotesDatabaseHelper dbHelper;
    private ListView listView;
    private CustomAdapter adapter;
    private List<Note> notesList;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        dbHelper = new NotesDatabaseHelper(getContext());
        listView = view.findViewById(R.id.listView);

        notesList = dbHelper.getAllNotes();

        if (notesList.isEmpty()) {
            Toast.makeText(getContext(), "Нет заметок", Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomAdapter(getContext(), notesList);
        listView.setAdapter(adapter);

        // Воспроизведение аудио при клике на элемент списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = notesList.get(position);
                playAudio(selectedNote.getAudioPath());
            }
        });

        return view;
    }

    private void playAudio(String audioFilePath) {
        if (audioFilePath == null || audioFilePath.isEmpty()) {
            Toast.makeText(getContext(), "Аудиофайл не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getContext(), "Воспроизведение...", Toast.LENGTH_SHORT).show();

            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(getContext(), "Аудио закончилось", Toast.LENGTH_SHORT).show();
                mediaPlayer.release();
                mediaPlayer = null;
            });

        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }
}
