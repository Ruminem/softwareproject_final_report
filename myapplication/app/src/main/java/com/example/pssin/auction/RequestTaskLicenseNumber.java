package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestTaskLicenseNumber extends AsyncTask<String, Void, String> {

    final String TAG        = "RequestTaskLicenseNumber";
    final String target     = "https://teht.hometax.go.kr/wqAction.do?actionId=ATTABZAA001R08&screenId=UTEABAAA13&popupYn=false&realScreenId=";
    final String USERAGENT  = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    String licenseNumber = null;
    String result;
    boolean success             = false;
    boolean completeTask        = false;
    @SuppressLint("StaticFieldLeak")
    private Context activity;

    RequestTaskLicenseNumber(Context context){
        this.activity=context;
    }

    @Override
    protected void onPreExecute() {
        //초기화하는부분
    }
    /*
    * params[0] : licenseNumber
    * */
    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(String... params) {
        String success = "부가가치세 일반과세자 입니다.";
        String fail1   = "폐업자";
        String fail2   = "사업을 하지 않고 있습니다.";

        licenseNumber = params[0];

        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        StringBuilder xmlPasingStr = new StringBuilder();
        String result = null;

        try {
            url = new URL(target);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type","text/xml; charset=utf-8");
            String body = "<map id='ATTABZAA001R08'><pubcUserNo/><mobYn>N</mobYn><inqrTrgtClCd>1</inqrTrgtClCd><txprDscmNo>" +
                    licenseNumber + "</txprDscmNo><dongCode>05</dongCode><psbSearch>Y</psbSearch><map id='userReqInfoVO'/></map>";
            OutputStream output = new BufferedOutputStream(conn.getOutputStream());
            output.write(body.getBytes());
            output.flush();
            output.close();

            //전송후 결과를 받기위해 inputStream 생성
            InputStream inStream;
            int status = conn.getResponseCode();

            if(status != HttpURLConnection.HTTP_OK) {
                inStream = conn.getErrorStream();
                Log.e(TAG, String.valueOf(status));
            }
            else {
                inStream = conn.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inStream));
                String line; //전송 받은 스트림의 line 별값 세팅 용

                //읽어들일 line 이 있으면 실행
                while ((line = bufferedReader.readLine()) != null){
                    xmlPasingStr.append(line); //line 별 결과는 연결해서 최종 string 생성
                }

                result = xmlPasingStr.toString();

                if(result.contains(success)) {  // 1. 조회에 성공했다면
                    Log.i(TAG, "사업자번호 조회성공");
                    this.success = true;
                    this.result = success;
                } else if(result.contains(fail1) || result.contains(fail2)) { // 2. 조회에 실패했다면
                    Log.i(TAG, "사업자번호 조회실패");
                    this.success = false;

                    if(result.contains(fail1)) {
                        this.result = fail1;
                    } else if(result.contains(fail2)) {
                        this.result = fail2;
                    }
                }
            }
            onPostExecute(this.result);
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
        completeTask = true;
    }

    public String getResult() { return this.result; }
    public boolean getCompleteTask() { return this.completeTask; }
    public boolean getSuccess() { return this.success; }
}
