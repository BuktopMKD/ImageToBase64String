package com.denofdevelopers.android.ImageToBase64String;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.overlayText)
    TextView overlayTest;
    @BindView(R.id.base64Text)
    TextView base64Text;
    @BindView(R.id.base64TextOverlay)
    TextView base64TextOverlay;

    private static final int GET_PICTURE = 1111;
    private static final int GALLERY_REQUEST_PERMISSION_CODE = 1112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void onGoToCameraClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestWriteExternalStoragePermission();
        } else {
            startGalleryIntent();
        }
    }

    private void startGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestWriteExternalStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY_REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryIntent();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PICTURE && resultCode == Activity.RESULT_OK) {

            Uri imageUri = Objects.requireNonNull(data).getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                image.setBackground(getDrawable(R.color.white));
                image.setImageBitmap(bitmap);
                byte[] imageBytes = imageToByteArray(bitmap);
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                base64Text.setText(encodedImage);
            }
            overlayTest.setVisibility(View.INVISIBLE);
            base64TextOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private byte[] imageToByteArray(Bitmap bitmapImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, baos); //bm is the bitmap object
        return baos.toByteArray();
    }
}
