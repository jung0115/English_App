package com.example.english_app;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.widget.SearchView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "dictionary";
    private static final int MAX_ID_SIZE = 4;
    private static ArrayList<String> TAG_LIST = new ArrayList<>();
    private static final int[] ID_LIST = {R.id.textView_list_id, R.id.textView_list_item1, R.id.textView_list_item2, R.id.textView_list_item3};


    private static ArrayList<HashMap<String, String>> dictionary = new ArrayList<>();

    ListView mlistView;
    String mJsonString;
    Button addButton;

    Button korSortToggleBtn, engSortToggleBtn, idClearBtn;
    private boolean korSortToggle, engSortToggle;

    private static boolean idClearFlag = true;

    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(idClearFlag) {
            // 데이터베이스 단어들의 ID를 1부터 ~~~로 초기화하는 작업
            PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.IDCLEAR, new HashMap<>());
            postRequestHandler.execute();
            idClearFlag = false;
        }

        korSortToggle = true;
        engSortToggle = true;

        InitializeView();
        SetListener();

        // Read 구현, 메인화면에 단어들을 출력
        GetData task = new GetData();
        task.execute(Constant.READ);
    }

    // 컴포넌트 id 초기화
    private void InitializeView(){
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        // 단어 추가 버튼 onclick listener
        addButton = findViewById(R.id.add_button);
        // 단어 정렬 버튼
        korSortToggleBtn = findViewById(R.id.kor_sort_toggle_btn);
        engSortToggleBtn = findViewById(R.id.eng_sort_toggle_btn);
        idClearBtn = findViewById(R.id.id_clear_btn);

        searchView = findViewById(R.id.search_view);
    }

    // 컴포넌트 리스너 초기화
    private void SetListener(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WordInputForm.class);
                intent.putExtra("editFlag", false);
                startActivity(intent);
            }
        });

        korSortToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortTable("kor", korSortToggle);
                korSortToggle = !korSortToggle;
                engSortToggle = true;
            }
        });

        engSortToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortTable("eng", engSortToggle);
                engSortToggle = !engSortToggle;
                korSortToggle = true;
            }
        });


        idClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 데이터베이스 단어들의 ID를 1부터 ~~~로 초기화하는 작업
                PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.IDCLEAR, new HashMap<>());
                postRequestHandler.execute();
                idClearFlag = false;
                SortTable("id", true);
                GetData task = new GetData();
                task.execute(Constant.READ);
            }
        });


        // 리스트에서 단어를 클릭 시 한글 메세지가 출력되도록 하는 onClick 함수
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l_position) {
                // i는 리스트뷰에서 몇번째 item을 클릭했는지에 대한 정보
                // adapter에서 .getItem(index)를 하면 index번째 item을 가져올 수 있다.
                // 이때 가져오는 데이터 형식은 위에 ListAdapter ~~ = new SimpleAdapter에서 매개변수로 넣은
                // mArrayList의 형식인 ArrayList<HashMap<String,String>>에서 HashMap 이다.
                // .getItem 후에 형변환을 해줘야 함.
                final HashMap<String,String> clickedWord = (HashMap<String,String>)adapterView.getAdapter().getItem(position);
                Toast.makeText(MainActivity.this, "" + clickedWord.get("kor"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, WordInfo.class);
                intent.putExtra("word", clickedWord);
                startActivity(intent);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ok버튼 누르면 검색
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            // 단어 바뀔때마다 검색
            @Override
            public boolean onQueryTextChange(String s) {
                showSearchedData(s);
                return true;
            }
        });
    }

    // 단어 출력
    private void showData(ArrayList<HashMap<String, String>> data, ArrayList<String> tagList, int[] idList){
        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, data, R.layout.item_list,
                tagList.toArray(new String[TAG_LIST.size()]), idList
        );
        // TAG_LIST = { id, kor, eng, etc... };
        // ID_LIST = {R.id.textView_list_id, R.id.textView_list_item1, R.id.textView_list_item2, R.id.textView_list_item3};
        mlistView.setAdapter(adapter);
    }

    // 매개변수로 단어 검색
    private void showSearchedData(String query) {
        ArrayList<HashMap<String, String>> searchedWords = new ArrayList<>();
        for (HashMap<String, String> word : dictionary) {
            if (word.get("eng").toLowerCase().contains(query.toLowerCase()) || word.get("kor").contains(query))
                searchedWords.add(word);
        }
        showData(searchedWords, TAG_LIST, ID_LIST);
    }


    // 단어장을 sortKey 기준(id, kor, eng)으로 정렬(increase가 true이면 오름차순)
    private void SortTable(String sortKey, boolean increase) {
        Collections.sort(dictionary, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
                if (sortKey.compareTo("id") == 0) {
                    return Integer.parseInt(obj1.get(sortKey)) - Integer.parseInt(obj2.get(sortKey));
                } else {
                    if (increase == true) {
                        return obj1.get(sortKey).compareTo(obj2.get(sortKey));
                    } else {
                        return obj2.get(sortKey).compareTo(obj1.get(sortKey));
                    }
                }
            }
        });
        showData(dictionary, TAG_LIST, ID_LIST);
    }


    // php페이지에서 단어 읽어와서 출력하는 내부 클래스
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {
                Log.d(TAG, "error " + errorString);
            } else {
                // 받아온 Json 데이터를 String으로 저장
                mJsonString = result;
                // String을 파싱
                JsonParser.parseJsonString(dictionary, TAG_LIST, mJsonString, TAG_JSON, MAX_ID_SIZE);
                // 파싱한 데이터를 출력
                showData(dictionary, TAG_LIST, ID_LIST);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            return JsonParser.convertJson(serverURL).trim();
        }
    }
}