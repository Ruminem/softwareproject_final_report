package com.example.pssin.auction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterNormalMemberActivity extends AppCompatActivity {
    /*
        idText               : 아이디 입력칸
        passwordText         : 비밀번호 입력칸
        phoneNumberTextLeft  : 휴대폰번호 앞 3자리 ( default : 010 )
        phoneNumberTextRight : 휴대폰번호 뒷 8자리
        inquiryButton        : Facebook Account Kit을 통한 휴대폰 SMS 인증버튼
        confirmButton        : 회원등록 버튼
        cancelButton         : 취소 버튼 ( 이전화면으로 인텐트 )
     */
    private static final String TAG = "RegisterNormalMemberActivity";


    EditText idText;
    EditText passwordText;
    EditText phoneNumberText;
    Button inquiryButton;
    Button confirmButton;
    Button verifyButton;

    InputMethodManager imm;
    LinearLayout mylayout;
    LinearLayout verifyLayout;

    PhoneAuthentication phoneAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_normal_member);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mylayout = findViewById(R.id.activity_register_normal_member);

        // 각 변수들을 레이아웃의 id와 매칭
        getObjects();

        // 인증버튼 누르기 전 입력값들 유지
        getTexts();

        // SMS 인증버튼과 회원등록 버튼을 제어
        buttonControl();

        // SMS 인증버튼을 눌렀을 떄 동작하는 부분
        inquiryButton.setOnClickListener(v -> {hideKeyboard(); clickInquiryButton();});
        verifyButton.setOnClickListener(v -> {hideKeyboard(); clickVerifyButton();});

        // 회원버튼을 눌렀을 때 동작하는 부분
        confirmButton.setOnClickListener(v -> {hideKeyboard(); clickConfirmButton();});
    }

    // 버튼 누를 때 키보드 올라오는 거 방지
    public void hideKeyboard() {
        imm.hideSoftInputFromWindow(mylayout.getWindowToken(), 0);
    }

    private void getObjects() {
        idText               = findViewById(R.id.idText);
        passwordText         = findViewById(R.id.passwordText);
        phoneNumberText      = findViewById(R.id.phoneNumber);

        inquiryButton        = findViewById(R.id.phoneNumberInquiryButton);
        confirmButton        = findViewById(R.id.confirmButton);
        verifyButton         = findViewById(R.id.verifyButton);

        verifyLayout         = findViewById(R.id.verifyLayout);
        verifyLayout.setVisibility(View.GONE);

        phoneAuthentication = new PhoneAuthentication(this);
    }

    private void getTexts() {
        Intent intent = getIntent();
        idText.setText(intent.getStringExtra("idText"));
        passwordText.setText(intent.getStringExtra("passwordText"));
    }

    private void clickInquiryButton() {
        String phoneNumber = phoneNumberText.getText().toString();
        if(phoneNumber.length() != 11 || !isDigit(phoneNumber)) {
            phoneNumberText.setError("휴대폰 번호를 확인해주세요.");
        } else {
            verifyLayout.setVisibility(View.VISIBLE);

            phoneAuthentication.init();
            phoneAuthentication.sendCode(phoneNumber);
        }
    }

    private void clickVerifyButton() {
        Log.e(TAG, "clickVerifyButton() 호출");
        EditText codeText = findViewById(R.id.codeText);
        String code = codeText.getText().toString();

        phoneAuthentication.verifyCode(code);
    }

    private boolean isDigit(String str) {
        char c;
        for(int i = 0; i < str.length(); ++i) {
            c = str.charAt(i);
            if(!('0' <= c && c <= '9'))
                return false;
        }

        return true;
    }

    private void clickConfirmButton() {
        String memberClassification = "normal";
        String id = idText.getText().toString();
        String password = passwordText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();

        RequestTaskRegister register = new RequestTaskRegister(this);
        register.execute(memberClassification, id, password, phoneNumber);
    }

    // SMS 인증버튼과 회원등록 버튼을 활성화, 비활성화
    private void buttonControl() {
        boolean success = getIntent().getBooleanExtra("phoneNumberInquirySuccess", false);

        Intent intent = getIntent();
        idText.setText(intent.getStringExtra("idText"));
        passwordText.setText(intent.getStringExtra("passwordText"));

        inquirySuccess(success);
    }

    // Facebook Account Kit
    private void inquirySuccess(boolean success) {
        /*
            1. SMS 인증이 완료되었다면
            1-1. SMS 인증버튼 텍스트를 "SMS 인증 성공"로 변경
            1-2. SMS 인증버튼의 색깔을 파란색으로 변경
            1-3. SMS 인증버튼 비활성화
            1-4. 휴대폰번호 입력칸 비활성화
            1-5. 회원등록 버튼 활성화

            2. SMS 인증이 완료되지 않았다면
            2-1. SMS 인증버튼 텍스트를 "SMS 인증하기"로 변경
            2-2. SMS 인증버튼의 색깔을 빨간색으로 변경
            2-3. SMS 인증버튼 활성화
            2-4. 휴대폰번호 입력칸 활성화
            2-5. 회원등록 버튼 비활성화
         */
        String inquiryBefore = "인증";
        String inquiryAfter  = "완료";

        if(success) {   // SMS인증이 완료되었다면
            inquiryButton.setText(inquiryAfter);
        }
        else {          // SMS인증이 완료되지 않았다면
            inquiryButton.setText(inquiryBefore);
        }
    }

    // 버튼 활성화, 비활성화 메소드
    private void setUsableButton(Button button, boolean usable) {
        button.setClickable(usable);
        button.setEnabled(usable);
        button.setFocusable(false);
        button.setFocusableInTouchMode(false);
    }

    private void gotoLoginActivity() {
        Intent Intent = new Intent(this, LoginActivity.class);
        startActivity(Intent);
        finish();
    }
}

