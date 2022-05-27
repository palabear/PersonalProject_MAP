package edu.skku.cs.personalproject;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button login,register;
    EditText id, passwd;
    String ID_name , PASSWORD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login_btn);
        passwd = findViewById(R.id.Password);
        id = findViewById(R.id.ID);
        register = findViewById(R.id.register_btn);

        register.setOnClickListener(view -> {
            OkHttpClient client = new OkHttpClient();
            login_class register = new login_class();
            ID_name = id.getText().toString();
            PASSWORD = passwd.getText().toString();

            register.setName(ID_name);
            register.setPasswd(PASSWORD);
            Gson gson = new Gson();
            String json = gson.toJson(register,login_class.class);
            HttpUrl.Builder urlBuilder =
                    HttpUrl.parse("https://tuh0odbmah.execute-api.ap-northeast-2.amazonaws.com/dev/register")
                            .newBuilder();

            String url = urlBuilder.build().toString();


            Request req = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"),json))
                    .build();
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }


                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String myResponse = response.body().string().trim();


                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!myResponse.equals("{\"success\":true}")){
                                Toast.makeText(getApplicationContext(),"Register Failed!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Register Completed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        });

        login.setOnClickListener(view -> {
            OkHttpClient client = new OkHttpClient();
            login_class register = new login_class();
            ID_name = id.getText().toString();
            PASSWORD = passwd.getText().toString();

            register.setName(ID_name);
            register.setPasswd(PASSWORD);
            Gson gson = new Gson();
            String json = gson.toJson(register,login_class.class);
            HttpUrl.Builder urlBuilder =
                    HttpUrl.parse("https://tuh0odbmah.execute-api.ap-northeast-2.amazonaws.com/dev/access")
                            .newBuilder();

            String url = urlBuilder.build().toString();
            System.out.println("login json :" + json);


            Request req = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"),json))
                    .build();
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }


                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String myResponse = response.body().string().trim();


                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!myResponse.equals("{\"success\":true}")){
                                Toast.makeText(getApplicationContext(),"login Failed!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"login Completed!",Toast.LENGTH_SHORT).show();
                                login_activity(view,ID_name);
                            }
                        }
                    });
                }
            });


        });



    }


    public void login_activity(View v,String ID_name){
        Intent intent = new Intent(MainActivity.this,camera.class);
        intent.putExtra("ID",ID_name);
        startActivity(intent);
    }


}