package com.example.mynotes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class FragmentAdd extends Fragment {
    private EditText editText;
    private ImageView imageView;
    private Button btnSelectImage, btnRecordAudio, btnSave;
    private NotesDatabaseHelper dbHelper;

    private String imagePath;
    private String audioFilePath;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        imagePath = selectedImageUri.toString(); // Сохраняем URI вместо физического пути
                        Log.d("IMAGE_PATH", "Выбранное изображение: " + imagePath);
                        imageView.setImageURI(Uri.parse(imagePath));
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editText = view.findViewById(R.id.editText);
        imageView = view.findViewById(R.id.imageView);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnRecordAudio = view.findViewById(R.id.btnRecordAudio);
        btnSave = view.findViewById(R.id.btnSave);
        dbHelper = new NotesDatabaseHelper(getContext());

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnRecordAudio.setOnClickListener(v -> toggleRecording());
        btnSave.setOnClickListener(v -> saveNote());

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveNote() {
        String description = editText.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Введите описание заметки", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addNote(description, imagePath != null ? imagePath : "", audioFilePath != null ? audioFilePath : "");

        Toast.makeText(getContext(), "Заметка сохранена", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 200);
            return;
        }

        File audioFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "audio_" + System.currentTimeMillis() + ".3gp");
        audioFilePath = audioFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnRecordAudio.setText("Остановить запись");
            Toast.makeText(getContext(), "Запись началась", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
            Log.e("AUDIO", "Ошибка при старте записи", e);
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btnRecordAudio.setText("Записать аудио");
            Toast.makeText(getContext(), "Запись сохранена", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), "Ошибка при остановке записи", Toast.LENGTH_SHORT).show();
            Log.e("AUDIO", "Ошибка при остановке записи", e);
        }
    }
}