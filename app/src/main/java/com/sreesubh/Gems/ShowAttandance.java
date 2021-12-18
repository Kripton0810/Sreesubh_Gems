package com.sreesubh.Gems;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowAttandance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowAttandance extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShowAttandance() {
        // Required empty public constructor
    }

    public static ShowAttandance newInstance(String param1, String param2) {
        ShowAttandance fragment = new ShowAttandance();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    FirebaseAuth auth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    List<AttandanceModelClass> list;
    RecyclerView attandance_recycle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_attandance, container, false);
        attandance_recycle = view.findViewById(R.id.attandance_recycle);
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://sreesubh.bookbin.tech/api/view_att.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    attandance_recycle.setLayoutManager(layoutManager);
                    JSONArray array  = new JSONArray(response);
                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        String date = object.getString("date");
                        String status = object.getString("status");
                        String ltime = object.getString("ltime");
                        String lout = object.getString("lout");


                        String latein = object.getString("latein");
                        String earlyout = object.getString("earlyout");
                        String worktime = object.getString("worktime");
                        String ot = object.getString("ot");


                        //String status, String ltime, String lotime, String date, String ot, String latein, String earlyout, String worktime
                        list.add(new AttandanceModelClass(status,ltime,lout,date,ot,latein,earlyout,worktime));
                    }
                    AttandanceAdapterView adapterView = new AttandanceAdapterView(list);
                    attandance_recycle.setAdapter(adapterView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Email",user.getEmail());
                return map;
            }
        };
        queue.add(request);
        return  view;
    }
}