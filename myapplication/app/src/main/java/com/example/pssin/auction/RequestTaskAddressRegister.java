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

public class RequestTaskAddressRegister extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context activity;
    final String target     = "http://pssin1.cafe24.com/address/addressRegister.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";


    String id                   = null;
    String address              = null;
    String fullAddress          = null;
    String memberClassification = null;
    String latitude             = null;
    String longitude            = null;
    String update               = null;
    String result;

    RequestTaskAddressRegister(Context context){
        this.activity=context;
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
        id                          = params[0];
        address                     = params[1];
        fullAddress                 = params[2];
        memberClassification        = params[3];
        latitude                    = params[4];
        longitude                   = params[5];
        update                      = params[6];
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
            query.append("id")                  .append("=").append(id)                  .append("&")
                 .append("address")             .append("=").append(address)             .append("&")
                 .append("fullAddress")         .append("=").append(fullAddress)         .append("&")
                 .append("memberClassification").append("=").append(memberClassification).append("&")
                 .append("latitude")            .append("=").append(latitude)            .append("&")
                 .append("longitude")           .append("=").append(longitude);
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
        try
        {
            JSONObject jsonResponse = new JSONObject(result);
            boolean success = jsonResponse.getBoolean("success");
            if(success) // 주소등록 성공
            {

                if(update.equals("update")){
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("memberClassification", memberClassification);
                    intent.putExtra("addressRegister", true);
                    intent.putExtra("fragment", "mypage");
                    activity.startActivity(intent);
                }else{
                    Log.i(this.getClass().getName() + "건호", "주소등록 성공");
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("memberClassification", memberClassification);
                    intent.putExtra("addressRegister", true);
                    activity.startActivity(intent);
                }

            }
            else // 주소등록 실패
            {
                Log.e(this.getClass().getName() + "건호", "주소등록 실패");
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("addressRegister",false);
                activity.startActivity(intent);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() { return this.result; }
}
