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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Halfday#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Halfday extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Halfday() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Halfday.
     */
    // TODO: Rename and change types and number of parameters
    public static Halfday newInstance(String param1, String param2) {
        Halfday fragment = new Halfday();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    Button start_date,submit;
    EditText subject_of_leave,body_of_leave;
    ProgressBar progressBar4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_halfday, container, false);
        start_date = view.findViewById(R.id.half_start_date);
        submit = view.findViewById(R.id.half_leave_submit);
        body_of_leave = view.findViewById(R.id.half_body_leave);
        progressBar4 = view.findViewById(R.id.progressBar5);
        subject_of_leave= view.findViewById(R.id.half_leave_subject);
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
            }
        }){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                FirebaseAuth auth = FirebaseAuth.getInstance();

//                    Toast.makeText(getContext(), ""+difference_In_Days, Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    map.put("subject",subject_of_leave.getText().toString());
                    map.put("body",body_of_leave.getText().toString());
                    map.put("start_date",start_date.getText().toString());
                    map.put("end_date",start_date.getText().toString());
                    map.put("leave_length","0.5");
                    map.put("raised_by",user.getEmail());
                    map.put("raised_by_name",user.getDisplayName());
                    map.put("type","0");

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
                    start_date.setText(date);
                }
            }
        },
                Calendar.getInstance().get(Calendar.YEAR)
                ,Calendar.getInstance().get(Calendar.MONTH)
                ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}