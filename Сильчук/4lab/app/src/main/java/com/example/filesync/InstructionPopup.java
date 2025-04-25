package com.example.filesync;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class InstructionPopup {

    public static void showPopup(Context context, View anchorView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_instruction, null);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = (int) (metrics.widthPixels * 0.8); // 80% ширины экрана
        int height = (int) (metrics.heightPixels * 0.6); // 60% высоты экрана

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                width,
                height,
                true
        );

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        TextView textInstruction = popupView.findViewById(R.id.text_instruction);

        textInstruction.setText(Html.fromHtml(context.getString(R.string.instruction_text)));

        popupView.findViewById(R.id.button_ok).setOnClickListener(v -> popupWindow.dismiss());
    }
}