package com.example.english_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class WordInfo extends AppCompatActivity {
    HashMap<String,String> word;
    TextView korText, engText;
    Button deleteButton, editButton, backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_info);

        Intent intent = getIntent();
        word = (HashMap<String,String>)intent.getSerializableExtra("word");


        // 한글, 영어 세팅
        korText = findViewById(R.id.kor_text);
        engText = findViewById(R.id.eng_text);
        korText.setText(word.get("kor"));
        engText.setText(word.get("eng"));


        // 뒤로가기 버튼
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMainActivityIntent = new Intent(WordInfo.this, MainActivity.class);
                startActivity(goMainActivityIntent);
            }
        });


        // 지우기 버튼
        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 단어 삭제 관련 php에 삭제할 단어의 id를 post 방식으로 전달
                String id = word.get("id");
                HashMap<String, String> request = new HashMap<>();
                request.put("id",id);
                // 전달하는 함수
                PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.DELETE, request);
                postRequestHandler.execute();

                // MainActivity로 이동
                Intent goMainActivityIntent = new Intent(WordInfo.this, MainActivity.class);
                startActivity(goMainActivityIntent);
            }
        });


        // 수정 버튼
        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수정할 단어에 대한 정보를 WordInputForm으로 전달/이동
                Intent goWordInputFormIntent = new Intent(WordInfo.this, WordInputForm.class);
                goWordInputFormIntent.putExtra("editFlag", true); // 수정모드
                goWordInputFormIntent.putExtra("word", word); // 수정 대상 단어 정보 전달
                startActivity(goWordInputFormIntent);
            }
        });

    }
}