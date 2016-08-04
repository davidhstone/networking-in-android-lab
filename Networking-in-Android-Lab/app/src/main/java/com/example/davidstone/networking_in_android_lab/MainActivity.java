package com.example.davidstone.networking_in_android_lab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private ListView mListview;

    ArrayList<String> mItemList;
    ArrayAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mListview = (ListView) findViewById(R.id.listview);
        mItemList = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, mItemList);
        mListview.setAdapter(mAdapter);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com" +
                        "/v1/search?apiKey=tp3ecfpvms4jjtmyj9rt2wqg&format=json&query=cereal");
            }

        });

        mButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mItemList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com" +
                        "/v1/search?apiKey=tp3ecfpvms4jjtmyj9rt2wqg&format=json&query=chocolate");
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemList.clear();
                new DownloadTask().execute("http://api.walmartlabs.com" +
                        "/v1/search?apiKey=tp3ecfpvms4jjtmyj9rt2wqg&format=json&query=tea");
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            Toast.makeText(this, "You're connected",
                    Toast.LENGTH_LONG).show();
        }
        else {
            //the connection is not available
            Toast.makeText(this, "You're not connected",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void downloadUrl(String myUrl) throws IOException, JSONException {
        InputStream is = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            is = conn.getInputStream();

            String contentAsString = readIt(is);
            parseJson(contentAsString);

        } finally{
            if (is != null){
                is.close();
            }
        }
    }

    private String readIt(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String read;

        while((read = br.readLine()) != null) {
            sb.append(read);
        }
        return sb.toString();
    }

    private void parseJson(String contentAsString) throws JSONException {
        //LinkedList itemByTypeList = new LinkedList();
       // String itemList = "";
       // JSONArray array = new JSONArray(contentAsString);

        JSONObject search = new JSONObject(contentAsString);
        JSONArray items = search.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {

            JSONObject item = items.getJSONObject(i);
            mItemList.add(item.getString("name"));
            //itemByTypeList += item.getString("name") + "/n";
            // itemList += item.getString("name") + "\n";
        }
        //return itemByTypeList;
    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {



        @Override
        protected Void doInBackground(String... strings) {
            try {
                downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {



            super.onPostExecute(s);
            mAdapter.notifyDataSetChanged();
        }
    }


}
