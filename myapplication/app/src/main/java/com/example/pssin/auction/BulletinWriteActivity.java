package com.example.pssin.auction;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BulletinWriteActivity extends AppCompatActivity implements OnMapReadyCallback{
    private final String NAVER_CLIENT_ID = "yqyp1ld5u1";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);;


    CustomScrollView mCustomScrollView;
    LinearLayout mapLayout;

    TextView locationText;
    SeekBar timeLimit;
    TextView timeLimitText;
    Button commitButton;
    Button cancelButton;

    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    EditText startPriceText;
    String startPriceResult = "";
    String startPriceText_ = "";

    EditText contentText;

    LatLng coordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_bulletin);

        getObject();

        // 현재 레이아웃에 프래그먼트를 삽입
        init();

        // 네이버 지도를 띄움
        initMap();

        commitButton.setOnClickListener(v -> clickCommitButton());
        cancelButton.setOnClickListener(v -> clickCancelButton());
    }

    private void clickCommitButton() {
        String id                   = getIntent().getStringExtra("id");
        String content              = contentText.getText().toString();
        String timeLimit            = timeLimitText.getText().toString();
        String startPrice           = startPriceText_;
        String currentPrice         = startPrice;
        String latitude             = String.valueOf(coordinate.latitude);
        String longitude            = String.valueOf(coordinate.longitude);
        String address              = locationText.getText().toString();
        String memberClassification = getIntent().getStringExtra("memberClassification");

        RequestTaskWriteBulletin write = new RequestTaskWriteBulletin(this);
        write.execute(id, content, timeLimit, startPrice, currentPrice, latitude, longitude, address, memberClassification);
    }

    private void clickCancelButton() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", getIntent().getStringExtra("id"));
        intent.putExtra("memberClassification", getIntent().getStringExtra("memberClassification"));
        this.startActivity(intent);
    }

    // 각 변수들을 레이아웃의 id값과 매칭
    private void getObject() {
        mCustomScrollView = findViewById(R.id.customScrollView);
        mapLayout         = findViewById(R.id.mapLayout);

        locationText      = findViewById(R.id.location);
        startPriceText    = findViewById(R.id.startPrice);
        timeLimit         = findViewById(R.id.timeLimit);
        timeLimitText     = findViewById(R.id.timeLimitText);
        commitButton      = findViewById(R.id.commitButton);
        cancelButton      = findViewById(R.id.cancelButton);

        contentText       = findViewById(R.id.content);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        // 입찰 시작가 EditText 관련 부분
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(startPriceResult)){
                    startPriceResult = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    startPriceText.setText(startPriceResult);
                    startPriceText.setSelection(startPriceResult.length());
                    startPriceText_ = startPriceResult.replace(",", "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
        startPriceText.addTextChangedListener(watcher);

        // 입찰 제한시간 SeekBar 관련 부분
        timeLimitText.setText(timeLimit.getProgress() + " 시간"); // 초기화
        timeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                timeLimitText.setText(i + " 시간");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(BulletinWriteActivity.this,
//                        "Seekbar touch started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(BulletinWriteActivity.this,
//                        "Seekbar touch stopped", Toast.LENGTH_SHORT).show();
            }
        });
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
        // UI
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);  // 현재위치 버튼 활성화
        uiSettings.setLogoClickEnabled(false);

        // MAP
        naverMap.setSymbolScale(1); // 주변 건물 아이콘, 글자 크기(default : 1)
        naverMap.setMapType(NaverMap.MapType.Basic); // 지도 타입 (위성, 일반지도, ...)
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow); // 위치추적기능 활성화

        // 지도에 띄울 마커
        Marker marker = new Marker();

        // 지도에 띄울 정보 창
        InfoWindow infoWindow = new InfoWindow();


        // 지도 화면을 드래그중일 때 스크롤 뷰의 스크롤 기능을 막음
        naverMap.addOnCameraChangeListener((i, b) -> {
            Log.i("naverMap","addOnCameraChangeListener");
            mCustomScrollView.setScrolling(false);
            LatLng coord = naverMap.getCameraPosition().target;
            marker.setPosition(coord);
            marker.setMap(naverMap);
            setInfoWindow(infoWindow, marker, "");
        });

        // 지도 화면에서 드래그가 끝나고 손가락을 뗏을 때 동작하는 부분
        naverMap.addOnCameraIdleListener(() -> {
            Log.i("naverMap", "addOnCameraIdleListeneer");
            mCustomScrollView.setScrolling(true);
            LatLng coord = naverMap.getCameraPosition().target;
            setInfoWindow(infoWindow, marker, getJibunAddress(coord) + "\n(여기를 터치하면 현재 위치로 결정)"); // 해당 마커에 지번주소를 InfoWindow로 띄움
        });

        marker.setOnClickListener(v -> clickMarker(naverMap.getCameraPosition().target));


        // 현재 위치가 변경되었을 떄 동작하는 부분 (기능 없음)
        naverMap.addOnLocationChangeListener(location -> {});
        // 지도의 특정 위치를 한번 터치했을 떄 동작하는 부분 (기능 없음)
        naverMap.setOnMapClickListener((point, coord) -> {});
        // 지도의 특정 위치를 길게 터치했을 때 동작하는 부분 (기능 없음)
        naverMap.setOnMapLongClickListener((point, coord) -> {});
    }

    private boolean clickMarker(LatLng coord) {
        locationText.setText(getJibunAddress(coord));
        coordinate = coord;
        return true;
    }

    private void setInfoWindow(InfoWindow infoWindow, Marker marker, String message) {
        infoWindow.close();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return message;
            }
        });

        if(message.equals("")) {
            infoWindow.setAlpha(0.0f);
        } else {
            infoWindow.setAlpha(1.0f);
        }
        infoWindow.open(marker);
    }

    private String getJibunAddress(LatLng coord) {
        final Geocoder geocoder = new Geocoder(this);
        String result = "";
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(
                    coord.latitude, // 위도
                    coord.longitude, // 경도
                    10); // 얻어올 값의 개수
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("건호", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                Log.e("건호", "현재 경위도에 해당하는 주소 정보가 없습니다.");
            } else {
                String address = list.get(0).getAddressLine(0);
                boolean success = false;
                int i = 0;
                for(;i < address.length(); ++i) {
                    if(address.charAt(i) == '읍' || address.charAt(i) == '면'
                    || address.charAt(i) == '동' || address.charAt(i) == '가') {
                        success = true;
                        break;
                    }
                }

                if(success) {
                    Log.i("건호", list.get(0).toString());
                    result = list.get(0).getAddressLine(0).substring(5, i + 1);
                } else {
                    result = "default";
                }
                //Toast.makeText(this, list.get(0).getAddressLine(0).substring(5, i + 1),
                //        Toast.LENGTH_SHORT).show();
            }
        }
        return result;
    }
}