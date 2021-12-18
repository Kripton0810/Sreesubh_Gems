package com.sreesubh.Gems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Logout extends AppCompatActivity {
    Button view_logout;
    FirebaseAuth auth;
    FusedLocationProviderClient locationProviderClient;
    ImageView imageView;
    ProgressBar progressBar2;
    TextToSpeech speech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        view_logout = findViewById(R.id.view_logout);
        imageView = findViewById(R.id.imageView2);
        progressBar2 = findViewById(R.id.progressBar2);
        checkLogout();
        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int lang = speech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        view_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }


    private void logout() {
        progressBar2.setVisibility(View.VISIBLE);
        view_logout.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        locationProviderClient =LocationServices.getFusedLocationProviderClient(Logout.this);
        if((ActivityCompat.checkSelfPermission(Logout.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)&&
                ActivityCompat.checkSelfPermission(Logout.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task!=null)
                            {
                                Geocoder geocoder = new Geocoder(Logout.this, Locale.getDefault());
                                Location location = task.getResult();
                                if(location!=null)
                                {
                                    try {
                                        List<Address> addresses =geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String add = addresses.get(0).getAddressLine(0);
                                        Log.d("Location",add);
                                            try {
                                                String s = "Thank you! "+user.getDisplayName()+" For the day see you tomorrow";
                                                speech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                                                    Glide.with(Logout.this)
                                                            .load(R.drawable.thankyouoffice)
                                                            .into(imageView);
                                                RequestQueue queue = Volley.newRequestQueue(Logout.this);
                                                String url = "https://sreesubh.bookbin.tech/api/logout.php";
                                                StringRequest request = new StringRequest(Request.Method.POST, url,
                                                        new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            if(object.getBoolean("message"))
                                                            {
                                                                progressBar2.setVisibility(View.GONE);
                                                                Toast.makeText(Logout.this, "Logout Successful", Toast.LENGTH_SHORT).show();
//
//                                                                startActivity(new Intent(Logout.this,MainDashboard.class));
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(Logout.this, "Logout Registered Unsuccessful Server Error", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Logout.this,MainDashboard.class));
                                                        finish();
                                                    }
                                                }){
                                                    @Nullable
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String,String> map = new HashMap<>();
                                                        map.put("Email",user.getEmail());
                                                        map.put("Location",add);
                                                        return map;
                                                    }
                                                };
                                                queue.add(request);
                                            } catch (Exception e) {
                                                Log.d("======= Hours",e.getMessage());
                                                e.printStackTrace();
                                            }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else {
                                Toast.makeText(Logout.this, "Location error ::"+task.getException() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(Logout.this, "Please Access location Permission", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkLogout() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        try {
            RequestQueue queue = Volley.newRequestQueue(Logout.this);
            String url = "https://sreesubh.bookbin.tech/api/logout_check.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.has("no_in"))
                        {
                            Toast.makeText(Logout.this, "Please Login first", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Logout.this, Attandance.class));
                            finish();

                        }
                        else {
                            if (object.getBoolean("message")) {
                                Toast.makeText(Logout.this, "Already Logout", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Logout.this, MainDashboard.class));
                                finish();
                            } else {
                                view_logout.setEnabled(true);
                            }
                        }
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } 
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Logout.this, "Server error!!", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Email", user.getEmail());
                    return map;
                }
            };
            queue.add(request);
        } catch (Exception e) {
            Log.d("======= Hours", e.getMessage());
            e.printStackTrace();
        }
    }
}