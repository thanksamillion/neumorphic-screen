package com.neumorphic.livewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;

public class NeumorphicView extends View {
    private Paint paint;
    private Bitmap backgroundImage;
    private float touchX = -1;
    private float touchY = -1;
    private boolean isPressed = false;

    public NeumorphicView(Context context) {
        super(context);
        init();
    }

    public NeumorphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NeumorphicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setBackgroundImage(Uri imageUri) {
        if (imageUri != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                if (backgroundImage != null) {
                    backgroundImage.recycle();
                }
                backgroundImage = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearBackground() {
        if (backgroundImage != null) {
            backgroundImage.recycle();
            backgroundImage = null;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        if (backgroundImage != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
            canvas.drawBitmap(scaledBitmap, 0, 0, paint);
            if (scaledBitmap != backgroundImage) {
                scaledBitmap.recycle();
            }
        } else {
            canvas.drawColor(Color.parseColor("#E0E5EC"));
        }

        // Draw neumorphic effects
        if (touchX >= 0 && touchY >= 0) {
            // Draw shadow circle (darker)
            float shadowX = touchX + 20;
            float shadowY = touchY + 20;
            RadialGradient shadowGradient = new RadialGradient(
                shadowX, shadowY, 300,
                Color.parseColor("#A8B0C0"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            );
            paint.setShader(shadowGradient);
            canvas.drawCircle(shadowX, shadowY, 300, paint);

            // Draw light circle (brighter)
            float lightX = touchX - 20;
            float lightY = touchY - 20;
            RadialGradient lightGradient = new RadialGradient(
                lightX, lightY, 300,
                Color.parseColor("#66FFFFFF"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            );
            paint.setShader(lightGradient);
            canvas.drawCircle(lightX, lightY, 300, paint);
        } else {
            // Default centered neumorphic effect
            float centerX = width / 2f;
            float centerY = height / 2f;

            // Draw shadow circle (darker) - bottom right
            float shadowX = centerX + 100;
            float shadowY = centerY + 100;
            RadialGradient shadowGradient = new RadialGradient(
                shadowX, shadowY, 400,
                Color.parseColor("#A8B0C0"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            );
            paint.setShader(shadowGradient);
            canvas.drawCircle(shadowX, shadowY, 400, paint);

            // Draw light circle (brighter) - top left
            float lightX = centerX - 100;
            float lightY = centerY - 100;
            RadialGradient lightGradient = new RadialGradient(
                lightX, lightY, 400,
                Color.parseColor("#66FFFFFF"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            );
            paint.setShader(lightGradient);
            canvas.drawCircle(lightX, lightY, 400, paint);
        }

        paint.setShader(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                touchY = event.getY();
                isPressed = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchX = -1;
                touchY = -1;
                isPressed = false;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (backgroundImage != null) {
            backgroundImage.recycle();
            backgroundImage = null;
        }
    }
}
