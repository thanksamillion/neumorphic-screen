package com.neumorphic.livewallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> applyWallpaper());
    }

    private void applyWallpaper() {
        try {
            // Create intent to set live wallpaper
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, NeumorphicWallpaperService.class));

            // Show message to user
            Toast.makeText(this, "Opening wallpaper preview...", Toast.LENGTH_SHORT).show();

            // Start the wallpaper picker with our wallpaper pre-selected
            startActivity(intent);

        } catch (Exception e) {
            // Fallback to general wallpaper chooser if the direct method fails
            try {
                Intent fallbackIntent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                Toast.makeText(this, "Please select Neumorphic Live Wallpaper", Toast.LENGTH_LONG).show();
                startActivity(fallbackIntent);
            } catch (Exception ex) {
                Toast.makeText(this, "Unable to set wallpaper: " + ex.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
