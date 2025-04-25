package com.example.rpomp_l6;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.Prediction;
import android.widget.Toast;

import java.util.ArrayList;

public class GestureHelper {
    private static GestureLibrary gestureLibrary;
    private static final float MIN_CONFIDENCE = 1.0f;
    private static final String[] DEFAULT_GESTURES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "s", "question", "right", "left", "up"};
    public static void initialize(Context context) {
        if (gestureLibrary == null) {
            gestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gestures);
            if (!gestureLibrary.load()) {
                Toast.makeText(context, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String recognizeGesture(Gesture gesture) {
        if (gestureLibrary == null) return null;

        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
        if (predictions.size() > 0 && predictions.get(0).score > MIN_CONFIDENCE) {
            return predictions.get(0).name;
        }
        return null;
    }

    public static void addGesture(String name, Gesture gesture) {
        if (gestureLibrary != null) {
            gestureLibrary.addGesture(name, gesture);
            gestureLibrary.save();
        }
    }

    public static GestureLibrary getGestureLibrary() {
        return gestureLibrary;
    }

    public static boolean isDefaultGesture(String gestureName) {
        for (String defaultGesture : DEFAULT_GESTURES) {
            if (defaultGesture.equalsIgnoreCase(gestureName)) {
                return true;
            }
        }
        return false;
    }
}