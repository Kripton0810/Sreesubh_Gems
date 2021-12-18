package com.sreesubh.Gems;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        GoogleSignInOptions googleSignInOptions =new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("1000880822314-5u63cg3e69seovef7ua5tbptnlk2qdob.apps.googleusercontent.com")
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            Log.d("USER", user.getEmail());
            String url = "https://sreesubh.bookbin.tech/api/login.php?Email=" + user.getEmail();
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getBoolean("message"))
                        {
                            startActivity(new Intent(MainActivity.this, MainDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                        else
                        {
                            googleSignInClient.signOut();
                            firebaseAuth.signOut();
                            startActivity(new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    startActivity(new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Toast.makeText(MainActivity.this, "System Error", Toast.LENGTH_SHORT).show();
                    googleSignInClient.signOut();
                    firebaseAuth.signOut();
                }
            });

            queue.add(objectRequest);
        }else {
            SystemClock.sleep(1000);
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }
    }
}