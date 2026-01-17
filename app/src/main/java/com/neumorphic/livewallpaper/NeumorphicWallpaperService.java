package com.neumorphic.livewallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class NeumorphicWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new NeumorphicEngine();
    }

    private class NeumorphicEngine extends Engine {
        private Paint paint;
        private int backgroundColor = Color.parseColor("#E0E5EC");
        private boolean visible = true;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
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

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    int width = canvas.getWidth();
                    int height = canvas.getHeight();

                    // Draw neumorphic background
                    canvas.drawColor(backgroundColor);

                    // Draw soft shadow circle (bottom-right)
                    paint.setShader(new RadialGradient(
                            width / 2f + 50, height / 2f + 50, 300,
                            Color.parseColor("#A8B0C0"),
                            backgroundColor,
                            Shader.TileMode.CLAMP
                    ));
                    canvas.drawCircle(width / 2f, height / 2f, 300, paint);

                    // Draw light circle (top-left)
                    paint.setShader(new RadialGradient(
                            width / 2f - 50, height / 2f - 50, 300,
                            Color.parseColor("#FFFFFF"),
                            backgroundColor,
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
