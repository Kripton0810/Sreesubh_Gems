package com.sreesubh.Gems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Attandance extends AppCompatActivity {
    Button present,absent;
    ImageView imageView;
    ProgressBar progressBar3;
    FusedLocationProviderClient locationProviderClient;
    FirebaseAuth auth;
    TextToSpeech speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandance);
        present = findViewById(R.id.att_present);
        absent = findViewById(R.id.att_absent);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar3.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        imageView = findViewById(R.id.compli);

        present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(Attandance.this)
                        .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                gevAttandance(true);

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(Attandance.this)
                        .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                gevAttandance(false);

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });
    }
    public void gevAttandance(boolean att)
    {
        present.setVisibility(View.GONE);
        absent.setVisibility(View.GONE);
        progressBar3.setVisibility(View.VISIBLE);
        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int lang = speech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        locationProviderClient =LocationServices.getFusedLocationProviderClient(Attandance.this);
        if((ActivityCompat.checkSelfPermission(Attandance.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)&&
                ActivityCompat.checkSelfPermission(Attandance.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            CancellationToken token = new CancellationToken() {
                @Override
                public boolean isCancellationRequested() {
                    Toast.makeText(Attandance.this, "Location is turned off!!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    Toast.makeText(Attandance.this, "Location is turned off!!", Toast.LENGTH_SHORT).show();
                    return null;
                }
            };
            locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task!=null)
                            {
                                Geocoder geocoder = new Geocoder(Attandance.this, Locale.getDefault());
                                Location location = task.getResult();
                                if(location!=null)
                                {
                                    try {
                                        List<Address> addresses =geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String add = addresses.get(0).getAddressLine(0);
                                        Log.d("Location",add);
                                        RequestQueue queue = Volley.newRequestQueue(Attandance.this);
                                        String url = "https://sreesubh.bookbin.tech/api/give_attandance.php";
                                        StringRequest objectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String rep) {
                                                try {
                                                    Toast.makeText(Attandance.this, "Given", Toast.LENGTH_SHORT).show();
                                                    JSONObject response = new JSONObject(rep);
                                                    if (!response.has("given")) {
                                                        present.setVisibility(View.VISIBLE);
                                                        absent.setVisibility(View.VISIBLE);
                                                        progressBar3.setVisibility(View.GONE);
                                                        try {
                                                            if (Integer.parseInt(response.getString("message")) < 0) {
                                                                String s = "Oh! no man your are late time try to be on time";
                                                                speech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                                                                Glide.with(Attandance.this)
                                                                        .load(R.drawable.upsetkrish)
                                                                        .into(imageView);
                                                            } else {
                                                                String s = "congratulations your are on time Keep it up";
                                                                speech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                                                                Glide.with(Attandance.this)
                                                                        .load(R.drawable.krishcongo)
                                                                        .into(imageView);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Toast.makeText(Attandance.this, "Attandance Registered Successful", Toast.LENGTH_SHORT).show();
//                                                        finish();

                                                    } else {
                                                        Toast.makeText(Attandance.this, "Attandance Given!!!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {

                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }){
                                            @Nullable
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String,String> map = new HashMap<>();
                                                map.put("Email",user.getEmail());
                                                map.put("Status", String.valueOf(att));
                                                map.put("Location",add);
                                                return map;
                                            }
                                        };
                                        queue.add(objectRequest);
                                    }catch (Exception e) {

                                    }




                                }



                            }
                            else {
                                Toast.makeText(Attandance.this, "Location error ::"+task.getException() , Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }
        else
        {
            Toast.makeText(Attandance.this, "Please Access location Permission", Toast.LENGTH_SHORT).show();
        }
    }
}