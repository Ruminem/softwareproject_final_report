package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentMainActivityHome extends Fragment {

    // 상단부 툴바
    Toolbar toolbar;                                                        // 툴바 ?? (아직 활용방도 모름)
    EditText keyword;                                                       // 키워드 검색창
    // 중단부 게시글 목록
    List<BulletinListViewItem> bulletinListData = new ArrayList<>();        // 게시글 목록 데이터를 저장하기 위함
    BulletinListViewAdapter bulletinListAdapter;                            // bulletinListData들을 리스트뷰에 출력하기 위함
    ListView listView;                                                      // 게시글 목록이 담길 리스트뷰
    Thread bulletinListThread;
    RequestTaskBulletinList getBulletinList;

    MainActivity activity;
    @Override
    public void onAttach(@NonNull Context context) {
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

    @Override
    public void onViewCreated(@NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_home , container, false);

        getObject(rootView);


        // 상단부
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        activity.setSupportActionBar(toolbar);
        setKeywordProcess();

        // 중단부
        // 게시글 목록 출력
        onRequestTaskBidHistory("");

        // 게시글 목록중 특정 게시글을 터치하면 해당 게시글로 프래그먼트를 변경
        listView.setOnItemClickListener((parent, v, position, id) -> {
            getBulletinList.setRunningState(false);
            String bulletinNumber       = bulletinListData.get(position).getBulletinNumber();
            String bulletinID           = bulletinListData.get(position).getbulletinID();
            String uploadTime_          = bulletinListData.get(position).getUploadTime();
            String currentPrice_        = bulletinListData.get(position).getCurrentPrice();
            String content_             = bulletinListData.get(position).getContent();
            String bulletinLatitude     = bulletinListData.get(position).getLatitude();
            String bulletinLongitude    = bulletinListData.get(position).getLongitude();
            activity.onFragmentChange("bulletinView", bulletinNumber, bulletinID, uploadTime_,
                    currentPrice_, content_, bulletinLatitude, bulletinLongitude);
        });

        return rootView;
    }

    private void getObject(ViewGroup rootView) {
        toolbar   = rootView.findViewById(R.id.toolbar);             // 툴바설정
        keyword   = rootView.findViewById(R.id.keyword);             // 검색창 EditText
        listView  = rootView.findViewById(R.id.bulletinList);        // 게시글 목록 ListView
    }

    // 게시글 목록 불러오기
    private void onRequestTaskBidHistory(String keyword) {
        getBulletinList = new RequestTaskBulletinList(this);
        getBulletinList.execute(keyword);
    }

    void getBulletinList(String response, String keyword) {
        bulletinListThread = new Thread() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                bundle.putString("keyword", keyword);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };
        bulletinListThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            // 원래 하고싶었던 일들 (UI변경작업 등...)
            try {
                Bundle bundle   = msg.getData();
                String response = bundle.getString("response");
                String keyword  = bundle.getString("keyword");
                bulletinListData = new ArrayList<>();
                bulletinListAdapter = new BulletinListViewAdapter(activity.getApplicationContext(), bulletinListData);
                listView.setAdapter(bulletinListAdapter);

                Log.i(this.getClass().getName() + "거노, ", "목록 불러오기 도전!");
                Log.i(this.getClass().getName(), response + '\n');
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                String a = String.valueOf(jsonArray.length());
                int lengthJS = jsonArray.length();
                int count = 0;

                String bulletinNumber;  // 게시글 번호
                String bulletinID;      // 게시글 작성자 아이디
                String content;         // 내용
                String uploadTime;      // 게시글 작성시간
                String timeLimit;       // 입찰 제한시간
                String countdown;       // 남은 입찰시간
                String startPrice;      // 입찰시작가
                String currentPrice;    // 현재 입찰가
                String latitude;        // 위도
                String longitude;       // 경도
                String address;         // 지번 주소

                while(count < lengthJS)
                {
                    JSONObject object = jsonArray.getJSONObject(count);

                    bulletinNumber  = object.getString("bulletinNumber");
                    bulletinID      = object.getString("id");
                    content         = object.getString("content");
                    uploadTime      = object.getString("uploadTime");
                    timeLimit       = object.getString("timeLimit");
                    countdown       = object.getString("countdown");
                    startPrice      = object.getString("startPrice");
                    currentPrice    = object.getString("currentPrice");
                    latitude        = object.getString("latitude");
                    longitude       = object.getString("longitude");
                    address         = object.getString("address");

                    // 남은 입찰시간이 0초 이상인 게시글만 목록에 출력
                    int remainTime = Integer.parseInt(countdown);
                    if(remainTime > 0) {
                        if(keyword == null) {
                            BulletinListViewItem bulletin = new BulletinListViewItem(bulletinNumber, bulletinID, content, uploadTime, timeLimit, countdown, startPrice, currentPrice,
                                    latitude, longitude, address);
                            bulletinListData.add(bulletin);
                        } else {
                            if(address.contains(keyword)) {
                                BulletinListViewItem bulletin = new BulletinListViewItem(bulletinNumber, bulletinID, content, uploadTime, timeLimit, countdown, startPrice, currentPrice,
                                        latitude, longitude, address);
                                bulletinListData.add(bulletin);
                            }
                        }
                    }
                    count++;
                }
            } catch (JSONException e) {
                Log.e(this.getClass().getName(), "에러");
                e.printStackTrace();
            }
        }
    };

    private void setKeywordProcess() {
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                // 게시글 목록을 키워드에 해당하는 것만 리로드
                onRequestTaskBidHistory(keyword.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    public void setRunningState(boolean state) { getBulletinList.setRunningState(state); }
}