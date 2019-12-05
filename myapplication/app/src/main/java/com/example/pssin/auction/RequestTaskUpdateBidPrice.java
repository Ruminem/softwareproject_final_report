package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestTaskUpdateBidPrice extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context activity;
    final String target     = "http://pssin1.cafe24.com/bulletinBoard/updateBidPrice.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    String bulletinNumber   = null;
    String id               = null;
    String bidPrice         = null;
    String address          = null;
    String latitude         = null;
    String longitude        = null;
    String comment          = null;
    String phoneNumber      = null;
    String companyName      = null;
    String result;

    boolean success         = false;
    boolean completeTask    = false;

    RequestTaskUpdateBidPrice(Context context){
        this.activity=context;
    }

    @Override
    protected void onPreExecute(){
        //초기화하는부분
    }
    /*
    * params[0] : bulletinNumber
    * params[1] : id
    * params[2] : bidPrice
    * params[3] : address
    * params[4] : latitude
    * params[5] : longitude
    * params[6] : comment
    * params[7] : phoneNumber
    * params[8] : companyName
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params) {
        Log.i(this.getClass().getName(), "doInBackground 호출");
        bulletinNumber              = params[0];
        id                          = params[1];
        bidPrice                    = params[2];
        address                     = params[3];
        latitude                    = params[4];
        longitude                   = params[5];
        comment                     = params[6];
        phoneNumber                 = params[7];
        companyName                 = params[8];
        URL url                     = null;
        HttpURLConnection conn      = null;

        try {
            url = new URL(target);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", USERAGENT);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer query = new StringBuffer();
            query.append("bulletinNumber")      .append("=").append(bulletinNumber)      .append("&")
                 .append("id")                  .append("=").append(id)                  .append("&")
                 .append("bidPrice")            .append("=").append(bidPrice)            .append("&")
                 .append("address")             .append("=").append(address)             .append("&")
                 .append("latitude")            .append("=").append(latitude)            .append("&")
                 .append("longitude")           .append("=").append(longitude)           .append("&")
                 .append("comment")             .append("=").append(comment)             .append("&")
                 .append("phoneNumber")         .append("=").append(phoneNumber)         .append("&")
                 .append("companyName")         .append("=").append(companyName);
            Log.e(this.getClass().getName() + "query", query.toString());
            OutputStream os = conn.getOutputStream();
            os.write(query.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();

            InputStream inStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inStream, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                response.append(line + "\n");                   // View에 표시하기 위해 라인 구분자 추가
            }

            //해당문자열의 집합 반환
            onPostExecute(response.toString().trim());
            return response.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        //오류발생시 null
        return null;
    }
    @Override
    public  void onProgressUpdate(Void... values){
        super.onProgressUpdate(values);
    }


    @Override
    public void onPostExecute(String result){
        super.onPostExecute(result);
        Log.i(this.getClass().getName(), "onPostExecute 호출");
        this.result = result;
        try {
            JSONObject jsonObject = new JSONObject(result);
            success = jsonObject.getBoolean("success");
            this.completeTask = true;
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), "에러");
            e.printStackTrace();
        }
    }

    public String getResult() { return this.result; }

    boolean getSuccess() { return this.success; }
    boolean getCompleteTask() { return this.completeTask; }

}
