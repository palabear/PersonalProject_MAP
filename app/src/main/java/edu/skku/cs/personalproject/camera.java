package edu.skku.cs.personalproject;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class camera extends AppCompatActivity implements LocationListener{
    Button btnCamera, btnLocation, btnHistory, btnadd;
    ImageView imageView;
    TextView text,id;
    Uri photoUri;
    String ID_name, file_dir,temp_file_dir,img_name;
    //Arraylist<history> hist = new ArrayList<history>();
    Intent intent;
    String loc_address = null;
    LocationManager locationManager;
    File photoFile = null;
    private static final String accessKey = BuildConfig.ACCESSKEY;
    private static final String secretKey = BuildConfig.SECRETKEY;
    private static final String bucketName = "zappa-7lelfnbhz" ;


    private static final int GPS_TIME_INTERVAL = 1000 * 60 * 5; // get gps location every 1 min
    private static final int GPS_DISTANCE = 1000; // set the distance value in meter
    private static final int HANDLER_DELAY = 1000 * 60 * 5;
    private static final int START_HANDLER_DELAY = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        intent = getIntent();
        ID_name = intent.getStringExtra("ID");
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("카메라 권한을 거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        btnCamera = (Button) findViewById(R.id.camera_btn);
        btnLocation = (Button) findViewById(R.id.loc);
        btnHistory = (Button) findViewById(R.id.history);
        btnadd = (Button) findViewById(R.id.add);

        imageView = (ImageView) findViewById(R.id.imageView);
        text = (TextView) findViewById(R.id.address_show);
        id = (TextView) findViewById(R.id.ID_show);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        System.out.println("user:[" + ID_name +"]");
        id.setText("Hi! " + ID_name +" record your moment!");
        try { File init_temp  = createImageFile(); } catch (IOException ex) { }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try { photoFile = createImageFile(); } catch (IOException ex) { }
                if(photoFile != null) {
                    photoUri = FileProvider.getUriForFile(camera.this, getPackageName() + ".fileprovider", photoFile);
                //
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);//?
                activityResultPicture.launch(intent);
            }

        });


        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(photoFile != null) {
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            requestLocation();
                            handler.postDelayed(this, HANDLER_DELAY);
                        }
                    }, START_HANDLER_DELAY);
                    ////////////////////////////////////////

                    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
                    AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                    TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(camera.this).build();
                    TransferNetworkLossHandler.getInstance(camera.this);
                    TransferObserver uploadObserver = transferUtility.upload(bucketName, file_dir, photoFile);
                    uploadObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());

                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                            int percentDone = (int)percentDonef;
                            Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    });
                    ////////////////////////////////////////////////////////////////////

                }
                else
                {
                    Toast.makeText(camera.this,"Take Picture First!",Toast.LENGTH_SHORT).show();
                }

            }
                ////////////////////////////////////////


        });

        btnadd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(photoFile != null && loc_address != null){
                    OkHttpClient client = new OkHttpClient();
                    pic_info pic = new pic_info();

                    pic.setFile_path(temp_file_dir);
                    pic.setPic_name(img_name);
                    pic.setLocation(loc_address);
                    pic.setUsr_name(ID_name);

                    Gson gson = new Gson();
                    String json = gson.toJson(pic,pic_info.class);
                    HttpUrl.Builder urlBuilder =
                            HttpUrl.parse("https://tuh0odbmah.execute-api.ap-northeast-2.amazonaws.com/dev/upload")
                                    .newBuilder();

                    String url = urlBuilder.build().toString();

                    System.out.println(url);

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


                            camera.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!myResponse.equals("{\"success\":true}")){
                                        Toast.makeText(getApplicationContext(),"Upload Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Upload Completed!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else{
                    Toast.makeText(camera.this,"Nothing to Add!",Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                hist_activity(view,ID_name,temp_file_dir);

            }


        });



    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.d("mylog", "Got Location: " + location.getLatitude() + ", " + location.getLongitude());
        //Toast.makeText(camera.this, "Got Coordinates: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        List<Address> address = null;
        Geocoder g = new Geocoder(this);
        try {
            address = g.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            loc_address = address.get(0).getAddressLine(0);
            text.setText(loc_address);
            locationManager.removeUpdates(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //text.setText(String.format("latitude : %f \n longitude : %f",location.getLatitude(),location.getLongitude()));

    }

    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        GPS_TIME_INTERVAL, GPS_DISTANCE, this);
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){

                        imageView.setImageURI(photoUri);

                    }
                }
            });

    private final PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        img_name = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                img_name,
                ".jpg",
                storageDir
        );
        StringBuilder temp = new StringBuilder(storageDir.toString());
        temp_file_dir = temp.deleteCharAt(0) + "/" + ID_name +"/";
        file_dir = temp_file_dir+img_name;



        return image;
    }

    public void hist_activity(View v,String ID_name,String filedir){
        Intent intent = new Intent(camera.this,activity_history.class);
        intent.putExtra("ID",ID_name);
        intent.putExtra("filedir",filedir);
        startActivity(intent);
    }




}

