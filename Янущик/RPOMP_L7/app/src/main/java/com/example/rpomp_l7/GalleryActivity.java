package com.example.rpomp_l7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_PERMISSION_CODE = 100;

    private ViewPager viewPager;
    private Button btnBack, btnPrev, btnNext, btnSelectImages;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSelectImages = findViewById(R.id.btnSelectImages);

        checkPermissions();

        btnSelectImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGES_REQUEST);
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPosition > 0) {
                viewPager.setCurrentItem(currentPosition - 1);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPosition < imagePaths.size() - 1) {
                viewPager.setCurrentItem(currentPosition + 1);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            imagePaths.clear();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imagePaths.add(getRealPathFromURI(imageUri));
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imagePaths.add(getRealPathFromURI(imageUri));
            }

            if (!imagePaths.isEmpty()) {
                ImagePagerAdapter adapter = new ImagePagerAdapter();
                viewPager.setAdapter(adapter);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(GalleryActivity.this);
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position));
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                Matrix matrix = new Matrix();
                imageView.setScaleType(ImageView.ScaleType.MATRIX);

                imageView.setOnTouchListener(new View.OnTouchListener() {
                    float scaleFactor = 1f;
                    float lastX, lastY;
                    float startX, startY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                startX = event.getX();
                                startY = event.getY();
                                lastX = event.getX();
                                lastY = event.getY();
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                scaleFactor = ((ImageView) v).getScaleX();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (event.getPointerCount() == 1) {
                                    float dx = event.getX() - lastX;
                                    float dy = event.getY() - lastY;
                                    matrix.postTranslate(dx, dy);
                                    lastX = event.getX();
                                    lastY = event.getY();
                                } else if (event.getPointerCount() == 2) {
                                    float newDist = spacing(event);
                                    if (newDist > 10f) {
                                        float scale = newDist / spacing(event);
                                        scaleFactor *= scale;
                                        matrix.postScale(scaleFactor, scaleFactor, startX, startY);
                                    }
                                }
                                ((ImageView) v).setImageMatrix(matrix);
                                break;
                        }
                        return true;
                    }

                    private float spacing(MotionEvent event) {
                        float x = event.getX(0) - event.getX(1);
                        float y = event.getY(0) - event.getY(1);
                        return (float) Math.sqrt(x * x + y * y);
                    }
                });

                container.addView(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }
}