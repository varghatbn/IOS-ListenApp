package com.example.abdulkadir.seenav.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abdulkadir.seenav.Config;
import com.example.abdulkadir.seenav.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextId;
    private Button buttonSearch;
    private TextView textViewResult;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextId = (EditText) findViewById(R.id.editTextId);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        buttonSearch.setOnClickListener(this);
    }

    private void getData() {
        String id = editTextId.getText().toString().trim();
        if (id.equals("")) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
            return;
        }
        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        String url = Config.DATA_URL+editTextId.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        String username="";
        String name="";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONArray userData = result.getJSONArray(0);
            username = userData.getString(Config.KEY_USERNAME);
            name = userData.getString(Config.KEY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textViewResult.setText("Name:\t"+name+"\nusername:\t" +username);
    }

    @Override
    public void onClick(View v) {
        getData();
    }
}