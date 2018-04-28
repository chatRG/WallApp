package com.wallapp.async;


import android.os.AsyncTask;

import com.wallapp.store.StaticVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ParseBing extends AsyncTask<Void, Void, String> {
    private static final String JSON_URL = StaticVars.BING_DAILY_URL;
    private AsyncResponse delegate = null;

    public ParseBing(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String jsonData;
        StringBuilder mBuilder = new StringBuilder();
        String imageURL;
        List<String> imageURI = new ArrayList<>();

        try {
            // JSON fetch
            URL url = new URL(JSON_URL);
            HttpURLConnection mConnection = (HttpURLConnection) url.openConnection();
            BufferedReader mReader =
                    new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
            while ((jsonData = mReader.readLine()) != null) {
                mBuilder.append(jsonData).append("\n");
            }
            mReader.close();
            mConnection.disconnect();

            // JSON parse
            JSONObject mJObject = new JSONObject(mBuilder.toString().trim());
            JSONArray mJArray = mJObject.getJSONArray("images");
            for (int i = 0; i < mJArray.length(); i++) {
                imageURL = StaticVars.BING_BASE_URL + mJArray.getJSONObject(i).getString("url");
                imageURI.add(imageURL);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return imageURI.get(0);
    }

    @Override
    protected void onPostExecute(String s) {
        if (delegate != null)
            delegate.jsonURI(s);
    }

    public interface AsyncResponse {
        void jsonURI(String mURI);
    }
}