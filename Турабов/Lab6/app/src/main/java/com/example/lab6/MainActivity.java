package com.example.lab6;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLibrary = null;

    private TextView sequence;
    GestureOverlayView gestures;

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
        sequence = findViewById(R.id.sequence);

        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);

        Button backspace = findViewById(R.id.backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = sequence.getText().toString();
                if(!tmp.isEmpty())
                {
                    String newStr = tmp.substring(0, tmp.length()-1);
                    sequence.setText(newStr);
                }
            }

        });

        Button result = findViewById(R.id.result_button);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = sequence.getText().toString();
                if(tmp.isEmpty())
                    return;

                try {
                Expression expr = new ExpressionBuilder(tmp).build();
                    double res = expr.evaluate();
                    sequence.setText(/*tmp + "=" + **/String.valueOf(res));
                } catch (IllegalArgumentException e) {
                    // If an exception is thrown, the expression is invalid
                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gestures = (GestureOverlayView) findViewById(R.id.gestureView);
        gestures.addOnGesturePerformedListener(this);
        gestures.setGestureStrokeAngleThreshold(65);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.devby)
        {
            Toast.makeText(this, "Developed by Andrey Turabov", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, GestureSaveActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, LabInfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Загружаем библиотеку жестов (если есть)
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        if(gestureLibrary == null)
            return;

        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);

            if (prediction.score > 1.0) {
                if (prediction.name.equals("one"))
                    sequence.append("1");
                else if (prediction.name.equals("two"))
                    sequence.append("2");
                else if (prediction.name.equals("three"))
                    sequence.append("3");
                else if (prediction.name.equals("four"))
                    sequence.append("4");
                else if (prediction.name.equals("five"))
                    sequence.append("5");
                else if (prediction.name.equals("six"))
                    sequence.append("6");
                else if (prediction.name.equals("seven"))
                    sequence.append("7");
                else if (prediction.name.equals("eight"))
                    sequence.append("8");
                else if (prediction.name.equals("nine"))
                    sequence.append("9");
                else if (prediction.name.equals("zero"))
                    sequence.append("0");
                else if (prediction.name.equals("plus"))
                    sequence.append("+");
                else if (prediction.name.equals("minus"))
                    sequence.append("-");
                else if (prediction.name.equals("multiply"))
                    sequence.append("*");
                else if (prediction.name.equals("divide"))
                    sequence.append("/");
                }else{
                    Toast.makeText(this, "Жест неизвестен",Toast.LENGTH_LONG);
                }
            }

        }
}
