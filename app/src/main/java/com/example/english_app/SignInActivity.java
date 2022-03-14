package com.example.english_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    EditText editUserID, editPassword,editEmail;
    Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editUserID = findViewById(R.id.edit_userid);
        editPassword = findViewById(R.id.edit_password);
        editEmail = findViewById(R.id.edit_email);



        btnSignin = findViewById(R.id.btn_signin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = editUserID.getText().toString();
                String userPassword = editPassword.getText().toString();
                String userEmail = editEmail.getText().toString();


                // 예외처리
                if(IsUserIDAvailable(userID) == false){
                    return;
                }
                if(IsUserEmailAvailable(userEmail) == false){
                    return;
                }
                if(IsUserPasswordAvailable(userPassword) == false){
                    return;
                }


                // php POST 전송문
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("SigninActivity", response);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(response.contains("Duplicate entry")) {
                                Toast.makeText(getApplicationContext(), "중복된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };

                // php POST 전송문
                SigninRequest signinRequest = new SigninRequest(userID, userPassword, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignInActivity.this);
                queue.add(signinRequest);
            }
        });
    }



    private boolean IsUserIDAvailable(String userID){
        if(userID.length() < 4 || userID.length() > 20){
            if(userID.length() < 4){
                Toast.makeText(getApplicationContext(),
                        "아이디는 최소 4글자 이상이어야 합니다.",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "아이디는 최대 20글자 이하여야 합니다.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        if(userID.matches("^[a-zA-Z0-9]*$") == false){
            Toast.makeText(getApplicationContext(),
                    "아이디에는 영문자와 숫자만 사용할 수 있습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean IsUserEmailAvailable(String userEmail){
        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() == false){
            Toast.makeText(getApplicationContext(),
                    "이메일 형식에 맞지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userEmail.length() >= 50){
            Toast.makeText(getApplicationContext(),
                    "이메일 주소의 길이는 최대 50입니다.",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean IsUserPasswordAvailable(String userPassword){
        //Pattern pattern = Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,20})");
        //Matcher matcher = pattern.matcher(userPassword);
        //matcher.matches() == false
        String matchPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
        if(Pattern.matches(matchPattern, userPassword) == false){
            Toast.makeText(getApplicationContext(),
                    "비밀번호는 8~20글자로 숫자, 문자, 특수문자 각각 1개 이상을 포함해야 합니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}