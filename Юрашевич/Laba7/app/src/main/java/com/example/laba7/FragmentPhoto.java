package com.example.laba7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentPhoto extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private ImageView imageView;
    private HistoryDatabaseHelper dbHelper;
    private float currentScale = 1.0f;
    private float currentRotation = 0f;
    private Bitmap originalBitmap;
    private String currentPhotoPath;
    private static final float SCALE_STEP = 0.2f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 3.0f;
    private static final float ROTATION_STEP = 90f;

    private Button zoomInButton;
    private Button zoomOutButton;
    private Button rotateLeftButton;
    private Button rotateRightButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        dbHelper = new HistoryDatabaseHelper(getActivity());

        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        Button takePhotoButton = view.findViewById(R.id.takePhotoButton);
        Button helpButton = view.findViewById(R.id.helpButton);
        Button historyButton = view.findViewById(R.id.historyButton);
        zoomInButton = view.findViewById(R.id.zoomInButton);
        zoomOutButton = view.findViewById(R.id.zoomOutButton);
        rotateLeftButton = view.findViewById(R.id.rotateLeftButton);
        rotateRightButton = view.findViewById(R.id.rotateRightButton);
        imageView = view.findViewById(R.id.imageView);

        setTransformButtonsEnabled(false);

        selectFileButton.setOnClickListener(v -> openFilePicker());
        takePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        helpButton.setOnClickListener(v -> showHelp());
        historyButton.setOnClickListener(v -> showPhotoHistory());

        zoomInButton.setOnClickListener(v -> {
            if (originalBitmap == null) {
                Toast.makeText(getActivity(), "Сначала загрузите изображение", Toast.LENGTH_SHORT).show();
                return;
            }
            currentScale = Math.min(currentScale + SCALE_STEP, MAX_SCALE);
            applyScale();
        });

        zoomOutButton.setOnClickListener(v -> {
            if (originalBitmap == null) {
                Toast.makeText(getActivity(), "Сначала загрузите изображение", Toast.LENGTH_SHORT).show();
                return;
            }
            currentScale = Math.max(currentScale - SCALE_STEP, MIN_SCALE);
            applyScale();
        });

        rotateLeftButton.setOnClickListener(v -> {
            if (originalBitmap == null) {
                Toast.makeText(getActivity(), "Сначала загрузите изображение", Toast.LENGTH_SHORT).show();
                return;
            }
            currentRotation = (currentRotation - ROTATION_STEP) % 360;
            applyRotation();
        });

        rotateRightButton.setOnClickListener(v -> {
            if (originalBitmap == null) {
                Toast.makeText(getActivity(), "Сначала загрузите изображение", Toast.LENGTH_SHORT).show();
                return;
            }
            currentRotation = (currentRotation + ROTATION_STEP) % 360;
            applyRotation();
        });

        return view;
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Ошибка при создании файла", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.laba7.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setTransformButtonsEnabled(boolean enabled) {
        zoomInButton.setEnabled(enabled);
        zoomOutButton.setEnabled(enabled);
        rotateLeftButton.setEnabled(enabled);
        rotateRightButton.setEnabled(enabled);

        float alpha = enabled ? 1.0f : 0.5f;
        zoomInButton.setAlpha(alpha);
        zoomOutButton.setAlpha(alpha);
        rotateLeftButton.setAlpha(alpha);
        rotateRightButton.setAlpha(alpha);
    }

    private void applyScale() {
        imageView.setScaleX(currentScale);
        imageView.setScaleY(currentScale);
    }

    private void applyRotation() {
        imageView.setRotation(currentRotation);
    }

    private void resetTransformations() {
        currentScale = 1.0f;
        currentRotation = 0f;
        imageView.setScaleX(1.0f);
        imageView.setScaleY(1.0f);
        imageView.setRotation(0f);
    }

    private void showPhotoHistory() {
        List<HistoryDatabaseHelper.HistoryItem> history = dbHelper.getHistoryByType("Фото");

        StringBuilder historyText = new StringBuilder();
        for (HistoryDatabaseHelper.HistoryItem item : history) {
            historyText.append("Имя: ").append(item.name)
                    .append("\nФормат: ").append(item.format)
                    .append("\nВремя: ").append(item.timestamp)
                    .append("\n\n");
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("История фото")
                .setMessage(historyText.length() > 0 ? historyText.toString() : "История пуста")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showHelp() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Справка: Работа с фото")
                .setMessage("1. Нажмите 'Выберите фото' для загрузки из галереи\n" +
                        "2. Или нажмите 'Сделать фото' для съемки новой фотографии\n" +
                        "3. Используйте кнопки 'Увеличить' и 'Уменьшить' для масштабирования\n" +
                        "4. Используйте кнопки 'Повернуть влево' и 'Повернуть вправо' для вращения\n\n" +
                        "Поддерживаемые форматы:\n- JPG/JPEG\n- PNG")
                .setPositiveButton("OK", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg|image/png");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (mimeType != null && (mimeType.equals("image/jpeg") ||
                    mimeType.equals("image/jpg") ||
                    mimeType.equals("image/png"))) {
                try {
                    if (originalBitmap != null) {
                        originalBitmap.recycle();
                    }

                    originalBitmap = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), uri);

                    resetTransformations();

                    imageView.setImageBitmap(originalBitmap);
                    imageView.setVisibility(View.VISIBLE);

                    setTransformButtonsEnabled(true);

                    String fileName = getFileName(uri);
                    String fileFormat = mimeType.substring(mimeType.lastIndexOf("/") + 1).toUpperCase();

                    dbHelper.addToHistory("Фото", fileName, fileFormat);
                } catch (IOException e) {
                    Toast.makeText(getActivity(),
                            "Ошибка загрузки изображения",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(),
                        "Поддерживаются только JPG/JPEG и PNG форматы",
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            originalBitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

            if (originalBitmap != null) {
                resetTransformations();
                imageView.setImageBitmap(originalBitmap);
                imageView.setVisibility(View.VISIBLE);
                setTransformButtonsEnabled(true);

                String fileName = new File(currentPhotoPath).getName();
                dbHelper.addToHistory("Фото", fileName, "JPG");
            } else {
                Toast.makeText(getActivity(), "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
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
        if (originalBitmap != null) {
            originalBitmap.recycle();
            originalBitmap = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(getActivity(),
                        "Для съемки фото необходимо разрешение на использование камеры",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}