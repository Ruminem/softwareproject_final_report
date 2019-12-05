package com.example.pssin.auction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static Context mContext;

    /*
     *   idText                   : 사용자가 입력한 ID값
     *   passwordText             : 사용자가 입력한 password값
     *   normalMemberRadioButton  : 사용자의 회원구분 ( 일반회원 )
     *   companyMemberRadioButton : 사용자의 회원구분 ( 기업회원 )
     *   loginButton              : 로그인버튼
     *   registreButton           : 회원가입버튼
     */
    EditText idText;
    EditText passwordText;
    RadioButton normalMemberRadioButton;
    RadioButton companyMemberRadioButton;
    Button loginButton;
    TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 각 변수들을 레이아웃의 id들과 매칭
        getObject();

        // 로그인 버튼을 누르면 MainActivity로 인텐트 수행
        loginButton.setOnClickListener(v -> clickLoginButton());

        // 회원가입 버튼을 누르면 RegisterActivity로 인텐트 수행
        registerButton.setOnClickListener(v -> clickRegisterButton());
    }

    private void getObject() {
        mContext = this;

        idText                      = findViewById(R.id.idText);
        passwordText                = findViewById(R.id.passwordText);
        normalMemberRadioButton     = findViewById(R.id.normalMemberRadioButton);
        companyMemberRadioButton    = findViewById(R.id.companyMemberRadioButton);
        loginButton                 = findViewById(R.id.loginButton);
        registerButton              = findViewById(R.id.registerButton);
    }

    private void clickLoginButton() {
        Log.i(this.getClass().getName() + "건호", "로그인 버튼을 클릭하였다");

        String memberClassification = null;
        String id                   = idText.getText().toString();
        String password             = passwordText.getText().toString();

        if(normalMemberRadioButton.isChecked())
            memberClassification = "normal";
        else if(companyMemberRadioButton.isChecked())
            memberClassification = "company";

        RequestTaskLogin login = new RequestTaskLogin(this);
        login.execute(memberClassification, id, password);
    }

    private void clickRegisterButton() {
        Log.i(this.getClass().getName() + "건호", "회원가입 버튼을 누름");
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(registerIntent);
    }
}
