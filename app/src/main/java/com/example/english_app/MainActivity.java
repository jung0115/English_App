package com.example.english_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

// import android.widget.Toolbar; 이거를 아래거로 변경해야함


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

//연관어 검색 TCP/IP 통신에 필요한 부분
import android.os.Handler;
import android.widget.EditText;
import android.widget.ArrayAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;


// View.OnClickListener를 상속받아
// onClick 함수로 클릭 시 리스너를 한꺼번에 구현함
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context = this;

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "dictionary";
    private static final int MAX_ID_SIZE = 4;
    private static ArrayList<String> TAG_LIST = new ArrayList<>();
    private static final int[] ID_LIST = {R.id.textView_list_id, R.id.textView_list_item1, R.id.textView_list_item2, R.id.textView_list_item3};


    private static ArrayList<HashMap<String, String>> dictionary = new ArrayList<>();

    ListView mlistView;
    String mJsonString;
    Button addButton;


    TextView korSortToggleBtn, engSortToggleBtn, idClearBtn;
    private boolean korSortToggle, engSortToggle, menuShowToggle=false;


    private static boolean idClearFlag = true;

    SearchView searchView;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // 기존 메뉴 버튼 코드
    /*
    private DrawerLayout drawerLayout;
    private View sliderView;
    Button closeSliderBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //false가 보이지 않는 상태
        if (menuShowToggle==false){
            menuShowToggle = true;
            showMenu();
        }
        else {
            menuShowToggle = false;
            hideMenu();
        }
        return true;
    }
    private void showMenu(){
        drawerLayout.openDrawer(sliderView);
    }
    private void hideMenu(){
        drawerLayout.closeDrawers();
    }
    */
    ///////////////////////////////////////////////////////////////////////////////////////////////


    // 연관어 검색 TCP/IP 통신에 필요한 부분
    Button connect_btn;                 // word 받아오는 버튼

    EditText word_edit;               // word 에디트
    //ListView show_text;             // 서버에서 온 거 보여주는 에디트

    ArrayList<String> words = new ArrayList<String>(); // 연관어 저장할 리스트

    // 소켓통신에 필요한것
    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;

    private String ip = "203.255.3.164";            // 연구실컴 IP 번호
    private int port = 9999;                      // port 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //연관어 검색에 필요한 부분
        connect_btn = (Button)findViewById(R.id.connect_btn);
        connect_btn.setOnClickListener(this);
        word_edit = (EditText)findViewById(R.id.word_edit);


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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.connect_btn:     // ip 받아오는 버튼
                connect();
        }
    }

    // 로그인 정보 db에 넣어주고 연결시켜야 함.
    void connect(){
        mHandler = new Handler();
        Log.w("connect","연결 하는중");
        // 받아오는거
        Thread checkUpdate = new Thread() {
            public void run() {
                // ip받기
                String newip = String.valueOf(ip);

                // 서버 접속
                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                } catch (IOException e1) {
                    Log.w("서버접속못함", "서버접속못함");
                    e1.printStackTrace();
                }

                Log.w("edit 넘어가야 할 값 : ","안드로이드에서 서버로 연결요청");

                try {
                    dos = new DataOutputStream(socket.getOutputStream());   // output에 socket server로 보낼 거 넣음
                    dis = new DataInputStream(socket.getInputStream());     // input에 socket server에서 받은 거 들어감
                    String search_word = String.valueOf(word_edit.getText()); //검색할 단어 입력 받기
                    dos.writeUTF(search_word); //Python 서버로 문자열 보내기

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 잘못됨");
                }
                Log.w("버퍼","버퍼생성 잘됨");

                // Python 서버에서 받아옴
                try {
                    String len = "";
                    String line = "";

                    int count = 0;
                    while(true) {
                        if (count == 50) { //50개의 데이터를 전송 받으면 종료
                            break;
                        }
                        len = (String)dis.readUTF();
                        line = (String)dis.readUTF(); //python server에서 전송한 값을 받아옴
                        words.add(line);
                        Log.w("서버에서 받아온 값 ", ""+count+": "+words.get(count));
                        count++;
                    }
                }catch (Exception e){

                }
            }
        };
        // 소켓 접속 시도, 버퍼생성
        checkUpdate.start();
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

        // 슬라이드메뉴
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //sliderView = (View) findViewById(R.id.slider);
        //closeSliderBtn = (Button) findViewById(R.id.closeSliderBtn);
    }

    // 컴포넌트 리스너 초기화
    private void SetListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.add_button:
                        Intent intent = new Intent(MainActivity.this, WordInputForm.class);
                        intent.putExtra("editFlag", false);
                        startActivity(intent);
                        break;
                    case R.id.kor_sort_toggle_btn:
                        SortTable("kor", korSortToggle);
                        korSortToggle = !korSortToggle;
                        engSortToggle = true;
                        break;
                    case R.id.eng_sort_toggle_btn:
                        SortTable("eng", engSortToggle);
                        engSortToggle = !engSortToggle;
                        korSortToggle = true;
                        break;
                    case R.id.id_clear_btn:
                        // 데이터베이스 단어들의 ID를 1부터 ~~~로 초기화하는 작업
                        PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.IDCLEAR, new HashMap<>());
                        postRequestHandler.execute();
                        idClearFlag = false;
                        SortTable("id", true);
                        GetData task = new GetData();
                        task.execute(Constant.READ);
                        break;
                }
            }
        };
        addButton.setOnClickListener(onClickListener);
        korSortToggleBtn.setOnClickListener(onClickListener);
        engSortToggleBtn.setOnClickListener(onClickListener);
        idClearBtn.setOnClickListener(onClickListener);
        //closeSliderBtn.setOnClickListener(onClickListener);



        // 리스트에서 단어를 클릭할 때의 onClick 함수
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l_position) {
                // i는 리스트뷰에서 몇번째 item을 클릭했는지에 대한 정보
                // adapter에서 .getItem(index)를 하면 index번째 item을 가져올 수 있다.
                // 이때 가져오는 데이터 형식은 위에 ListAdapter ~~ = new SimpleAdapter에서 매개변수로 넣은
                // mArrayList의 형식인 ArrayList<HashMap<String,String>>에서 HashMap 이다.
                // .getItem 후에 형변환을 해줘야 함.
                final HashMap<String,String> clickedWord = (HashMap<String,String>)adapterView.getAdapter().getItem(position);
                //Toast.makeText(MainActivity.this, "" + clickedWord.get("kor"),Toast.LENGTH_SHORT).show();
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


