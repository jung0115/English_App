package com.example.english_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// 출처: https://ilbbang.tistory.com/52 [일빵의 티스토리]
// Json으로 받은 내용을 안드로이드 화면에 출력하기 위해 변환하는 매서드가 있습니다.
public class JsonParser {
    private static final String TAG = JsonParser.class.getSimpleName();
    public static String convertJson(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseStatusCode = conn.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            conn.setRequestMethod("GET");

            // read the response
            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(conn.getInputStream());
            } else {
                inputStream = new BufferedInputStream(conn.getErrorStream());
            }
            response = convertStreamToString(inputStream);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }


    private static String convertStreamToString(InputStream is) {
        // 한글을 받기 위해서 Reader로 받아야 한다.
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void parseJsonString(ArrayList<HashMap<String, String>> dictionary, ArrayList<String> TAG_LIST, String jsonString, String TAG_JSON, int MAX_ID_SIZE){
        dictionary.clear();
        TAG_LIST.clear();
        // Json 형식의 String을 파싱하여 화면에 단어들을 출력
        try {
            Log.d(TAG, "jsonString : " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                // php에 몇개의 key가 존재하는지를 확인하기 위한 코드
                // id, kor, eng <- 이 3개를 알 수 있다. i==0에서만 실행된다.
                if (i == 0) {
                    Iterator iterator = item.keys();
                    while (iterator.hasNext()) {
                        TAG_LIST.add(iterator.next().toString());
                    }
                }

                // 값을 가져와서 그 값들과 Tag를 매칭시켜
                // ArrayList<HashMap<String,String>>에 저장하는 코드
                String[] values = new String[TAG_LIST.size()];
                for (int j = 0; j < TAG_LIST.size(); j++) {
                    values[j] = item.getString(TAG_LIST.get(j));
                }
                HashMap<String, String> hashMap = new HashMap<>();

                for (int j = 0; j < MAX_ID_SIZE; j++) {
                    if (j < TAG_LIST.size()) {
                        hashMap.put(TAG_LIST.get(j), values[j]);
                    } else {
                        hashMap.put("" + j, "");
                    }
                }
                dictionary.add(hashMap);
            }
            // item_list.xml에 태그 칸이 4개 존재하는데
            // php에 출력된 태그 개수가 4보다 모자랄때, 이 개수를 4개로 맞추기 위한 코드
            for (int i = TAG_LIST.size(); i < MAX_ID_SIZE; i++) {
                TAG_LIST.add("" + i);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}