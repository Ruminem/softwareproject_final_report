package com.example.pssin.auction;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class FragmentMainActivityBulletinView extends Fragment implements OnMapReadyCallback{
    private final String NAVER_CLIENT_ID = "yqyp1ld5u1";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

    ScrollView mScrollView;
    LinearLayout mapLayout;
    LinearLayout explainLayout;

    double bulletinLatitude;
    double bulletinLongitude;

    TextView idText;
    TextView uploadTimeText;
    TextView currentPriceText;
    TextView contentText;

    String bulletinNumber;
    String bulletinID;
    String uploadTime;
    String currentPrice;
    String content;

    /* 입찰하기 부분 */
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    String inputBidPrice = "";
    String inputBidPrice_ = "";
    Button bidButton;

    // bidHistory에 담길 것들
    String jibunAddress = "";
    String latitude     = "";
    String longitude    = "";
    String bidComment   = "";
    String phoneNumber  = "";
    String companyName  = "";

    List<BidHistoryItem> mBidHistoryItems = new ArrayList<>();        // 입찰내역 데이터를 저장하기 위함
    BidHistoryAdapter mBidHistoryAdapter;                             // mBidHistoryItems를 리스트뷰에 출력하기 위함
    ListView listView;                                                // 입찰내역이 담길 리스트뷰
    RequestTaskBidHistoryList getBidHistoryList;
    /* ------------------------- */

    MainActivity activity;

    @SuppressLint("StaticFieldLeak")
    static View rootView; // 프래그먼트의 뷰 인스턴스

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    static FragmentMainActivityBulletinView newInstance(
            String bulletinNumber,
            String bulletinID,
            String uploadTime,
            String currentPrice,
            String content,
            String bulletinLatitude,
            String bulletinLongitude) {
        FragmentMainActivityBulletinView fragment = new FragmentMainActivityBulletinView();
        Bundle args = new Bundle();
        args.putString("bulletinNumber", bulletinNumber);
        args.putString("bulletinID", bulletinID);
        args.putString("uploadTime", uploadTime);
        args.putString("currentPrice", currentPrice);
        args.putString("content", content);
        args.putString("bulletinLatitude", bulletinLatitude);
        args.putString("bulletinLongitude", bulletinLongitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_main_bulletin_view , container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getObject();                // 변수, ID 매칭

        onRequestTaskBidHistory();  // 입찰내역 불러오는 태스크 호출

        setVisableBidButton();      // 일반회원 : "입찰하기"버튼 비활성화, 기업회원 : " 입찰하기"버튼 활성화

        init();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
    }

    // 입찰내역 불러오는 태스크 호출
    private void onRequestTaskBidHistory() {
        getBidHistoryList = new RequestTaskBidHistoryList(this);
        getBidHistoryList.execute(bulletinNumber);
    }

    // 해당 게시글의 입찰내역 불러오기
    void getBidHistoryList(String response) {
        try {
            mBidHistoryItems = new ArrayList<>();
            mBidHistoryAdapter = new BidHistoryAdapter(activity.getApplicationContext(), mBidHistoryItems);
            listView.setAdapter(mBidHistoryAdapter);

            Log.i(this.getClass().getName() + "거노, ", "입찰내역 불러오기 도전!");
            Log.i(this.getClass().getName(), response + '\n');
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int lengthJS = jsonArray.length();
            int count = 0;

            String bulletinNumber;  // 게시글 번호
            String id;              // 입찰자 아이디
            String bidPrice;        // 입찰가
            String address;         // 지번주소
            String latitude;        // 경도
            String longitude;       // 위도
            String comment;         // 댓글
            String phoneNumber;     // 입찰자 휴대폰번호
            String companyName;     // 가게 이름

            while(count < lengthJS)
            {
                JSONObject object = jsonArray.getJSONObject(count);

                bulletinNumber  = object.getString("bulletinNumber");
                id              = object.getString("id");
                bidPrice        = object.getString("bidPrice");
                address         = object.getString("address");
                latitude        = object.getString("latitude");
                longitude       = object.getString("longitude");
                comment         = object.getString("comment");
                phoneNumber     = object.getString("phoneNumber");
                companyName     = object.getString("companyName");

                BidHistoryItem bidHistoryItem = new BidHistoryItem(bulletinNumber, id, bidPrice, address, latitude, longitude, comment,
                        phoneNumber, companyName);
                mBidHistoryItems.add(bidHistoryItem);

                count++;
            }
            setListViewHeightBasedOnChildren(listView);

            // 네이버 지도를 띄움
            initMap();
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "에러");
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void getObject() {
        mScrollView         = rootView.findViewById(R.id.scrollArea);

        mapLayout           = rootView.findViewById(R.id.mapLayout);
        explainLayout       = rootView.findViewById(R.id.explainLayout);

        idText              = rootView.findViewById(R.id.id);
        uploadTimeText      = rootView.findViewById(R.id.uploadTime);
        currentPriceText    = rootView.findViewById(R.id.currentPrice);
        contentText         = rootView.findViewById(R.id.content);

        listView            = rootView.findViewById(R.id.bidHistory);        // 입찰내역 ListView
//        listView.setOnTouchListener((v, event) -> {
//            mScrollView.requestDisallowInterceptTouchEvent(true);
//            return false;
//        });


        bidButton           = rootView.findViewById(R.id.bidButton);
    }

    // 스크롤 뷰 내 리스트뷰의 높이를 자동으로 조정해주는 함수
    private void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 50;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    // 일반회원이라면 "입찰하기" 버튼을 가리기
    private void setVisableBidButton() {
        String memberClassification = activity.getMemberClassification();
        if(memberClassification.equals("normal")) {
            bidButton.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void init() {
        idText.setText("아이디 : " + bulletinID);
        uploadTimeText.setText("작성시간 : " + uploadTime);
        currentPriceText.setText("최저 입찰액 : " + String.format("%,d", Integer.parseInt(currentPrice)) + "원");
        contentText.setText("\n\n\n\n" + content);

        bidButton.setOnClickListener(v -> onClickBidButton());

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})

    private void onClickBidButton() {
        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog dialog = new Dialog(activity); // 다이얼로그 객체 생성
        dialog.setContentView(R.layout.dialog_main_bulletin_view_bid_button); // 다이얼로그 화면 등록

        // 객체얻어오기
        // 게시글 상세보기창에서 입력한 액수를 지정
        TextView currentBidPriceTextView    = dialog.findViewById(R.id.currentBidPrice);
        TextView currentTimeTextView        = dialog.findViewById(R.id.currentTime);
        EditText inputBidPriceEditText      = dialog.findViewById(R.id.inputBidPrice);

        EditText bidCommentEditText         = dialog.findViewById(R.id.bidComment);
        Button confirmButton                = dialog.findViewById(R.id.confirmButton);
        Button cancelButton                 = dialog.findViewById(R.id.cancelButton);

        inputBidPriceEditText.addTextChangedListener(setInputBidPriceTextChanger(inputBidPriceEditText));
        currentBidPriceTextView.setText("현재 입찰액 : " + String.format("%,d", Integer.parseInt(currentPrice)) + "원");
        setCurrentTime(currentTimeTextView);

        confirmButton.setOnClickListener(v -> {
            bidComment = bidCommentEditText.getText().toString();
            getCompanyMemberInformation();

            dialog.hide(); // 다이얼로그 객체를 화면에서만 제거
            dialog.dismiss(); // 다이얼로그 객체를 제거
        });

        cancelButton.setOnClickListener(v -> {
            dialog.hide(); // 다이얼로그 객체를 화면에서만 제거
            dialog.dismiss(); // 다이얼로그 객체를 제거
        });

        dialog.show(); // 다이얼로그 띄우기

        // Activity 에 Dialog 를 등록하기
        dialog.setOwnerActivity(activity);

        dialog.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 영역을 클릭시
        // 종료할 것인지 여부, false : 종료안됨
    }



    private TextWatcher setInputBidPriceTextChanger(EditText inputBidPriceEditText) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(inputBidPrice)){
                    inputBidPrice  = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    inputBidPrice_ = inputBidPrice.replace(",", "");


                    // 현재 입찰하려는 금액이 현재 최저입찰액보다 높은 경우
                    // 현재 최저입찰액 - 1 원으로 고정(치환)시킴
                    if(Integer.parseInt(inputBidPrice_) >= Integer.parseInt(currentPrice)
                            && Integer.parseInt(currentPrice) > 0) {
                        inputBidPrice = decimalFormat.format(Double.parseDouble(String.valueOf(Integer.parseInt(currentPrice) - 1).replaceAll(",","")));
                        inputBidPrice_ = inputBidPrice.replace(",", "");
                    }

                    inputBidPriceEditText.setText(inputBidPrice);
                    inputBidPriceEditText.setSelection(inputBidPrice.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        return watcher;
    }

    private void getCompanyMemberInformation() {
        RequestTaskMemberInformation getMemberInformation = new RequestTaskMemberInformation(activity);
        getMemberInformation.execute(activity.getMemberClassification(), activity.getId());

        boolean completeTask, success;
        while(true) {
            Log.i(this.getClass().getName(), "getCompanyMemberInformation() 실행중");
            completeTask = getMemberInformation.getCompleteTask();

            if(completeTask) {
                success = getMemberInformation.getSuccess();
                if(success) {
                    companyName     = getMemberInformation.getCompanyName();
                    jibunAddress    = getMemberInformation.getAddress();
                    phoneNumber     = getMemberInformation.getPhoneNumber();
                    latitude        = getMemberInformation.getLatitude();
                    longitude       = getMemberInformation.getLongitude();

                    updateBidInformation();
                } else {
                    Log.e(this.getClass().getName(), "getCompanyMemberInformation() 실패");
                }
                break;
            }
        }
    }

    private void updateBidInformation() {
        RequestTaskUpdateBidPrice updateBidPrice = new RequestTaskUpdateBidPrice(activity);
        updateBidPrice.execute(bulletinNumber, activity.getId(), inputBidPrice_, jibunAddress,
                latitude, longitude, bidComment, phoneNumber, companyName);

        boolean completeTask, success;
        while(true) {
            Log.i(this.getClass().getName(), "updateBidInformation() 실행중");
            completeTask = updateBidPrice.getCompleteTask();

            if(completeTask) {
                success = updateBidPrice.getSuccess();
                if(success) {
                    Log.i(this.getClass().getName(), "입찰 성공!");
                    currentPrice = inputBidPrice_;
                    activity.onFragmentChange("bulletinView", bulletinNumber, bulletinID, uploadTime,
                            currentPrice, content, String.valueOf(bulletinLatitude), String.valueOf(bulletinLongitude));
                } else {
                    Log.e(this.getClass().getName(), "입찰 실패");
                }
                break;
            }
        }
    }

    private void setCurrentTime(TextView currentTimeTextView) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat ("yyyy년 MM월dd일 HH시mm분ss초");
        Date time = new Date();
        String timeString = format.format(time);
        currentTimeTextView.setText(timeString);
    }

    private void onErrorDialog(String message) {
        // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
        final Dialog dialog = new Dialog(activity); // 다이얼로그 객체 생성
        dialog.setContentView(R.layout.dialog_bid_error); // 다이얼로그 화면 등록

        // 객체얻어오기
        // 게시글 상세보기창에서 입력한 액수를 지정
        TextView errorMessage = dialog.findViewById(R.id.errorMessage);
        errorMessage.setText(message);
        // 확인 버튼을 누르면 현재 입찰액을 갱신
        Button confirmButton = dialog.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            dialog.hide(); // 다이얼로그 객체를 화면에서만 제거
            dialog.dismiss(); // 다이얼로그 객체를 제거
        });

        dialog.show(); // 다이얼로그 띄우기

        // Activity 에 Dialog 를 등록하기
        dialog.setOwnerActivity(activity);

        dialog.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 영역을 클릭시
        // 종료할 것인지 여부, false : 종료안됨
    }

    private void getArgs() {
        Bundle args = getArguments();
        if (args != null) {
            bulletinNumber      = args.getString("bulletinNumber");
            bulletinID          = args.getString("bulletinID");
            uploadTime          = args.getString("uploadTime");
            currentPrice        = args.getString("currentPrice");
            content             = args.getString("content");
            bulletinLatitude    = Double.parseDouble(args.getString("bulletinLatitude"));
            bulletinLongitude   = Double.parseDouble(args.getString("bulletinLongitude"));
        }
    }

    /* fragment_naver_map.xml 관련 */
    private void initMap() {
        NaverMapSdk.getInstance(activity).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID));

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.fragment_naver_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentTransaction.add(R.id.mapLayout, mapFragment).commit();
        }

        mapFragment.getMapAsync(this); // 맵 프래그먼트에 네이버 지도를 싱크
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
        InfoWindow infoWindow = new InfoWindow();
        List<Marker> companyMarker = new ArrayList<>();

        // UI
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);  // 현재위치 버튼 활성화
        uiSettings.setLogoClickEnabled(false);

        // MAP
        naverMap.setSymbolScale(1); // 주변 건물 아이콘, 글자 크기(default : 1)
        naverMap.setMapType(NaverMap.MapType.Basic); // 지도 타입 (위성, 일반지도, ...)
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.None); // 위치추적기능 활성화
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(bulletinLatitude, bulletinLongitude));
        naverMap.moveCamera(cameraUpdate);

        // 지도에 띄울 마커
        // 1. 게시글 작성할 때 지정했던 위치
        bulletinMarker.setPosition(new LatLng(bulletinLatitude, bulletinLongitude));
        bulletinMarker.setMap(naverMap);

        // 2. 입찰한 가게들 띄워주는 마커
        // 입찰내역에 있는 각 좌표들을 리스트에 추가하고
        for(BidHistoryItem item : mBidHistoryItems) {
            double latitude  = Double.parseDouble(item.getLatitude());
            double longitude = Double.parseDouble(item.getLongitude());
            Marker marker = new Marker();
            marker.setPosition(new LatLng(latitude, longitude));
            companyMarker.add(marker);
        }

        // 추가된 리스트를 맵에 출력
        for(Marker marker : companyMarker) {
            Log.i(this.getClass().getName(), "기업 마커 좌표 : " + marker.getPosition());
            marker.setIconTintColor(Color.RED);
            marker.setMap(naverMap);
            marker.setOnClickListener(v -> clickMarker());
        }

        // 3. 입찰내역 리스트 뷰에서 특정 입찰내역을 터치했을 때 해당 마커를 활성화
        listView.setOnItemClickListener((parent, v, position, id) -> {
            Log.e("마커활성화?", "마커활성화 : " + companyMarker.size());
            setInfoWindow(infoWindow, companyMarker.get(position), mBidHistoryItems.get(position).getCompanyName() + '\n' +
                    mBidHistoryItems.get(position).getAddress());
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("롱클릭", "롱클릭 : " + companyMarker.size());

                // Dialog 다이얼로그 클래스로 다이얼로그를 만든다
                final Dialog dialog = new Dialog(activity); // 다이얼로그 객체 생성
                dialog.setContentView(R.layout.dialog_main_bulletin_view_winning_bid_view); // 다이얼로그 화면 등록

                TextView winningBidICompanyName    = dialog.findViewById(R.id.winningBidICompanyName);
                TextView winningBidPrice = dialog.findViewById(R.id.winningBidPrice);
                Button winningconfirmButton = dialog.findViewById(R.id.winningconfirmButton);
                Button winningcancelButton = dialog.findViewById(R.id.winningcancelButton);

                winningBidICompanyName.setText(mBidHistoryItems.get(position).getCompanyName());
                winningBidPrice.setText(mBidHistoryItems.get(position).getBidPrice());

                winningconfirmButton.setOnClickListener(v -> {
                    // bidhistory bulletinBoard 게시글을 각각 비활성화후
                    // bidhistory는 게시글번호 id 최저가격으로 찾는다.
                    // 그리고 낙찰관리하는 페이지를 들어가서 정보를 갱신해주면된다.
                    // 낙찰시 현재 게시글와 낙찰사람 매칭 해주기위해 DB저장
                    // 저장해야될값은.. 가게위치 낙찰가격 핸드폰번호 코멘트
                    // 부가적으로 보여주면 좋은것은 1:1 대화나 쪽지기능 ^^

                    //winningBidinformation은 낙찰한다는 의미로 DB상에 게시글과 입찰자들의 값을 0->1 값으로 만들어준다 즉 "1"은 true 낙찰이 된 상황
                    winningBidinformation(mBidHistoryItems.get(position).getId(),mBidHistoryItems.get(position).getBidPrice());


                    dialog.hide(); // 다이얼로그 객체를 화면에서만 제거
                    dialog.dismiss(); // 다이얼로그 객체를 제거
                });

                winningcancelButton.setOnClickListener(v -> {
                    dialog.hide(); // 다이얼로그 객체를 화면에서만 제거
                    dialog.dismiss(); // 다이얼로그 객체를 제거
                });

                dialog.show(); // 다이얼로그 띄우기

                // Activity 에 Dialog 를 등록하기
                dialog.setOwnerActivity(activity);

                dialog.setCanceledOnTouchOutside(false); // 다이얼로그 바깥 영역을 클릭시

                return true;
            }
        });
        Log.e("테스트입니다.", "기업마커 길이 : " + companyMarker.size());


        // 지도 화면을 드래그중일 때 스크롤 뷰의 스크롤 기능을 막음
        naverMap.addOnCameraChangeListener((i, b) -> {
            Log.i("naverMap","addOnCameraChangeListener");
        });

        // 지도 화면에서 드래그가 끝나고 손가락을 뗏을 때 동작하는 부분
        naverMap.addOnCameraIdleListener(() -> {
            Log.i("naverMap", "addOnCameraIdleListeneer");
        });

        //marker.setOnClickListener(v -> clickMarker(naverMap.getCameraPosition().target));


        // 현재 위치가 변경되었을 떄 동작하는 부분 (기능 없음)
        naverMap.addOnLocationChangeListener(location -> {});
        // 지도의 특정 위치를 한번 터치했을 떄 동작하는 부분 (기능 없음)
        naverMap.setOnMapClickListener((point, coord) -> {});
        // 지도의 특정 위치를 길게 터치했을 때 동작하는 부분 (기능 없음)
        naverMap.setOnMapLongClickListener((point, coord) -> {});
    }

    private void winningBidinformation(String id, String price){
        RequestTaskWinningBid winningBid = new RequestTaskWinningBid(activity);
        winningBid.execute(bulletinNumber,id,price);

        boolean completeTask, success;
        while(true){
            completeTask = winningBid.getCompleteTask();

            if(completeTask){
                success = winningBid.getSuccess();
                if(success){
                    Log.e(this.getClass().getName(),"낙찰 성공");
                    //Intent intent = new Intent(activity, Mypage_Bid_History.class);
                    //activity.startActivity(intent);
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", bulletinID);
                    intent.putExtra("memberClassification", "normal");
                    startActivity(intent);

                }else{
                    Log.e(this.getClass().getName(),"낙찰 실패");
                }
                break;
            }
        }
    }

    private boolean clickMarker() {
        return true;
    }

    private void setInfoWindow(InfoWindow infoWindow, Marker marker, String message) {
        infoWindow.close();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(activity) {
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
}
