package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WinningHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<WinningHistoryItem> oData = null;

    WinningHistoryAdapter(Context context, List<WinningHistoryItem> oData) {
        Log.i(this.getClass().toString(), "게시글 목록 어댑터 생성");
        this.context = context;
        this.oData = oData;
    }

    @Override
    public int getCount() {
        Log.i(this.getClass().toString(), "getCount");
        return oData.size();
    }

    @Override
    public Object getItem(int position) { return oData.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View v = View.inflate(context, R.layout.item_winning_history,null);
        Log.i(this.getClass().toString(), "getView 실행");

        TextView bulletinNumber     =       v.findViewById(R.id.bulletinNumber);
        TextView mycontent          =       v.findViewById(R.id.mycontent);
        TextView startPrice         =       v.findViewById(R.id.startPrice);
        TextView currentPrice       =       v.findViewById(R.id.currentPrice);
        TextView mylatitude         =       v.findViewById(R.id.mylatitude);
        TextView mylongitude        =       v.findViewById(R.id.mylongitude);
        TextView yourid             =       v.findViewById(R.id.yourid);
        TextView youraddress        =       v.findViewById(R.id.youraddress);
        TextView yourlatitude       =       v.findViewById(R.id.yourlatitude);
        TextView yourlongtude       =       v.findViewById(R.id.yourlongitude);
        TextView yourcomment        =       v.findViewById(R.id.yourcomment);
        TextView yourphoneNumber    =       v.findViewById(R.id.yourphoneNumber);
        TextView companyname        =       v.findViewById(R.id.companyName);

        bulletinNumber.setText(oData.get(position).getBulletinNumber());
        mycontent.setText(oData.get(position).getmycontent());
        startPrice.setText(String.format("%,d", Integer.parseInt(oData.get(position).getstartPrice())));
        currentPrice.setText(String.format("%,d", Integer.parseInt(oData.get(position).getcurrentPrice())));
        mylatitude.setText(oData.get(position).getmylatitude());
        mylongitude.setText(oData.get(position).getmylongitude());
        yourid.setText(oData.get(position).getyourid());
        youraddress.setText(oData.get(position).getyouraddress());
        yourlatitude.setText(oData.get(position).getyourlatitude());
        yourlongtude.setText(oData.get(position).getyourlongitude());
        yourcomment.setText(oData.get(position).getyourcomment());
        yourphoneNumber.setText(oData.get(position).getyourphoneNumber());
        companyname.setText(oData.get(position).getcompanyName());
        v.setTag(oData.get(position).getBulletinNumber());
        return v;
    }
}
