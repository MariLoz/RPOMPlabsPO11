package com.example.lab6;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiView extends View {
    private List<Confetti> confettiList = new ArrayList<>();
    private Paint paint = new Paint();
    private Random random = new Random();
    private boolean isRunning = false;
    private long lastUpdateTime;

    public ConfettiView(Context context) {
        super(context);
        init();
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
    }

    public void startConfetti() {
        if (isRunning) return;

        isRunning = true;
        lastUpdateTime = System.currentTimeMillis();
        confettiList.clear();

        for (int i = 0; i < 100; i++) {
            confettiList.add(new Confetti(getWidth(), getHeight()));
        }

        invalidate();
    }

    public void stopConfetti() {
        isRunning = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isRunning) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000f;
        lastUpdateTime = currentTime;

        for (int i = 0; i < confettiList.size(); i++) {
            Confetti confetti = confettiList.get(i);
            paint.setColor(confetti.color);
            canvas.drawRect(
                    confetti.x,
                    confetti.y,
                    confetti.x + confetti.size,
                    confetti.y + confetti.size,
                    paint
            );

            confetti.update(deltaTime);

            if (confetti.y > getHeight()) {
                confettiList.set(i, new Confetti(getWidth(), getHeight()));
            }
        }

        invalidate();
    }

    private class Confetti {
        float x, y, speed, rotation, rotationSpeed, size;
        int color;

        Confetti(int maxX, int maxY) {
            x = random.nextInt(maxX);
            y = -random.nextInt(maxY * 2);
            speed = 100 + random.nextFloat() * 300;
            rotation = random.nextFloat() * 360;
            rotationSpeed = (random.nextFloat() - 0.5f) * 180;
            size = 5 + random.nextFloat() * 15;
            color = Color.HSVToColor(new float[]{
                    random.nextFloat() * 360,
                    0.8f,
                    0.9f
            });
        }

        void update(float deltaTime) {
            y += speed * deltaTime;
            x += (random.nextFloat() - 0.5f) * 50 * deltaTime;
            rotation += rotationSpeed * deltaTime;
        }
    }
}