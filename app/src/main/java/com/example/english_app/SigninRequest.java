package com.example.english_app;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SigninRequest extends StringRequest {

    private HashMap<String, String> map;

    // 생성자
    public SigninRequest(String userID, String userPassword, String userEmail, Response.Listener<String> listener) {
        super(Method.POST, Constant.SIGNIN, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPassword", userPassword);
        map.put("userEmail", userEmail);
        Log.d("SigninActivity", map.toString());
    }

    protected Map<String, String> getParams() throws AuthFailureError{
        return map;
    }
}
