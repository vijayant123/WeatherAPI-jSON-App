package com.productx.weatherapi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends ActionBarActivity {

    final int delay = 2000;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String apiUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Delhi&units=metric&APPID=2156c7904a18c482e938371ec490a655";
        TextView t = (TextView) findViewById(R.id.text);
        t.setText("Hello");
        Toast.makeText(this, "Loading API...", Toast.LENGTH_LONG).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadJSON(apiUrl);
            }
        }, delay);
    }

    private void loadJSON(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String temp = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet req = new HttpGet(url);
                try {
                    // Execute HTTP GET Request
                    HttpResponse response = httpclient.execute(req);
                    temp = EntityUtils.toString(response.getEntity());
                    Log.i("tag", temp);
                } catch (ClientProtocolException e) {
                    Log.e("app", "PROTOCOL ERROR");
                    return;
                } catch (IOException e) {
                    Log.e("app", "NETWORK IO ERROR");

                    return;
                }
                parseJSON(temp);
            }
        }).start();
    }

    public void parseJSON(String jsonText) {
        //Toast.makeText(MainActivity.this, "Rendering JSON...", Toast.LENGTH_LONG).show();
        Log.i("tag", jsonText);
        StringBuilder v = new StringBuilder();
        try {
            JSONObject json = new JSONObject(jsonText);
            JSONObject city = json.getJSONObject("city");
            v.append("City Name: ");
            v.append(city.getString("name"));
            JSONObject coord = city.getJSONObject("coord");
            v.append("\nCoord: (");
            v.append((coord.get("lat")).toString());
            v.append("), (");
            v.append((coord.get("lon")).toString());
            v.append(")\nCountry: ");
            v.append(city.getString("country"));
            int cnt = json.getInt("cnt");
            v.append("\nForecast for ");
            v.append(cnt + " ");
            v.append("days: \n\n\n");
            JSONArray list = json.getJSONArray("list");
            JSONObject li = null;
            for(int i=0; i<cnt; i++){
                li = list.getJSONObject(i);
                Timestamp stamp = new Timestamp(li.getLong("dt") * 1000);
                Date date = new Date(stamp.getTime());
                v.append(date.toString());
                v.append(": \ntemp:");
                v.append(((li.getJSONObject("temp")).get("day")).toString());
                v.append("  \n\n");
            }



            Log.i("json", v.toString());
            updateView(v.toString());
        } catch (JSONException e) {
            Log.e("tag", "Parsing failed");
            return;
        }


    }

    public void updateView(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.text)).setText(text);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
