package com.example.english_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class WordInputForm extends AppCompatActivity {
    boolean editFlag = false;
    EditText korText,engText;
    Button okButton,exitButton;
    String wordID;

    HashMap<String, String> word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_input_form);

        korText = findViewById(R.id.kor_text);
        engText = findViewById(R.id.eng_text);

        Intent intent = getIntent();

        // 수정모드인지를 확인하여, 수정모드면 kor, eng 입력칸에
        // 수정할 단어의 한글/영어를 적는다
        editFlag = intent.getBooleanExtra("editFlag", false);
        if(editFlag == true){
            word = (HashMap<String,String>)intent.getSerializableExtra("word");
            wordID = word.get("id");
            korText.setText(word.get("kor"));
            engText.setText(word.get("eng"));
        }


        // 확인 버튼, 입력된 kor/eng 정보를 토대로 단어를 생성/수정한다
        okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kor, eng;
                kor = korText.getText().toString();
                eng = engText.getText().toString();
                if(kor.length() <= 0 || eng.length() <= 0){
                    return;
                }

                korText.setText("");
                engText.setText("");

                HashMap<String, String> request = new HashMap<>();
                if(editFlag == true) {
                    // 수정모드의 경우 수정대상 단어의 id를 함께 전달한다
                    request.put("id", wordID);
                }
                request.put("kor", kor);
                request.put("eng", eng);

                if(editFlag == true) {
                    // 수정모드면 Update 처리 php파일로 post 요청을 보낸다
                    PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.UPDATE, request);
                    postRequestHandler.execute();
                }
                else {
                    // 추가모드면 Create 처리 php파일로 post 요청을 보낸다
                    PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.CREATE, request);
                    postRequestHandler.execute();
                }

                // MainActivity로 이동
                Intent goMainActivityIntent = new Intent(WordInputForm.this, MainActivity.class);
                startActivity(goMainActivityIntent);
            }
        });


        // 취소 버튼, 단어 생성/수정을 취소하고 MainActivity로 이동
        exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                korText.setText("");
                engText.setText("");

                // MainActivity로 이동
                Intent goMainActivityIntent = new Intent(WordInputForm.this, MainActivity.class);
                startActivity(goMainActivityIntent);
            }
        });
    }
}