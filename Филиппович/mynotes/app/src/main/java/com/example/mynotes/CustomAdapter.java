package com.example.mynotes;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;
    private LayoutInflater inflater;
    private MediaPlayer mediaPlayer;

    public CustomAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
        this.mediaPlayer = new MediaPlayer();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_note, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        Button playButton = convertView.findViewById(R.id.playButton);

        Note note = notes.get(position);
        textView.setText(note.getDescription());

        if (note.getImagePath() != null && !note.getImagePath().isEmpty()) {
            imageView.setImageURI(Uri.parse(note.getImagePath()));
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }

        if (note.getAudioPath() != null && !note.getAudioPath().isEmpty()) {
            File audioFile = new File(note.getAudioPath());
            if (audioFile.exists()) {
                playButton.setVisibility(View.VISIBLE);
                playButton.setOnClickListener(v -> playAudio(note.getAudioPath()));
            } else {
                playButton.setVisibility(View.GONE);
            }
        } else {
            playButton.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void playAudio(String audioPath) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            Log.d("AUDIO", "Попытка воспроизведения: " + audioPath);
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            if (mediaPlayer.isPlaying()) {
                Toast.makeText(context, "Аудиозапись включилась", Toast.LENGTH_SHORT).show();
                Log.d("AUDIO", "Аудиозапись запущена успешно");
            } else {
                Toast.makeText(context, "Ошибка воспроизведения аудио", Toast.LENGTH_SHORT).show();
                Log.d("AUDIO", "MediaPlayer не запустился");
            }

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.stop();
                mp.reset();
                Toast.makeText(context, "Воспроизведение завершено", Toast.LENGTH_SHORT).show();
                Log.d("AUDIO", "Воспроизведение завершено");
            });

        } catch (IOException e) {
            Toast.makeText(context, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            Log.e("AUDIO", "Ошибка при запуске MediaPlayer", e);
        }
    }
}
