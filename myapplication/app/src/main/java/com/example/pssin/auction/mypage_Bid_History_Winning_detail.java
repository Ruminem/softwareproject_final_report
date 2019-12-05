package com.example.pssin.auction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class mypage_Bid_History_Winning_detail extends AppCompatActivity implements
        OnMapReadyCallback {
    private final String NAVER_CLIENT_ID = "yqyp1ld5u1";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);;

    private TextView companyNameText;
    private TextView currentPriceText;
    private TextView mycontentText;
    private TextView startPriceText;
    private TextView youraddressText;
    private TextView yourcommentText;
    private TextView yourphoneNumberText;

    private String companyName;
    private String currentPrice;
    private String mycontent;
    private String startPrice;
    private String youraddress;
    private String yourcomment;
    private String yourlatitude;
    private String yourlongitude;
    private String yourphoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage__bid__history__winning_detail);

        initMap();

        Intent intent = getIntent();


        companyName = intent.getExtras().getString("companyName");
        currentPrice = intent.getExtras().getString("currentPrice");
        mycontent = intent.getExtras().getString("mycontent");
        startPrice = intent.getExtras().getString("startPrice");
        youraddress = intent.getExtras().getString("youraddress");
        yourcomment = intent.getExtras().getString("yourcomment");
        yourlatitude = intent.getExtras().getString("yourlatitude");
        yourlongitude = intent.getExtras().getString("yourlongitude");
        yourphoneNumber = intent.getExtras().getString("yourphoneNumber");

        companyNameText = findViewById(R.id.companyName);
        currentPriceText = findViewById(R.id.currentPrice);
        mycontentText = findViewById(R.id.mycontent);
        startPriceText = findViewById(R.id.startPrice);
        youraddressText =findViewById(R.id.youraddress);
        yourcommentText =findViewById(R.id.yourcomment);
        yourphoneNumberText = findViewById(R.id.yourphoneNumber);

        companyNameText.setText(companyName);
        currentPriceText.setText(currentPrice);
        mycontentText.setText(mycontent);
        startPriceText.setText(startPrice);
        youraddressText.setText(youraddress);
        yourcommentText.setText(yourcomment);
        yourphoneNumberText.setText(yourphoneNumber);
    }



    /* fragment_naver_map.xmlap.xml 관련 */
    private void initMap() {
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID));

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.fragment_naver_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.mapLayout, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 마커와 정보창 변수선언
        Marker bulletinMarker = new Marker();

        // UI
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);  // 현재위치 버튼 활성화
        uiSettings.setLogoClickEnabled(false);

        // MAP
        naverMap.setSymbolScale(1); // 주변 건물 아이콘, 글자 크기(default : 1)
        naverMap.setMapType(NaverMap.MapType.Basic); // 지도 타입 (위성, 일반지도, ...)
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.None); // 위치추적기능 활성화
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(Double.parseDouble(yourlatitude), Double.parseDouble(yourlongitude)));
        naverMap.moveCamera(cameraUpdate);

        // 지도에 띄울 마커
        // 1. 게시글 작성할 때 지정했던 위치
        bulletinMarker.setPosition(new LatLng(Double.parseDouble(yourlatitude), Double.parseDouble(yourlongitude)));
        bulletinMarker.setMap(naverMap);
    }
}
