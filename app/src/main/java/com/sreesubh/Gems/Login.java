package com.sreesubh.Gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    FirebaseAuth firebaseAuth;
    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 100;
    private static final String EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions googleSignInOptions =new GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("1000880822314-5u63cg3e69seovef7ua5tbptnlk2qdob.apps.googleusercontent.com")
            .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        signInButton = findViewById(R.id.gsingin);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            startActivity(new Intent(Login.this,MainDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
//        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email","public_profile"));
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handelFacebokToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handelFacebokToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Login.this,MainDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(Login.this, "UnSuccessful "+task.getException(), Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    Log.d("Facebook_Exception",task.getException().getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful())
            {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                    if(account!=null)
                    {
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull  Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {

                                    RequestQueue queue= Volley.newRequestQueue(Login.this);
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Log.d("USER",user.getEmail());
                                    String url = "https://sreesubh.bookbin.tech/api/login.php?Email=" + user.getEmail();
                                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getBoolean("message"))
                                                {
                                                    startActivity(new Intent(Login.this,MainDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                                else
                                                {
                                                    googleSignInClient.signOut();
                                                    firebaseAuth.signOut();
                                                    Toast.makeText(Login.this, "No User Exists", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Login.this,SignUp.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(Login.this, "Server Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            googleSignInClient.signOut();
                                            firebaseAuth.signOut();
                                        }
                                    });
                                    queue.add(objectRequest);
                                }else
                                {
                                    Log.d("API 1","Error-> "+task.getException());
                                    Toast.makeText(Login.this, "1 Auth Fail!!-> "+task.getException(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e);
                }
            }
            else
            {
                Log.d("API 2","Error->"+task.getException());
                Toast.makeText(Login.this, "2 Auth Fail!!-> "+task.getException(), Toast.LENGTH_SHORT).show();

            }

        }
    }

}