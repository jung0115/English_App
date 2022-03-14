package com.example.english_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;


// 참고 블로그 : https://blog.naver.com/PostView.nhn?isHttpsRedirect=true&blogId=cosmosjs&logNo=221391803528&redirect=Dlog
// 툴바를 여러 Activity에 공통으로 적용하는 방법
// 위 기능에 DrawerLayout을 추가로 적용한 코드
// DrawerLayout관련 xml코드는 activity_base.xml, activity_base_login.xml에 있음
// DrawerLayout 사용법 관련 블로그는 아래
// 참고 블로그 : https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=qbxlvnf11&logNo=221641795446
// 참고 블로그 : https://g-y-e-o-m.tistory.com/128
// 참고 블로그 : https://aries574.tistory.com/126
// 참고 블로그 : https://fffounding.tistory.com/m/94
// 참고 블로그 : https://lktprogrammer.tistory.com/161
// 참고 블로그 : https://soohyun6879.tistory.com/77
public class BaseActivity extends AppCompatActivity {
    Context context = this;
    static private boolean loginFlag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(UserConstant.isLogined == false) {
            setContentView(R.layout.activity_base);
        }
        else{
            setContentView(R.layout.activity_base_login);
        }
    }

    @Override
    public void setContentView(int layoutResID){
        if(UserConstant.isLogined == false) {
            // 기존 코드
            //LinearLayout fullView = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
            // DrawerLayout 코드
            DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
            FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
            getLayoutInflater().inflate(layoutResID, activityContainer, true);
            super.setContentView(fullView);
        }
        else{
            DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base_login, null);
            FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
            getLayoutInflater().inflate(layoutResID, activityContainer, true);
            super.setContentView(fullView);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); //뒤로가기 버튼 이미지 지정


        // 사이드바 메뉴 클릭 시 이벤트 처리

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //menuItem.setChecked(true); // 이거 주석 풀면 계속 색깔 변해있음 ㅅㅂ
                return navigationItemSelectedExecutor(menuItem);
            }
        });

        //툴바 사용여부 결정(기본적으로 사용)
        if(useToolbar()){
            setSupportActionBar(toolbar);
            setTitle("툴바예제");
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }


    private boolean navigationItemSelectedExecutor(MenuItem menuItem){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.fullView);
        int id = menuItem.getItemId();
        String title = menuItem.getTitle().toString();
        Intent intent;
        if(UserConstant.isLogined == false) {
            // 로그인 안된 상태
            switch (id) {
                case R.id.setting:
                    Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.login:
                    Toast.makeText(context, title + ": 로그인 시도중", Toast.LENGTH_SHORT).show();
                    //loginFlag = true;
                    //Intent intent = new Intent(context, MainActivity.class);
                    intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.signin:
                    intent = new Intent(context, SignInActivity.class);
                    startActivity(intent);
                    Toast.makeText(context, title + "회원가입 시도중", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
        else{
            switch (id) {
                case R.id.account:
                    Toast.makeText(context, title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                case R.id.setting:
                    Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.logout:
                    Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
                    //loginFlag = false;
                    //Intent intent = new Intent(context, MainActivity.class);
                    UserConstant.ClearUserData();
                    intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
        return true;
    }



    //툴바를 사용할지 말지 정함
    protected boolean useToolbar(){
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 아래 5줄은 onClick에 넣으면 아직 생성되지않은 상태라서 에러뜸.
        // 정확히 어디에서 생성되는지는 모르겠음.
        TextView textUserID, textUserEmail;
        textUserID = findViewById(R.id.header_text_user_id);
        textUserEmail = findViewById(R.id.header_text_user_email);
        textUserID.setText(UserConstant.GetUserID());
        textUserEmail.setText(UserConstant.GetUserEmail());


        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.fullView);
                mDrawerLayout.openDrawer(Gravity.LEFT);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /*
    //메뉴 등록하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.login_navi_menu, menu);
        return true;
    }
    */



    /*
    //앱바 메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Toast.makeText(getApplicationContext(),"setting", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.login:
                Toast.makeText(getApplicationContext(),"login", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.signin:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(),"signin", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    */
}
