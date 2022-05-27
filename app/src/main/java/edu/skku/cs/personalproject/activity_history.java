package edu.skku.cs.personalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_history extends AppCompatActivity {
    String usr_name, file_dir;
    Button get_hist_btn;

    ListView history_listview;
    private history_listviewAdapter listViewAdapter;
    private ArrayList<history> histories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        usr_name = intent.getStringExtra("ID");
        file_dir = intent.getStringExtra("filedir");
        get_hist_btn = (Button) findViewById(R.id.get_history);
        history_listview = (ListView) findViewById(R.id.history_list);
        System.out.println("fileinactivyty : "+file_dir);

        get_hist_btn.setOnClickListener(view -> {

            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();
            JsonObject obj = new JsonObject();
            obj.addProperty("usr_name",usr_name);
            String json = gson.toJson(obj);
            HttpUrl.Builder urlBuilder =
                    HttpUrl.parse("https://tuh0odbmah.execute-api.ap-northeast-2.amazonaws.com/dev/download")
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
                    System.out.println(myResponse);
                    Gson gson = new Gson();
                    final history_gson pic_data = gson.fromJson(myResponse, history_gson.class);
                    //포문
                    histories = new ArrayList<history>();
                    for (int i = 0 ;i < pic_data.getLocation().length;i++){
                        histories.add(new history(pic_data.getLocation()[i],pic_data.getPic_name()[i]));
                    }



                    activity_history.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewAdapter = new history_listviewAdapter(getApplicationContext(),histories,file_dir);
                            history_listview.setAdapter(listViewAdapter);
                        }
                    });
                }
            });


        });
    }
}