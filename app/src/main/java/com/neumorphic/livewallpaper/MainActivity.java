package com.neumorphic.livewallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NeumorphicWallpaperPrefs";
    private static final String PREF_BACKGROUND_URI = "background_uri";

    private Button selectImageButton;
    private Button setWallpaperButton;
    private Button applyButton;
    private ImageView backgroundPreview;
    private TextView selectedImagePath;

    private SharedPreferences preferences;
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize preferences
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize views
        selectImageButton = findViewById(R.id.selectImageButton);
        setWallpaperButton = findViewById(R.id.setWallpaperButton);
        applyButton = findViewById(R.id.applyButton);
        backgroundPreview = findViewById(R.id.backgroundPreview);
        selectedImagePath = findViewById(R.id.selectedImagePath);

        // Set up image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        displaySelectedImage(uri);
                    }
                });

        // Set up button listeners
        selectImageButton.setOnClickListener(v -> openImagePicker());
        setWallpaperButton.setOnClickListener(v -> setAsWallpaper());
        applyButton.setOnClickListener(v -> applySettings());

        // Load saved settings
        loadSavedSettings();
    }

    private void openImagePicker() {
        // Launch image picker for any image type
        imagePickerLauncher.launch("image/*");
    }

    private void displaySelectedImage(Uri uri) {
        try {
            // Take persistent permission to access the URI
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Load and display the image
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            backgroundPreview.setImageBitmap(bitmap);

            // Update the text to show selected file
            selectedImagePath.setText(uri.getLastPathSegment());

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setAsWallpaper() {
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

    private void applySettings() {
        // Save the selected image URI to preferences
        SharedPreferences.Editor editor = preferences.edit();

        if (selectedImageUri != null) {
            editor.putString(PREF_BACKGROUND_URI, selectedImageUri.toString());
            Toast.makeText(this, "Settings applied! Background will update if wallpaper is active.",
                    Toast.LENGTH_LONG).show();
        } else {
            editor.remove(PREF_BACKGROUND_URI);
            Toast.makeText(this, "No background image selected", Toast.LENGTH_SHORT).show();
        }

        editor.apply();

        // Notify the wallpaper service to reload settings
        // The service will pick up changes next time it redraws
        sendBroadcast(new Intent("com.neumorphic.livewallpaper.SETTINGS_CHANGED"));
    }

    private void loadSavedSettings() {
        // Load the saved background image URI
        String uriString = preferences.getString(PREF_BACKGROUND_URI, null);

        if (uriString != null) {
            try {
                selectedImageUri = Uri.parse(uriString);
                displaySelectedImage(selectedImageUri);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading saved image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
