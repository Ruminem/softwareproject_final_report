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

public class RequestTaskBulletinList extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private FragmentMainActivityHome activity;
    final String target     = "http://pssin1.cafe24.com/bulletinBoard/bulletinBoardItem.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    String keyword        = null;
    String result         = null;
    boolean isRunning     = true;

    RequestTaskBulletinList(FragmentMainActivityHome context){
        this.activity = context;
    }

    @Override
    protected void onPreExecute(){
        //초기화하는부분
    }
    /*
    * params[0] : memberClassification
    * params[1] : id
    * params[2] : password
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params){
        keyword                     = params[0];
        URL url                     = null;
        HttpURLConnection conn      = null;

        //while(isRunning) {
            try {
                Log.i(this.getClass().getName(), "게시글 목록 불러오기 수행");
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

                InputStream inStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                    response.append(line + "\n");                   // View에 표시하기 위해 라인 구분자 추가
                }

                //해당문자열의 집합 반환
                this.result = response.toString().trim();
                return this.result;
//                onProgressUpdate();
//                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        //}
        //오류발생시 null
        return null;
    }

    @Override
    public void onProgressUpdate(String... params){
        super.onProgressUpdate(params);
        Log.i(this.getClass().getName(), "onProgressUpdate");
        activity.getBulletinList(this.result, keyword);
    }


    @Override
    public void onPostExecute(String result){
        super.onPostExecute(result);
        Log.i(this.getClass().getName(), "onPostExecute");
        activity.getBulletinList(this.result, keyword);
    }

    public String getResult() { return this.result; }

    public void setRunningState(boolean state) { this.isRunning = state; }
}
