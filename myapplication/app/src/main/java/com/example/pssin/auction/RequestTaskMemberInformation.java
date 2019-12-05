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

public class RequestTaskMemberInformation extends AsyncTask<String, Void, String> {

    final String target     = "http://pssin1.cafe24.com/member/memberGet/getMemberInformation.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    String memberClassification = null;
    // 일반회원 + 기업회원 정보 ( 공통 )
    String id                   = null;
    String password             = null;
    String phoneNumber          = null;
    String latitude             = null;
    String longitude            = null;
    String address              = null;
    String fullAddress          = null;

    // 기업회원 정보
    String category             = null;
    String licenseNumber        = null;
    String companyName          = null;
    String openingHours         = null;
    String closingHours         = null;

    String result               = null;

    boolean success             = false;
    boolean completeTask        = false;
    @SuppressLint("StaticFieldLeak")
    private Context activity;

    RequestTaskMemberInformation(Context context){
        this.activity = context;
    }

    @Override
    protected void onPreExecute() {
        //초기화하는부분
    }
    /*
    * params[0] : memberClassification
    * params[1] : id
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params) {
        Log.i(this.getClass().getName(), "doInBackground");
        memberClassification        = params[0];
        id                          = params[1];
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
            query.append("memberClassification").append("=").append(memberClassification).append("&")
                 .append("id")                  .append("=").append(id);
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
            Log.i(this.getClass().getName(), "doInBackground 종료, 리스폰스 반환");
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
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(this.getClass().getName(), "onPostExecute");
        this.result = result;
        try {
            JSONObject jsonResponse = new JSONObject(result);
            success = jsonResponse.getBoolean("success");
            if (success) {
                this.id                 = jsonResponse.getString("id");
                this.password           = jsonResponse.getString("password");
                this.phoneNumber        = jsonResponse.getString("phoneNumber");
                this.latitude           = jsonResponse.getString("latitude");
                this.longitude          = jsonResponse.getString("longitude");
                this.address            = jsonResponse.getString("address");
                this.fullAddress        = jsonResponse.getString("fullAddress");

                if(memberClassification.equals("company")) {
                    this.category       = jsonResponse.getString("category");
                    this.licenseNumber  = jsonResponse.getString("licenseNumber");
                    this.companyName    = jsonResponse.getString("companyName");
                    this.openingHours   = jsonResponse.getString("openingHours");
                    this.closingHours   = jsonResponse.getString("closingHours");
                }
                // 화면전환 넣기 //
            }
            this.completeTask = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getResult() { return this.result; }

    boolean getSuccess() { return success; }
    boolean getCompleteTask() { return completeTask; }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public String getFullAddress() { return fullAddress; }
    public String getCategory() { return category; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getCompanyName() { return companyName; }
    public String getOpeningHours() { return openingHours; }
    public String getClosingHours() { return closingHours; }
}
