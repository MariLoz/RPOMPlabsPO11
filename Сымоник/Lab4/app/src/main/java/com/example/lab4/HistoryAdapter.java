package com.example.lab4;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> historyList;

    public HistoryAdapter(Context context, List<String> historyList) {
        super(context, R.layout.item_history, historyList);
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }

        String entry = historyList.get(position);

        TextView documentNameView = convertView.findViewById(R.id.documentName);
        ImageView thumbnailView = convertView.findViewById(R.id.thumbnail);

        documentNameView.setText(entry);


        String filePath = entry;
        Uri file = findFileUriByName(filePath);
        if (filePath != null) {
            Bitmap thumbnail = loadPdfThumbnail(file);
            if (thumbnail != null) {
                thumbnailView.setImageBitmap(thumbnail);
            } else {
                thumbnailView.setImageResource(R.drawable.ic_pdf_placeholder);
            }
        }

        return convertView;
    }
    public Uri findFileUriByName(String fileName) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }

        String[] projection = {MediaStore.Downloads._ID};
        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        // Выполняем запрос
        try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        } catch (Exception e) {
            Log.e("findFileUriByName", "Ошибка при поиске файла: " + e.getMessage());
        }

        return null; // Файл не найден
    }

    private Bitmap loadPdfThumbnail(Uri fileUri) {

        ContentResolver resolver = getContext().getContentResolver();

        try (ParcelFileDescriptor fileDescriptor = resolver.openFileDescriptor(fileUri, "r")) {
            if (fileDescriptor == null) {
                Log.e("loadPdfThumbnail", "Не удалось открыть файл по Uri: " + fileUri);
                return null;
            }

            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0); // Открываем первую страницу

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            page.close();
            pdfRenderer.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.e("loadPdfThumbnail", "Файл не найден: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("loadPdfThumbnail", "Ошибка загрузки миниатюры PDF: " + e.getMessage());
            return null;
        } catch (SecurityException e) {
            Log.e("loadPdfThumbnail", "Нет прав доступа к файлу: " + e.getMessage());
            return null;
        }
    }

}