package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BidHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<BidHistoryItem> oData = null;

    BidHistoryAdapter(Context context, List<BidHistoryItem> oData) {
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
        Log.i(this.getClass().toString(), "getView 실행");
        @SuppressLint("ViewHolder") View v = View.inflate(context, R.layout.item_bid_history,null);

        TextView id          = v.findViewById(R.id.id);             // 아이디
        TextView bidPrice    = v.findViewById(R.id.bidPrice);       // 입찰가격
        TextView comment     = v.findViewById(R.id.comment);        // 내용

        id.setText("입찰자 : "+oData.get(position).getId());
        bidPrice.setText("입찰가 : "+String.format("%,d", Integer.parseInt(oData.get(position).getBidPrice())));
        comment.setText(oData.get(position).getComment());

        v.setTag(oData.get(position).getId());
        return v;
    }
}
