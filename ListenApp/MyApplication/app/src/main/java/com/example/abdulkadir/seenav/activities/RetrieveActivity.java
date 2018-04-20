package com.example.abdulkadir.seenav.activities;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.abdulkadir.seenav.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RetrieveActivity extends AppCompatActivity {

    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_URL ="url";

    JSONArray url = null;

    ArrayList<HashMap<String, String>> urlList;
    ArrayAdapter<HashMap<String,String>> adapter;
    ListView list;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        list = (ListView) findViewById(R.id.listView);
        urlList = new ArrayList<HashMap<String,String>>();
        adapter = new ArrayAdapter<HashMap<String,String>>(this, R.layout.list_item, urlList );
        list.setAdapter(adapter);
        getData();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                playSong(position);

            }
        });
    }


    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            url = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<url.length();i++){
                JSONObject c = url.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String url = c.getString(TAG_URL);

                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put(TAG_ID,id);
                persons.put(TAG_NAME,name);
                persons.put(TAG_URL,url);

                urlList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    RetrieveActivity.this, urlList, R.layout.list_item,
                    new String[]{TAG_ID,TAG_NAME,TAG_URL},
                    new int[]{R.id.id, R.id.name, R.id.url}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://projectfeva.esy.es/FileUploads/getURL.php");

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),MODE_PRIVATE);
                String userId = sharedPref.getString("userId",null);

                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("userId", userId));

                InputStream inputStream = null;
                String result = null;
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(param));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    private void playSong(int position) {

        HashMap<String,String> result = adapter.getItem(position);


        // Play song
        mp.reset();// stops any current playing song
        String audioURL = result.get(TAG_URL);

        try {

            mp.setDataSource(audioURL);
            mp.prepare();


        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.start();

    }





}