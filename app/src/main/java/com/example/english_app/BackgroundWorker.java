package com.example.english_app;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


// 출처: https://ilbbang.tistory.com/52 [일빵의 티스토리]
// Background 에서 수행할 매서드가 있는 클래스입니다. Post로 보내기 및 받기 매서드가 있습니다.
public class BackgroundWorker {
    // Make a POST Request Handler
    public String postRequestHandler(String requestUrl, HashMap<String, String> requestedDataParams) throws UnsupportedEncodingException {
        // Set an Empty URL obj in system URL url;
        // Set a String Builder to store result as string
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Now Initialize
            URL url = new URL(requestUrl);
            // Make a HTTP url connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set Method Type
            connection.setRequestMethod(Constant.POST_METHOD);
            // Set Connection Time
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            // set Input output ok
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Remove Caches
            // connection.setUseCaches(false);
            // connection.setDefaultUseCaches(false);
            // Creating a url as String with params
            StringBuilder url_string = new StringBuilder();
            boolean ampersand = false;
            for (Map.Entry<String, String> params : requestedDataParams.entrySet()) {
                if (ampersand)
                    url_string.append("&");
                else
                    ampersand = true;
                url_string.append(URLEncoder.encode(params.getKey(), "UTF-8"));
                url_string.append("=");
                url_string.append(URLEncoder.encode(params.getValue(), "UTF-8"));
            }
            Log.d("Final Url===", url_string.toString());
            //Creating an output stream
            OutputStream outputStream = connection.getOutputStream();
            // Write Output Steam
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(url_string.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            Log.d("Response===", connection.getResponseMessage());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // Local String
                String result;
                while ((result = bufferedReader.readLine()) != null) {
                    stringBuilder.append(result);
                    Log.d("Result===", result);
                }
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // Get Request Handler
    public String getRequestHandler(String requestUrl){
        // To Store response
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(requestUrl);
            // Open Connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // Local
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}