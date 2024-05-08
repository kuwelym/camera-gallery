package com.example.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private AppCompatButton takePhotoButton;
    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoButton = findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.camera.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("MainActivity", "Storage dir: " + storageDir);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Add photo to custom gallery
            if (currentPhotoPath != null) {
                addToCustomGallery(currentPhotoPath);
            }
            Toast.makeText(this, "Photo taken and saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCustomGallery(String imagePath) {
        File source = new File(imagePath);
        File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CustomGallery", source.getName());
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        if (source.renameTo(destination)) {
            Toast.makeText(this, "Photo added to custom gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add photo to custom gallery", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}