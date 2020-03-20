package com.kds.gold.acebird.async;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetAsyncTask extends AsyncTask<String, Void, JSONObject> {

    private OnGetResultsListener listener;
    private Context context;
    private int request_code;

    public GetAsyncTask(Context context, int request_code) {
        this.context = context;
        this.request_code = request_code;
    }

    public void onGetResultsData1(OnGetResultsListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected JSONObject doInBackground(String... params) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(params[0]);
//            Log.e("url", params[0]);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.connect();

            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            String res = buffer.toString();
            JSONObject obj = new JSONObject(res);
//            Map map = JsonUtils.stringObject(res, Map.class);
            return obj;
        } catch (MalformedURLException e) {
            return null;
        } catch (ProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        if (response != null && response.length() > 0) {
            listener.onGetResultsData1(response, request_code);
        } else {
            listener.onGetResultsData1(response, request_code);
        }
    }

    public interface OnGetResultsListener {
        public void onGetResultsData1(JSONObject object, int request_code);
    }
}
