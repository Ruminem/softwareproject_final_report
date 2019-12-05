package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // 상단, 중단부
    // 프래그먼트 매니저 선언
    FragmentManager fragmentManager = getSupportFragmentManager();
    // 프래그먼트 트랜잭션 시작
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    // 게시글 목록화면
    FragmentMainActivityHome home;
    // 게시글 상세보기 화면
    FragmentMainActivityBulletinView bulletinView;
    // 마이페이지 화면
    FragmentMainActivityMyPage myPage;

    String id;
    String memberClassification;

    // 하단부
    // 버튼 네비게이션
    BottomNavigationView bottomnav;                             // 하단부 버튼 4개

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getObject();
        init();
    }

    private void getObject() {
        bottomnav = findViewById(R.id.bottom_navigation);
    }

    // 초기설정하는 부분
    private void init() {
        // 주소 입력받기 ( 처음 로그인했을 때만 적용 )
        checkFirstLogin();

        String mypageFragment = getIntent().getStringExtra("fragment");

        if(mypageFragment == null) {
            onFragmentChange("home", null, null, null, null, null, null, null);
        } else if(mypageFragment.equals("mypage")) {
            onFragmentChange("mypage", null, null, null, null, null, null, null);
        }

        // 하단부
        bottomnav.setOnNavigationItemSelectedListener(navListener);
    }

    private void checkFirstLogin() {
        Log.i(this.getClass().getName() + "건호", "로그인 버튼을 클릭하였다");

        id                     = getIntent().getStringExtra("id");
        memberClassification   = getIntent().getStringExtra("memberClassification");


        RequestTaskCheckFirstLogin checkFirstLogin = new RequestTaskCheckFirstLogin(this);
        checkFirstLogin.execute(id, memberClassification);

        boolean completeTask, success;

        while(true) {
            completeTask = checkFirstLogin.getCompleteTask();

            if(completeTask) {
                success = checkFirstLogin.getSuccess();
                if(success) {
                    Log.i(this.getClass().getName(), "처음 로그인 한다.");
                    Intent intent = new Intent(this, AddressSearchActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("memberClassification", memberClassification);
                    intent.putExtra("update", "first");
                    this.startActivity(intent);
                }
                // 이전에 로그인한 이력이 있으므로 주소입력을 하지않는다.
                else {
                    Log.i(this.getClass().getName(), "이전에 로그인을 했던 이력이 있다.");
                }

                break;
            }
        }
    }

    public void onFragmentChange(String mode, String bulletinNumber, String bulletinID, String uploadTime, String currentPrice,
            String content, String bulletinLatitude, String bulletinLongitude) {
        fragmentTransaction = fragmentManager.beginTransaction();

        if(mode.equals("home")) {                   // "게시글 목록" 화면
            home = new FragmentMainActivityHome();
            fragmentTransaction.replace(R.id.frame_container, home);
        } else if(mode.equals("bulletinView")) {    // "게시글 상세보기" 화면
            bulletinView = FragmentMainActivityBulletinView.newInstance(
                    bulletinNumber, bulletinID, uploadTime, currentPrice, content, bulletinLatitude, bulletinLongitude);
            fragmentTransaction.replace(R.id.frame_container, bulletinView);
        } else if(mode.equals("myPage")) {          // "마이 페이지" 화면
            String id = getIntent().getStringExtra("id");
            String memberClassification = getIntent().getStringExtra("memberClassification");
            myPage = FragmentMainActivityMyPage.newInstance(id, memberClassification);
            fragmentTransaction.replace(R.id.frame_container, myPage);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // 하단바 4개의 버튼
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                // 각 버튼을 눌렀을 때 동작하는 부분
                switch (item.getItemId()) {
                    case R.id.navWirteBulletin: // 게시글 쓰기
                        Intent intent = new Intent(this, BulletinWriteActivity.class);
                        intent.putExtra("id", getIntent().getStringExtra("id"));
                        intent.putExtra("memberClassification", getIntent().getStringExtra("memberClassification"));

                        this.startActivity(intent);
                        break;

                    case R.id.navHome: // MainActivity로 가기
                        onFragmentChange("home", null, null, null, null, null, null, null);
                        break;

                    case R.id.navUserInformation: // 유저 정보창으로 이동
                        onFragmentChange("myPage", null, null, null, null, null, null, null);
                        break;
                }
                return true;
            };

    public String getId() { return this.id; }

    public String getMemberClassification() { return this.memberClassification; }

    /*

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
     */
}