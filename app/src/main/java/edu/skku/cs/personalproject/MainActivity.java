package edu.skku.cs.personalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    Button login;
    EditText id, passwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login_btn);
        passwd = findViewById(R.id.Password);
        id = findViewById(R.id.ID);

        login.setOnClickListener(view -> {


            login_activity(view);
        });



    }


    public void login_activity(View v){
        Intent intent = new Intent(MainActivity.this,camera.class);
        startActivity(intent);
    }


}