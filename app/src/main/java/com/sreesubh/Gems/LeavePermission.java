package com.sreesubh.Gems;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LeavePermission extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LeavePermission() {
    }

    public static LeavePermission newInstance(String param1, String param2) {
        LeavePermission fragment = new LeavePermission();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    Button end_date,start_date,submit;
    EditText subject_of_leave,body_of_leave;
    ProgressBar progressBar4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_permission, container, false);
        start_date = view.findViewById(R.id.start_date);
        end_date = view.findViewById(R.id.end_date);
        submit = view.findViewById(R.id.leave_submit);
        progressBar4 = view.findViewById(R.id.progressBar4);
        body_of_leave = view.findViewById(R.id.body_of_leave);
        subject_of_leave= view.findViewById(R.id.subject_of_leave);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(10);
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(20);
            }
        });
        
        return view;
    }

    private void sendData() {
        progressBar4.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, "https://sreesubh.bookbin.tech/api/leave_request.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("message"))
                    {
                        Toast.makeText(getContext(), "Request Uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().onBackPressed();
                NavigationView navigationView = getActivity().findViewById(R.id.menubar);
                navigationView.setCheckedItem(R.id.nav_home);
                progressBar4.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout,new HomeFragement());
                transaction.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Request Uploaded unsuccessfully one day one request", Toast.LENGTH_SHORT).show();
                progressBar4.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        }){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date d1 = sdf.parse(start_date.getText().toString());
                    Date d2 = sdf.parse(end_date.getText().toString());
                    long difference_In_Time = d2.getTime() - d1.getTime();
                    long difference_In_Days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
                    FirebaseUser user = auth.getCurrentUser();
                    map.put("subject",subject_of_leave.getText().toString());
                    map.put("body",body_of_leave.getText().toString());
                    map.put("start_date",start_date.getText().toString());
                    map.put("end_date",end_date.getText().toString());
                    map.put("leave_length",(difference_In_Days+1)+"");
                    map.put("raised_by",user.getEmail());
                    map.put("raised_by_name",user.getDisplayName());
                    map.put("type","1");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };
        queue.add(request);
    }

    private void showDatePicker(int n)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date;
                date = year+"-"+(month+1)+"-"+dayOfMonth;
                Log.d("Date_check",date);
                if(n==10)
                {
                    start_date.setText(date);
                }
                else {
                    end_date.setText(date);
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR)
        ,Calendar.getInstance().get(Calendar.MONTH)
        ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}