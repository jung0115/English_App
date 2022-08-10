package com.example.english_app;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private HashMap<String, String> map;


    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, Constant.LOGIN, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPassword", userPassword);
    }

    protected Map<String, String> getParams() throws AuthFailureError{
        return map;
    }
}
