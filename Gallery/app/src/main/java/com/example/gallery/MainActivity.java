package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private Button getPictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);

        getPictureButton = findViewById(R.id.get_picture_button);
        getPictureButton.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityIfNeeded(intent, 101);
                    } catch (Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                    }
                }
                else {
                    loadImages();
                }
            }
            else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    loadImages();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                }
            }
        });
    }

    private void loadImages() {
        ArrayList<String> imagePaths = getCustomGalleryImages();
        imageAdapter.setImagePaths(imagePaths);

    }

    private ArrayList<String> getCustomGalleryImages() {
        ArrayList<String> imagePaths = new ArrayList<>();
        File customGalleryDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomGallery");
        if (customGalleryDir.exists()) {
            File[] files = customGalleryDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getAbsolutePath())) {
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return imagePaths;
    }

    private boolean isImageFile(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImages();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
