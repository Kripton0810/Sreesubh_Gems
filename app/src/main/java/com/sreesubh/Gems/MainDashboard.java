package com.sreesubh.Gems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    Toolbar toolbar;
    TextView title,email;
    CircleImageView propic;

    public final int HOME_FRAGEMENT = 1;
    public final int VIEW_ATTANDANCE = 2;
    public final int HOLIDAY_LIST = 3;
    public final int ASK_LEAVE = 4;
    public final int HALF_DAY=5;
    public  static int CURRENT=0;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CURRENT=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =auth.getCurrentUser();
        setFragement(new HomeFragement(),HOME_FRAGEMENT);
        RequestQueue queue = Volley.newRequestQueue(MainDashboard.this);
        String url = "https://sreesubh.bookbin.tech/api/attandance.php";

        Dexter.withContext(MainDashboard.this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("message"))
                    {
                        Toast.makeText(MainDashboard.this, "Attandence Given!!!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainDashboard.this, "Give your Attandance", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Attandance.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainDashboard.this, "Server error!!!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Email",firebaseUser.getEmail());
                return map;
            }
        };
        queue.add(request);


        navigationView = findViewById(R.id.menubar);
        drawerLayout = findViewById(R.id.drawerlayout);
        frameLayout  = findViewById(R.id.framelayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainDashboard.this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        googleSignInClient = GoogleSignIn.getClient(MainDashboard.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        View view = navigationView.getHeaderView(0);
        email = view.findViewById(R.id.profile_email);
        title =view.findViewById(R.id.profile_name);
        propic = view.findViewById(R.id.profile_pic);
        if(firebaseUser!=null)
        {
            email.setText(firebaseUser.getEmail());
            title.setText(firebaseUser.getDisplayName());
            Glide.with(MainDashboard.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(propic);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title_menu_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add_upload_post)
        {
            Intent i= new Intent(MainDashboard.this,Add_Post.class);
            startActivity(i);
        }
        return true;
    }
    public void setFragement(Fragment fragement, int fragement_number)
    {
        if(CURRENT!=fragement_number) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            CURRENT = fragement_number;
            transaction.replace(R.id.framelayout, fragement);
            transaction.commit();
        }
    }
    public void gotoFragement(int fragement)
    {
        if(fragement == HOME_FRAGEMENT)
        {
            setFragement(new HomeFragement(),HOME_FRAGEMENT);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            navigationView.setCheckedItem(R.id.nav_home);
        }else if(fragement == VIEW_ATTANDANCE)
        {
            setFragement(new ShowAttandance(),VIEW_ATTANDANCE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("My Attandance");
            navigationView.setCheckedItem(R.id.attandance);
        }else if(fragement == HOLIDAY_LIST)
        {
            setFragement(new HolidayView(),HOLIDAY_LIST);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
//            getSupportActionBar().setIcon(null);
            getSupportActionBar().setTitle("My Holiday List");
            navigationView.setCheckedItem(R.id.holiday_nav);
        }
        else if(fragement == ASK_LEAVE)
        {

            setFragement(new LeavePermission(),ASK_LEAVE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            navigationView.setCheckedItem(R.id.ask_for_leave);
        }
        else if(fragement == HALF_DAY)
        {
            setFragement(new Halfday(),HALF_DAY);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            navigationView.setCheckedItem(R.id.half_day);
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout layout = findViewById(R.id.drawerlayout);

        if(layout.isDrawerOpen(GravityCompat.START))
        {
            layout.closeDrawer(GravityCompat.START);
        }else {
            if(CURRENT!=HOME_FRAGEMENT)
            {
                gotoFragement(HOME_FRAGEMENT);
            }
            else
            {
                super.onBackPressed();
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        DrawerLayout layout = findViewById(R.id.drawerlayout);

        switch (id)
        {
            case R.id.nav_home:
            {
                gotoFragement(HOME_FRAGEMENT);
                break;
            }
            case R.id.logout:
            {
                startActivity(new Intent(MainDashboard.this,Logout.class));
                finish();
                break;
            }
            case R.id.attandance:
            {
                gotoFragement(VIEW_ATTANDANCE);
                break;
            }
            case R.id.holiday_nav:
            {
                gotoFragement(HOLIDAY_LIST);
                break;
            }

            case R.id.ask_for_leave:
            {
                gotoFragement(ASK_LEAVE);
                break;
            }
            case R.id.half_day:
            {
                gotoFragement(HALF_DAY);
                break;
            }
        }
        layout.closeDrawer(GravityCompat.START);
        return true;
    }
}