package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestTaskWriteBulletin extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context activity;
    final String target     = "http://pssin1.cafe24.com/bulletinBoard/writeBulletin.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    String id                   = null;
    String content              = null;
    String timeLimit            = null;
    String startPrice           = null;
    String currentPrice         = null;
    String latitude             = null;
    String longitude            = null;
    String address              = null;
    String memberClassification = null;
    String result;

    RequestTaskWriteBulletin(Context context) {
        this.activity = context;
    }

    @Override
    protected void onPreExecute(){
        //초기화하는부분
    }
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params){
        id                          = params[0];
        content                     = params[1];
        timeLimit                   = params[2];
        startPrice                  = params[3];
        currentPrice                = params[4];
        latitude                    = params[5];
        longitude                   = params[6];
        address                     = params[7];
        memberClassification        = params[8];
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
            query.append("id")           .append("=").append(id)             .append("&")
                 .append("content")      .append("=").append(content)        .append("&")
                 .append("timeLimit")    .append("=").append(timeLimit)      .append("&")
                 .append("startPrice")   .append("=").append(startPrice)     .append("&")
                 .append("currentPrice") .append("=").append(currentPrice)   .append("&")
                 .append("latitude")     .append("=").append(latitude)       .append("&")
                 .append("longitude")    .append("=").append(longitude)      .append("&")
                 .append("address")      .append("=").append(address);
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
        try {
            Log.e(this.getClass().getName(), "게시글쓰기 시도 결과 : " + result);
            JSONObject jsonResponse = new JSONObject(result);
            boolean success = jsonResponse.getBoolean("success");
            if(success) { // 게시글쓰기 성공!
                Log.i(this.getClass().getName() + "건호", "게시글쓰기 성공");
                this.result = "SUCCESS";
                gotoMainActivity();
            }
            else { // 게시글쓰기 실패...
                Log.e(this.getClass().getName() + "건호", "게시글쓰기 실패");
                this.result = "FAIL";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() { return this.result; }

    private void gotoMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("memberClassification", memberClassification);
        activity.startActivity(intent);
    }
}
