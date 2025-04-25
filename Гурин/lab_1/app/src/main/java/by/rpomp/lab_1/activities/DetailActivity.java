package by.rpomp.lab_1.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;

import by.rpomp.lab_1.R;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        ShapeableImageView imageView = findViewById(R.id.title_image);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(DetailActivity.this, "Ошибка при загрузке изображения!", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);



        String photoId = getIntent().getStringExtra("PHOTO_ID");
        String sol = getIntent().getStringExtra("SOL");
        String cameraFullName = getIntent().getStringExtra("CAMERA_FULL_NAME");
        String text = "ID картинки: " + (photoId != null ? photoId : "") +
                "\nДень на марсе: " + (sol != null ? sol : "") +
                "\nСнято на марсоход: " + intent.getStringExtra("ROVER_NAME") +
                "\nСнято на камеру: " + (cameraFullName != null ? cameraFullName : "") +
                "\n\n\nИнформация о марсоходе:" +
                "\nДата посадки: " + intent.getStringExtra("ROVER_LANDING_DATE") +
                "\nДата вылета: " + intent.getStringExtra("ROVER_LAUNCH_DATE") +
                "\nСтатус: " + intent.getStringExtra("ROVER_STATUS");
        TextView textView = findViewById(R.id.textView);
        textView.setText(text);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
