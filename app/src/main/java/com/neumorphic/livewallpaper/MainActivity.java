package com.neumorphic.livewallpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NeumorphicView neumorphicView;
    private Button selectImageButton;
    private Button clearBackgroundButton;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        neumorphicView = findViewById(R.id.neumorphicView);
        selectImageButton = findViewById(R.id.selectImageButton);
        clearBackgroundButton = findViewById(R.id.clearBackgroundButton);

        // Set up image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        setBackgroundImage(uri);
                    }
                });

        // Set up button listeners
        selectImageButton.setOnClickListener(v -> openImagePicker());
        clearBackgroundButton.setOnClickListener(v -> clearBackground());
    }

    private void openImagePicker() {
        // Launch image picker for any image type
        imagePickerLauncher.launch("image/*");
    }

    private void setBackgroundImage(Uri uri) {
        try {
            // Take persistent permission to access the URI
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Set the background image in the neumorphic view
            neumorphicView.setBackgroundImage(uri);

            Toast.makeText(this, "Background image set! Touch and drag to play.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void clearBackground() {
        neumorphicView.clearBackground();
        Toast.makeText(this, "Background cleared", Toast.LENGTH_SHORT).show();
    }
}
