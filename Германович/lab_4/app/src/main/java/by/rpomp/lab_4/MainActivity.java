package by.rpomp.lab_4;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.rpomp.lab_4.tasks.DownloadFileTask;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUrl;
    private Button downloadButton, viewButton, deleteButton;
    private ProgressBar progressBar;
    private static String PREFS_NAME = "AppPrefs";
    private static String KEY_DONT_SHOW_AGAIN = "dont_show_again";
    private PopupWindow popupWindow;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextUrl = findViewById(R.id.editTextUrl);
        downloadButton = findViewById(R.id.downloadButton);
        viewButton = findViewById(R.id.viewButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);

        downloadButton.setOnClickListener(view -> onClickDownloadButton());
        viewButton.setOnClickListener(view -> onClickViewButton());
        deleteButton.setOnClickListener(view -> onClickDeleteButton());

        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean dontShowAgain = preferences.getBoolean(KEY_DONT_SHOW_AGAIN, false); // По умолчанию показывать

        if (!dontShowAgain) {
            getWindow().getDecorView().post(this::showFullScreenPopup);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onClickDeleteButton() {
        String fileUrl = editTextUrl.getText().toString().trim();
        String fileName = "";
        String filePath = "";
        String regex = "\\.([^\\.]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileUrl);
        if (fileUrl.contains(".pdf")) {
            fileName = getFilenameFromUrl(fileUrl);
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;
        } else {
            if (matcher.find()) {
                fileName = matcher.group(1);
                System.out.println(fileName);
            } else {
                fileName = getFilenameFromUrl(fileUrl);
            }
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName + ".pdf";
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "File not exists", LENGTH_SHORT).show();
            return;
        }

        if (file.delete()) {
            Toast.makeText(MainActivity.this, "File deleted successfully", LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Filed to delete the file", LENGTH_SHORT).show();
        }
    }

    private void onClickViewButton() {
        String fileUrl = editTextUrl.getText().toString().trim();

        String fileName = "";
        String filePath = "";
        String regex = "\\.([^\\.]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileUrl);
        if (fileUrl.contains(".pdf")) {
            fileName = getFilenameFromUrl(fileUrl);
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;
        } else {
            if (matcher.find()) {
                fileName = matcher.group(1);
                System.out.println(fileName);
            } else {
                fileName = getFilenameFromUrl(fileUrl);
            }
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName + ".pdf";
        }
        System.out.println(fileName);
        System.out.println(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "Невозможно открыть файл", LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, "your.package.name.provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent chooser = Intent.createChooser(intent, "Open PDF with");
        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickDownloadButton() {
        String fileUrl = editTextUrl.getText().toString().trim();
        if (!fileUrl.isEmpty()) {
            startPdfDownloadWorker(fileUrl);
        }
    }

    private void startPdfDownloadWorker(String fileUrl) {
        Data inputData = new Data.Builder()
                .putString("fileUrl", fileUrl)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(Constraints.NONE.getRequiredNetworkType())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadFileTask.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();
        progressBar.setVisibility(View.VISIBLE);
        WorkManager.getInstance(this).enqueueUniqueWork(
                "pdf_download",
                ExistingWorkPolicy.REPLACE,
                workRequest
        );
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        if (workInfo.getState().isFinished()) {
                            progressBar.setVisibility(View.GONE);
                        } else {
                            int progress = workInfo.getProgress().getInt("progress", 0);
                            progressBar.setProgress(progress);
                        }
                    }
                });
    }

    public String getFilenameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            return Paths.get(path).getFileName().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showFullScreenPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        popupWindow = new PopupWindow(
                popupView,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                true
        );
        popupWindow.showAtLocation(findViewById(R.id.main), 0, 0, 0);

        Button okButton = popupView.findViewById(R.id.okButton);
        CheckBox checkBox = popupView.findViewById(R.id.dontShowAgain);

        okButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DONT_SHOW_AGAIN, checkBox.isChecked());
            editor.apply();
            popupWindow.dismiss();
        });

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            popupWindow.showAtLocation(mainView, Gravity.CENTER, 0, 0);
        } else {
            Log.e("PopupError", "View not found");
        }
    }
}