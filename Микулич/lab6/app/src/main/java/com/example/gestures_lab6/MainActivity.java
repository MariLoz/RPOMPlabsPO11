package com.example.gestures_lab6;

import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {

    private GestureLibrary gestureLibrary;
    private EditText textInput;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private ArrayList<Message> messagesList = new ArrayList<>();

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

        textInput = findViewById(R.id.textInput);
        GestureOverlayView gestureOverlay = findViewById(R.id.gestureOverlay);
        Button btnHelp = findViewById(R.id.btnHelp);

        messagesRecyclerView = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesAdapter(messagesList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            Log.e("Gestures", "Не удалось загрузить жесты");
            finish();
        }

        gestureOverlay.addOnGesturePerformedListener((overlay, gesture) -> {
            ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
            if (!predictions.isEmpty() && predictions.get(0).score > 1.0) {
                handleGesture(predictions.get(0).name);
            }
        });
        btnHelp.setOnClickListener(v -> showHelpDialog());

    }

    // Метод обработки распознанных жестов
    private void handleGesture(String gestureName) {
        String currentText = textInput.getText().toString();

        switch (gestureName) {
            case "backspace":
                if (!currentText.isEmpty()) {
                    textInput.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "enter":
                textInput.append("\n");
                break;
            case "save":
                saveMessage(currentText); // Сохраняем сообщение при жесте "save"
                textInput.setText(""); // Очищаем поле ввода после сохранения
                break;
            default:
                textInput.append(gestureName);
                break;
        }
    }

    // Метод для показа диалогового окна "Справка"
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Справка")
                .setMessage("Это приложение позволяет вводить текст с помощью жестов. " +
                        "Используйте нарисованные жесты для ввода букв." +
                        "жест 'backspace' для удаления символа (стрелка от правого верхнего, до левого нижнего угла), 'enter' для новой строки (как клавиша на клавиатуре), " +
                        "а 'save' для сохранения текста (стрелка от левого верхнего до правого нижнего угла).")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveMessage(String messageText) {
        // Получаем текущее время
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        // Создаем новое сообщение
        Message newMessage = new Message(currentTime, messageText);

        // Добавляем сообщение в список
        messagesList.add(newMessage);
        messagesAdapter.notifyItemInserted(messagesList.size() - 1); // Уведомляем адаптер о добавлении нового сообщения
    }


    private static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
        private final ArrayList<Message> messages;

        public MessagesAdapter(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.timeTextView.setText(message.getTime());
            holder.messageTextView.setText(message.getText());
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView timeTextView;
            TextView messageTextView;

            public MessageViewHolder(View itemView) {
                super(itemView);
                timeTextView = itemView.findViewById(android.R.id.text1);
                messageTextView = itemView.findViewById(android.R.id.text2);
            }
        }
    }
    

}