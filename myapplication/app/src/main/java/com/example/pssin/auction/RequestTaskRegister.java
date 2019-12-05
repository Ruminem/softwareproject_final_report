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

public class RequestTaskRegister extends AsyncTask<String, Void, String> {

    private Context activity;
    final String target     = "http://pssin1.cafe24.com/member/memberRegister/memberRegister.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    String result;

    RequestTaskRegister(Context context) {
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
    *
    * ''' 일반회원 '''
    * params[3] : phoneNumber
    *
    * ''' 기업회원 '''
    * params[3] : licenseNumber
    * params[4] : companyName
    * params[5] : phoneNumber
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params) {
        String memberClassification = null;
        String id                   = null;
        String password             = null;
        String licenseNumber        = null;
        String companyName          = null;
        String phoneNumber          = null;
        URL url                     = null;
        HttpURLConnection conn      = null;

        try {
            // 변수부
            memberClassification = params[0];
            id                   = params[1];
            password             = params[2];
            if(memberClassification.equals("normal")) {
                Log.i(this.getClass().getName(), "일반회원 가입요청");
                phoneNumber   = params[3];
            } else if(memberClassification.equals("company")) {
                Log.i(this.getClass().getName(), "기업회원 가입요청");
                licenseNumber = params[3];
                companyName   = params[4];
                phoneNumber   = params[5];
            }


            // 실행부
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
            if(memberClassification.equals("normal")) {
                query.append("memberClassification").append("=").append("normal").append("&")
                        .append("id")               .append("=").append(id)      .append("&")
                        .append("password")         .append("=").append(password).append("&")
                        .append("phoneNumber")      .append("=").append(phoneNumber);
            } else if(memberClassification.equals("company")) {
                query.append("memberClassification").append("=").append("company").append("&")
                        .append("id")           .append("=").append(id)           .append("&")
                        .append("password")     .append("=").append(password)     .append("&")
                        .append("licenseNumber").append("=").append(licenseNumber).append("&")
                        .append("companyName")  .append("=").append(companyName)  .append("&")
                        .append("phoneNumber")  .append("=").append(phoneNumber);
            }

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
            Log.e(this.getClass().getName(), "회원가입 결과 : " + result);
            JSONObject jsonResponse = new JSONObject(result);
            boolean success = jsonResponse.getBoolean("success");
            if(success) { // 회원가입 성공!
                Log.i(this.getClass().getName() + "건호", "회원가입 성공");
                this.result = "SUCCESS";
                gotoLoginActivity();
            }
            else { // 회원가입 실패...
                Log.e(this.getClass().getName() + "건호", "회원가입 실패");
                this.result = "FAIL";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() { return this.result; }

    private void gotoLoginActivity() {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
