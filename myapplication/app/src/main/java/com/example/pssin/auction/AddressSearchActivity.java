package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class AddressSearchActivity extends AppCompatActivity {

    // 주소 데이터 받아오기
    private static final String TAG_RESULT      = "result";
    private static final String TAG_FULL_ADDR   = "roadFullAddr";
    private static final String TAG_ADDR_PART1  = "roadAddrPart1";
    private static final String TAG_ADDR_PART2  = "roadAddrPart2";

    // 주소 웹뷰 띄우기
    private WebView webView;
    private Handler handler;
    Geocoder geocoder;
    Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder  = new Geocoder(this);
        activity  = this;

        setContentView(R.layout.activity_address_register);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView() {
        // WebView 설정
        webView = findViewById(R.id.web_view);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        webView.addJavascriptInterface(new AndroidBridge(), "Auction");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        // webview url load
        webView.loadUrl("http://pssin1.cafe24.com/address/addressSearchPopUp.php");
//        webView.loadUrl("http://pssin1.cafe24.com/address/test1.asp");
    }

    // 이 함수는 addressPopUp.php 파일에서 [line 62]window.Auction.setAddress()로 호출됨
    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String roadFullAddr, final String roadAddrPart1, final String roadAddrPart2) {
            handler.post(() -> {
                gotoAddressRegisterRequest(roadAddrPart1, roadFullAddr);
                // WebView를 초기화 하지않으면 재사용할 수 없음
                //init_webView();
            });
        }

        private Double[] translateAddress(String roadAddrPart1) {

            List<Address> list = null;
            Double[] result = new Double[2];
            result[0] = result[1] = 0.0;

            String str = roadAddrPart1;
            try {
                list = geocoder.getFromLocationName(
                        str, // 지역 이름
                        10); // 읽을 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
            }

            if (list != null) {
                if (list.size() == 0) {
                    Log.e("건호", "해당되는 주소 정보는 없습니다");
                } else {
                    result[0] = list.get(0).getLatitude();
                    result[1] = list.get(0).getLongitude();
                    //          list.get(0).getCountryName();  // 국가명
                    //          list.get(0).getLatitude();        // 위도
                    //          list.get(0).getLongitude();    // 경도
                    return result;
                }
            }

            return result;
        }

        private void gotoAddressRegisterRequest(String address, String fullAddress) {
            Double[] translatedAddress = translateAddress(address);
            Double latitude  = translatedAddress[0];
            Double longitude = translatedAddress[1];
            Log.i("건호", "위도 : " + latitude);
            Log.i("건호", "경도 : " + longitude);
            String id                   = getIntent().getStringExtra("id");
            String memberClassification = getIntent().getStringExtra("memberClassification");
            String update               = getIntent().getStringExtra("update");

            RequestTaskAddressRegister addressRegister = new RequestTaskAddressRegister(activity);
            addressRegister.execute(id, address, fullAddress, memberClassification, String.valueOf(latitude), String.valueOf(longitude), update);
        }
    }
}
