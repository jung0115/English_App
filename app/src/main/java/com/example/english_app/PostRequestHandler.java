package com.example.english_app;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

// 출처: https://ilbbang.tistory.com/52 [일빵의 티스토리]
// Post 방식의 Requset를 보내기 위한 클래스 입니다. 네트워크 관련 처리기 때문에 별도의 스래드(3. BackgroundWorker)를 생성하여 수행합니다.
public class PostRequestHandler extends AsyncTask<Void, Void, String>
{
    // php URL 주소
    String url;
    // Key, Value 값
    HashMap<String, String> requestedParams;
    PostRequestHandler(String url, HashMap<String, String> params){
        this.url = url;
        this.requestedParams = params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        // post request 보냄
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        try {
            return backgroundWorker.postRequestHandler(url, requestedParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}