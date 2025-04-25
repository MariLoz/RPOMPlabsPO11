package com.example.laba5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentPhoto extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        imageView = view.findViewById(R.id.imageView);

        selectFileButton.setOnClickListener(v -> openFilePicker());

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && mimeType.startsWith("image/")) {
                imageView.setImageURI(uri);
                imageView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "Выберите файл изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }
}