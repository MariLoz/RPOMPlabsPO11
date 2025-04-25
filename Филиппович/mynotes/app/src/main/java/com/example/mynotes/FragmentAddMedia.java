package com.example.mynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class FragmentAddMedia extends Fragment {
    private static final int PICK_IMAGE = 1;
    private static final int RECORD_AUDIO = 2;

    private NotesDatabaseHelper dbHelper;
    private EditText editText;
    private ImageView imageView;
    private Uri imageUri;
    private String audioPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_media, container, false);

        dbHelper = new NotesDatabaseHelper(getContext());
        editText = view.findViewById(R.id.editText);
        imageView = view.findViewById(R.id.imageView);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        Button btnRecordAudio = view.findViewById(R.id.btnRecordAudio);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnRecordAudio.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, RECORD_AUDIO);
        });

        btnSave.setOnClickListener(v -> {
            String description = editText.getText().toString().trim();
            String imagePath = (imageUri != null) ? imageUri.toString() : "";
            dbHelper.addNote(description, imagePath, audioPath);
            Toast.makeText(getContext(), "Заметка сохранена", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        } else if (requestCode == RECORD_AUDIO && data != null) {
            audioPath = data.getData().toString();
        }
    }
}
