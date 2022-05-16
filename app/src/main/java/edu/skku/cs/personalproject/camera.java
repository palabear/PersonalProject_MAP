package edu.skku.cs.personalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class camera extends AppCompatActivity {
    Button btnCamera;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btnCamera = (Button) findViewById(R.id.camera_btn);
        imageView = (ImageView) findViewById(R.id.imageView);
        //btnCamera.setOnClickListener(this);


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(takePictureIntent);
        }
    }




}

