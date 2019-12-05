package com.example.pssin.auction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Mypage_Bid_History extends AppCompatActivity {
    String myid;
    ListView WinninglistView;
    WinningHistoryAdapter mWinningHistoryAdapter;
    List<WinningHistoryItem> mWinningHistoryItems;
    RequestTaskWinningHistoryList getWinningHistoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage__bid__history);
        WinninglistView = (ListView)findViewById(R.id.winningList);

        Intent intent = getIntent();
        myid = intent.getExtras().getString("id");
        Log.i(this.getClass().getName() + "                             id ", myid);
        Log.i(this.getClass().getName() + "                             id ","wjdals0471");
        getWinningHistoryList();

        WinninglistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                Intent intent2 = new Intent(getApplicationContext(),mypage_Bid_History_Winning_detail.class);
                intent2.putExtra("companyName",mWinningHistoryItems.get(position).getcompanyName());
                intent2.putExtra("currentPrice",mWinningHistoryItems.get(position).getcurrentPrice());
                intent2.putExtra("mycontent",mWinningHistoryItems.get(position).getmycontent());
                intent2.putExtra("startPrice",mWinningHistoryItems.get(position).getstartPrice());
                intent2.putExtra("youraddress",mWinningHistoryItems.get(position).getyouraddress());
                intent2.putExtra("yourcomment",mWinningHistoryItems.get(position).getyourcomment());
                intent2.putExtra("yourlatitude",mWinningHistoryItems.get(position).getyourlatitude());
                intent2.putExtra("yourlongitude",mWinningHistoryItems.get(position).getyourlongitude());
                intent2.putExtra("yourphoneNumber",mWinningHistoryItems.get(position).getyourphoneNumber());

                startActivity(intent2);
            }
        });
    }


    public void getWinningHistoryList() {
        getWinningHistoryList = new RequestTaskWinningHistoryList(this);
        getWinningHistoryList.execute(myid);

    }
    void getWinningHistoryList(String response) {
        try {

            mWinningHistoryItems = new ArrayList<>();
            mWinningHistoryAdapter = new WinningHistoryAdapter(this, mWinningHistoryItems);
            WinninglistView.setAdapter(mWinningHistoryAdapter);

            Log.i(this.getClass().getName() + "정민, ", "낙찰내역 불러오기 도전!");
            Log.i(this.getClass().getName(), response + '\n');
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int lengthJS = jsonArray.length();
            int count = 0;
            Log.i(this.getClass().getName() + lengthJS, " 몇개나불러왔을까?");

            String bulletinNumber;  // 게시글 번호
            String mycontent;       // 나의 게시글 내용
            String startPrice;      // 경매시작가
            String currentPrice;    // 낙찰가격
            String mylatitude;      // 나의 위도
            String mylongitude;     // 나의 경도
            String yourid;          // 낙찰대상 id
            String youraddress;     // 낙찰대상 위치
            String yourlatitude;    // 낙찰대상 위도
            String yourlongitude;   // 낙찰대상 경도
            String yourcomment;     // 낙찰대상 게시글내용
            String yourphoneNumber; // 낙찰대상 전화번호
            String companyName;     // 낙찰회사이름

            while(count < lengthJS)
            {
                Log.i(this.getClass().getName() + "count: ", "count!");
                JSONObject object = jsonArray.getJSONObject(count);

                bulletinNumber  = object.getString("bulletinNumber");
                mycontent = object.getString("mycontent");
                startPrice = object.getString("startPrice");
                currentPrice = object.getString("currentPrice");
                mylatitude = object.getString("mylatitude");
                mylongitude = object.getString("mylongitude");
                yourid = object.getString("yourid");
                youraddress = object.getString("youraddress");
                yourlatitude = object.getString("yourlatitude");
                yourlongitude = object.getString("yourlongitude");
                yourcomment = object.getString("yourcomment");
                yourphoneNumber = object.getString("yourphoneNumber");
                companyName = object.getString("companyName");

                WinningHistoryItem winningHistoryItem = new WinningHistoryItem(bulletinNumber, mycontent, startPrice, currentPrice, mylatitude, mylongitude, yourid,
                        youraddress, yourlatitude, yourlongitude,yourcomment, yourphoneNumber,companyName);
                mWinningHistoryItems.add(winningHistoryItem);

                count++;
            }

        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "에러");
            e.printStackTrace();
        }
    }
}
