package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class RequestTaskMemberUpdate extends AsyncTask<String, Void, String> {

    final String target     = "http://pssin1.cafe24.com/member/memberUpdate/memberUpdate.php";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    String id;
    String password;
    String memberClassification;
    String result;

    boolean success         = false;
    boolean completeTask    = false;
    @SuppressLint("StaticFieldLeak")
    private Context activity;

    RequestTaskMemberUpdate(Context context){
        this.activity=context;
    }

    @Override
    protected void onPreExecute() {
        //초기화하는부분
    }
    /*
    * params[0] : id
    * params[1] : password
    * params[2] : memberClassification
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params) {
        id                          = params[0];
        password                    = params[1];
        memberClassification        = params[2];
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
            query.append("id")                  .append("=").append(id)             .append("&")
                 .append("password")            .append("=").append(password)       .append("&")
                 .append("memberClassification").append("=").append(memberClassification);
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
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        try
        {
            Log.i(this.getClass().getName(), "회원정보 수정");
            JSONObject jsonResponse = new JSONObject(result);
            success = jsonResponse.getBoolean("success");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        completeTask = true;
    }

    public String getResult() { return this.result; }

    boolean getSuccess() { return success; }
    boolean getCompleteTask() { return completeTask; }
}
