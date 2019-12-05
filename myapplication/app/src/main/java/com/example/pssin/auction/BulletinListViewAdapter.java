package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BulletinListViewAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private Context context;
    private List<BulletinListViewItem> oData = null;
    CountDownTimer countDownTimer;

    BulletinListViewAdapter(Context context, List<BulletinListViewItem> oData) {
        Log.i(this.getClass().toString(), "입찰내역 목록 어댑터 생성");
        this.context = context;
        this.oData = oData;
    }

    @Override
    public int getCount() {
        Log.i(this.getClass().toString(), "getCount");
        return oData.size();
    }

    @Override
    public Object getItem(int position) {
        return oData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(this.getClass().toString(), "getView 실행");
        @SuppressLint("ViewHolder") View v = View.inflate(context, R.layout.item_bulletin_list,null);

        TextView address      = v.findViewById(R.id.address);      // 지역
        TextView countdown    = v.findViewById(R.id.countdown);    // 남은 입찰시간
        TextView content      = v.findViewById(R.id.content);      // 내용
        TextView currentPrice = v.findViewById(R.id.currentPrice); // 현재 입찰가

        address.setText(oData.get(position).getAddress());
        // 각 게시글마다 시간제한을 둠 ( 기능구현 다 끝나면 -1000을 1000으로 수정해야함 )
        int remainTime = Integer.parseInt(oData.get(position).getCountdown()) * 1000;
//        int remainTime = 3610 * 1000;
        countDownTimer(countdown, remainTime, 1000);
        content.setText(oData.get(position).getContent());

        // String.format("%,d", integer) : integer를 천원단위로 콤마 삽입해줌.
        currentPrice.setText(String.format("%,d", Integer.parseInt(oData.get(position).getCurrentPrice())) + "원");

        v.setTag(oData.get(position).getBulletinNumber());
        return v;
    }

    private void countDownTimer(TextView countdown, int MILLISINFUTURE, int COUNT_DOWN_INTERVAL) { //카운트 다운 메소드

        //줄어드는 시간을 나타내는 TextView
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) { //(300초에서 1초 마다 계속 줄어듬)

                long bulletinCountdown = millisUntilFinished / 1000;
                //Log.d(this.getClass().getName() + "게시글 카운트다운", bulletinCountdown + "");

                long hour   = (bulletinCountdown / 60) / 60;
                long minute = (bulletinCountdown / 60) % 60;
                long second = bulletinCountdown % 60;

                // 남은 시간이 1시간도 안남았다면 글자색을 빨간색으로 변경
                if(hour <= 0) {
                    countdown.setTextColor(Color.RED);
                }

                // minute, second가 10 미만일 때 앞에 0을 추가해서 출력
                if(minute >= 10 && second >= 10) {
                    countdown.setText(hour + " : " + minute + " : " + second);
                } else if(minute >= 10 && second < 10) {
                    countdown.setText(hour + " : " + minute + " : " + "0" + second);
                } else if(minute < 10 && second >= 10) {
                    countdown.setText(hour + " : " + "0" + minute + " : " + second);
                } else {
                    countdown.setText(hour + " : " + "0" + minute + " : " + "0" + second);
                }

                //bulletinCountdown은 종료까지 남은 시간임. 1분 = 60초 되므로,
                // 분을 나타내기 위해서는 종료까지 남은 총 시간에 60을 나눠주면 그 몫이 분이 된다.
                // 분을 제외하고 남은 초를 나타내기 위해서는, (총 남은 시간 - (분*60) = 남은 초) 로 하면 된다.

            }


            @Override
            public void onFinish() { //시간이 다 되면 다이얼로그 종료
            }
        }.start();
    }
}

