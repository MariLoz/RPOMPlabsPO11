package by.rpomp.lab_7;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private final int PICK_FILE_REQUEST = 1;
    private Button cameraButton, chooseFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cameraButton = findViewById(R.id.camera);
        chooseFileButton = findViewById(R.id.chooseFileButton);

        cameraButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });
        chooseFileButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != PICK_FILE_REQUEST || resultCode != RESULT_OK || data == null) {
            return;
        }

        Uri fileUri = data.getData();
        String mimeType = getContentResolver().getType(fileUri);

        if (mimeType == null) {
            Toast.makeText(this, "Failed to determine the file type", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        switch (mimeType) {
            case "image/jpeg":
            case "image/jpg":
            case "image/png":
                intent = new Intent(this, ImageActivity.class);
                break;
            case "audio/mpeg":
            case "audio/mp3":
                intent = new Intent(this, AudioActivity.class);
                break;
            case "video/mp4": // Для видео
                intent = new Intent(this, VideoActivity.class);
                break;
            default:
                Toast.makeText(this, "File type is not supported", Toast.LENGTH_SHORT).show();
                return;
        }
        intent.setData(fileUri);
        startActivity(intent);
    }
}