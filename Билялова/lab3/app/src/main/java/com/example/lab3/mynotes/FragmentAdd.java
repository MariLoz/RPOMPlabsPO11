package com.example.lab3.mynotes;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import com.example.lab3.R;
import java.io.IOException;

public class FragmentAdd extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editText;
    private ImageView imageView;
    private Uri imageUri;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        editText = view.findViewById(R.id.editTextNote);
        imageView = view.findViewById(R.id.imageView);
        Button btnChooseImage = view.findViewById(R.id.btnChooseImage);
        Button btnAddNote = view.findViewById(R.id.btnAddNote);
        dbHelper = new DatabaseHelper(getContext());

        btnChooseImage.setOnClickListener(v -> openGallery());
        btnAddNote.setOnClickListener(v -> addNote());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNote() {
        String name = editText.getText().toString();
        String imagePath = imageUri != null ? imageUri.toString() : "";

        if (!name.isEmpty()) {
            if (dbHelper.addNote(name, imagePath)) {
                Toast.makeText(getContext(), "Продукт добавлен!", Toast.LENGTH_SHORT).show();
                editText.setText("");
                imageView.setImageResource(R.drawable.placeholder);
            } else {
                Toast.makeText(getContext(), "Ошибка при добавлении!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Введите название!", Toast.LENGTH_SHORT).show();
        }
    }
}
