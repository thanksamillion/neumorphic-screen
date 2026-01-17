package com.neumorphic.livewallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.InputStream;

public class NeumorphicWallpaperService extends WallpaperService {

    private static final String PREFS_NAME = "NeumorphicWallpaperPrefs";
    private static final String PREF_BACKGROUND_URI = "background_uri";

    @Override
    public Engine onCreateEngine() {
        return new NeumorphicEngine();
    }

    private class NeumorphicEngine extends Engine {
        private Paint paint;
        private int backgroundColor = Color.parseColor("#E0E5EC");
        private boolean visible = true;
        private Bitmap backgroundBitmap;
        private SharedPreferences preferences;
        private BroadcastReceiver settingsReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint = new Paint();
            paint.setAntiAlias(true);

            // Initialize preferences
            preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            // Load background image
            loadBackgroundImage();

            // Register broadcast receiver for settings changes
            settingsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Reload settings when changed
                    loadBackgroundImage();
                    if (visible) {
                        drawFrame();
                    }
                }
            };

            IntentFilter filter = new IntentFilter("com.neumorphic.livewallpaper.SETTINGS_CHANGED");
            registerReceiver(settingsReceiver, filter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // Unregister broadcast receiver
            if (settingsReceiver != null) {
                unregisterReceiver(settingsReceiver);
            }
            // Clean up bitmap
            if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
                backgroundBitmap.recycle();
                backgroundBitmap = null;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                loadBackgroundImage();
                drawFrame();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
        }

        private void loadBackgroundImage() {
            // Clean up old bitmap
            if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
                backgroundBitmap.recycle();
                backgroundBitmap = null;
            }

            // Load the background image URI from preferences
            String uriString = preferences.getString(PREF_BACKGROUND_URI, null);

            if (uriString != null) {
                try {
                    Uri uri = Uri.parse(uriString);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        backgroundBitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    backgroundBitmap = null;
                }
            }
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    int width = canvas.getWidth();
                    int height = canvas.getHeight();

                    // Draw background - either image or solid color
                    if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
                        // Scale and draw the background image
                        canvas.drawBitmap(backgroundBitmap, null,
                                new android.graphics.Rect(0, 0, width, height), paint);
                    } else {
                        // Draw default neumorphic background
                        canvas.drawColor(backgroundColor);
                    }

                    // Draw neumorphic effects on top
                    // Draw soft shadow circle (bottom-right)
                    paint.setShader(new RadialGradient(
                            width / 2f + 50, height / 2f + 50, 300,
                            Color.parseColor("#A8B0C0"),
                            Color.TRANSPARENT,
                            Shader.TileMode.CLAMP
                    ));
                    canvas.drawCircle(width / 2f, height / 2f, 300, paint);

                    // Draw light circle (top-left)
                    paint.setShader(new RadialGradient(
                            width / 2f - 50, height / 2f - 50, 300,
                            Color.parseColor("#66FFFFFF"),
                            Color.TRANSPARENT,
                            Shader.TileMode.CLAMP
                    ));
                    canvas.drawCircle(width / 2f, height / 2f, 300, paint);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
