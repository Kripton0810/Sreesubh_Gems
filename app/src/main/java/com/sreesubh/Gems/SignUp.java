package com.sreesubh.Gems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText user_name,user_email,user_phone,user_bloodg;
    DatePicker user_dob;
    RadioGroup group;
    GoogleSignInClient googleSignInClient;
    ImageView user_img;
    Button user_submit;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        user_phone = findViewById(R.id.user_phone);
        user_bloodg = findViewById(R.id.user_bloodg);
        user_dob = findViewById(R.id.user_dob);
        group = findViewById(R.id.group);
        user_submit= findViewById(R.id.user_submit);
        progressBar= findViewById(R.id.progressBar);
        user_img = findViewById(R.id.user_img);
        GoogleSignInOptions googleSignInOptions =new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("1000880822314-5u63cg3e69seovef7ua5tbptnlk2qdob.apps.googleusercontent.com")
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        if(user!=null)
        {
            user_name.setText(user.getDisplayName());
            user_email.setText(user.getEmail());
            user_phone.setText(user.getPhoneNumber());
            Glide.with(SignUp.this)
                    .load(user.getPhotoUrl())
                    .into(user_img);
        }
        user_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD");
                Date d = new Date();
                String doj = simpleDateFormat.format(d.getTime());
                progressBar.setVisibility(View.VISIBLE);
                user_submit.setVisibility(View.GONE);
                RequestQueue queue = Volley.newRequestQueue(SignUp.this);
                String url = "https://bookbin.tech/sreesubh/signup.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DATA",response);
                        if(response.equals("200"))
                        {
                            startActivity(new Intent(SignUp.this,MainDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            progressBar.setVisibility(View.GONE);
                            user_submit.setVisibility(View.VISIBLE);
                            Toast.makeText(SignUp.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(response.equals("100"))
                        {
                            Toast.makeText(SignUp.this, "No User Exists Try again later...", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            user_submit.setVisibility(View.VISIBLE);
                        }
                        else if(response.equals("505"))
                        {
                            Toast.makeText(SignUp.this, "Database Error", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            user_submit.setVisibility(View.VISIBLE);
                            googleSignInClient.signOut();
                            firebaseAuth.signOut();

                        }
                        else if(response.equals("400"))
                        {
                            Toast.makeText(SignUp.this, "Server Error", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            user_submit.setVisibility(View.VISIBLE);
                            googleSignInClient.signOut();
                            firebaseAuth.signOut();
                        }
                        else
                        {
                            Toast.makeText(SignUp.this, "System Error", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            user_submit.setVisibility(View.VISIBLE);
                            googleSignInClient.signOut();
                            firebaseAuth.signOut();

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
                        int id = group.getCheckedRadioButtonId();
                        RadioButton button = findViewById(id);
                        map.put("Email",user_email.getText().toString());
                        map.put("Name",user_name.getText().toString());
                        map.put("Phone",user_phone.getText().toString());
                        map.put("BloodG",user_bloodg.getText().toString());
                        int day = user_dob.getDayOfMonth();
                        int month = user_dob.getMonth() + 1;
                        int year = user_dob.getYear();
                        map.put("DOJ",doj);
                        map.put("DOB",year+"-"+month+"-"+day);
                        map.put("Employee_Group",button.getText().toString());
                        return map;
                    }
                };
                queue.add(request);
            }
        });

    }

    @Override
    public void onBackPressed() {
        View view = SignUp.this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
        else {
            firebaseAuth.signOut();
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(SignUp.this,Login.class));
                    finish();
                }
            });
        }
    }
}