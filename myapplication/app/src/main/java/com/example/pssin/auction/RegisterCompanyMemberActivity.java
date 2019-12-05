/*
    작성자        : 박건호
    용도          : 기업회원을 위한 회원가입 화면
    이전 레이아웃  : RegisterActivity
 */
package com.example.pssin.auction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterCompanyMemberActivity extends AppCompatActivity {
    /*
            idText                           : 아이디
            passwordText                     : 비밀번호
            phoneNumberTextLeft              : 휴대폰번호 앞 3자리 ( default : 010 )
            phoneNumberTextRight             : 휴대폰번호 뒷 8자리
            companyNameText                  : 회사 이름
            businessLicenseNumber            : 사업자 등록번호 ( 10자리 )
            phoneNumberInquiryButton         : 휴대폰 번호 SMS 조회버튼 ( intent to FacebookAccountKitActivity )
            licenseNumberInquiryButton       : 사업자등록번호조회 버튼
            registerButton                   : 등록 버튼 ( intent to LoginActivity )
            cancelButton                     : 취소 버튼 ( intent to RegisterActivity )
     */
    private static final String TAG = "RegisterCompanyMemberActivity";

    EditText idText;
    EditText passwordText;
    EditText phoneNumberText;
    EditText companyNameText;
    EditText businessLicenseNumber;
    Button phoneNumberInquiryButton;
    Button licenseNumberInquiryButton;
    Button confirmButton;
    Button verifyButton;

    InputMethodManager imm;
    LinearLayout mylayout;
    LinearLayout verifyLayout;

    PhoneAuthentication phoneAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company_member);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mylayout = findViewById(R.id.activity_register_company_member);

        Log.i(TAG, "기업회원가입 화면 들어옴");
        // 각 변수들과 레이아웃의 id와 매칭
        getObject();

        // 인증 여부에 따른 버튼 활성화 비활성화 조절
        buttonControl();

        verifyLayout = findViewById(R.id.verifyLayout);
        verifyLayout.setVisibility(View.GONE);

        // 휴대폰 번호 인증버튼을 눌렀을 때 동작하는 부분
        phoneNumberInquiryButton.setOnClickListener(v -> {hideKeyboard(); clickPhoneNumberInquiryButton();});
        verifyButton.setOnClickListener(v -> {hideKeyboard(); clickVerifyButton();});

        // 사업자등록번호 조회버튼을 눌렀을 때 동작하는 부분
        licenseNumberInquiryButton.setOnClickListener(v -> {hideKeyboard(); clickCompanyNumberInquiryBotton();});

        // 확인 버튼을 눌렀을 때 동작하는 부분
        confirmButton.setOnClickListener(v -> {hideKeyboard(); clickConfirmButton();});
    }

    // 버튼 누를 때 키보드 올라오는 거 방지
    public void hideKeyboard() {
        imm.hideSoftInputFromWindow(mylayout.getWindowToken(), 0);
    }

    private void getObject() {
        idText                      = findViewById(R.id.idText);
        passwordText                = findViewById(R.id.passwordText);
        phoneNumberText             = findViewById(R.id.phoneNumber);

        phoneNumberInquiryButton    = findViewById(R.id.phoneNumberInquiryButton);
        companyNameText             = findViewById(R.id.companyNameText);
        businessLicenseNumber       = findViewById(R.id.businessLicenseNumber);
        licenseNumberInquiryButton  = findViewById(R.id.licenseNumberInquiryButton);
        confirmButton               = findViewById(R.id.confirmButton);
        verifyButton                = findViewById(R.id.verifyButton);

        phoneAuthentication = new PhoneAuthentication(this);
    }

    private void buttonControl() {
        /*
            forInquiry      : CompanyInquiryActibity에서 사업자등록번호 조회가 성공했는지에 대한 변수를 받기 위함
            inquirySuccess  : true : 사업자등록번호 조회성공, false : 사업자등록번호 조회실패
            setUsableButton : inquirySuccess 변수에 따라 확인 버튼을 활성화 or 비활성화시킴
         */
        idText.setText(getIntent().getStringExtra("idText"));
        passwordText.setText(getIntent().getStringExtra("passwordText"));
        companyNameText.setText(getIntent().getStringExtra("companyNameText"));
        businessLicenseNumber.setText(getIntent().getStringExtra("licenseNumber"));
        boolean phoneNumberInquirySuccess = getIntent().getBooleanExtra("phoneNumberInquirySuccess", false);
        boolean licenseNumberInquirySuccess = getIntent().getBooleanExtra("licenseNumberInquirySuccess", false);
        inquirySuccess(phoneNumberInquirySuccess, licenseNumberInquirySuccess);
    }

    private void clickPhoneNumberInquiryButton() {
        String phoneNumber = phoneNumberText.getText().toString();
        if(phoneNumber.length() != 11 || !isDigit(phoneNumber)) {
            phoneNumberText.setError("휴대폰 번호를 확인해주세요.");
        }
        else {
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

    private void clickCompanyNumberInquiryBotton() {
        String licensenumber = businessLicenseNumber.getText().toString();

        // 입력한 사업자번호가 10자리가 아니라면 다시 입력하라고 메세지 띄움
        if(licensenumber.length() != 10 || !isDigit(licensenumber)) {
            businessLicenseNumber.setError("사업자 번호를 확인해주세요.");
        } else {
            RequestTaskLicenseNumber requestTaskLicenseNumber = new RequestTaskLicenseNumber(this);
            requestTaskLicenseNumber.execute(licensenumber);

            boolean completeTask, success;
            while(true) {
                completeTask = requestTaskLicenseNumber.getCompleteTask();
                if(completeTask) {
                    success = requestTaskLicenseNumber.getSuccess();
                    if(success) {
                        Log.i(TAG, "사업자번호 조회성공");
//                        Looper.prepare();
//                        Toast.makeText(RegisterCompanyMemberActivity.this, "조회 성공~!", Toast.LENGTH_SHORT).show();
                        licenseNumberInquiryButton.setText("완료");
                        businessLicenseNumber.setEnabled(false);
                        inquirySuccess(getIntent().getBooleanExtra("phoneNumberInquirySuccess", false), true);
//                        Looper.loop();
                    } else {
                        Log.i(TAG, "사업자번호 조회실패");
                        Looper.prepare();
                        Toast.makeText(RegisterCompanyMemberActivity.this, "조회 실패~!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    break;
                }

            }
        }
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
        String memberClassification = "company";
        String id = idText.getText().toString();
        String password = passwordText.getText().toString();
        String licenseNumber = businessLicenseNumber.getText().toString();
        String companyName = companyNameText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();

        RequestTaskRegister register = new RequestTaskRegister(this);
        register.execute(memberClassification, id, password, licenseNumber, companyName, phoneNumber);
    }

    private void clickCancelButton() {
        Intent intent = new Intent(RegisterCompanyMemberActivity.this, RegisterActivity.class);
        RegisterCompanyMemberActivity.this.startActivity(intent);
    }

    private void inquirySuccess(boolean phoneNumberInquirySuccess, boolean licenseNumberInquirySuccess) {
        String phoneNumberInquiryBefore   = "인증";
        String companyNumberInquiryBefore = "조회";
        String inquiryAfter               = "완료";

        // 버튼 힌트텍스트 색깔 지정
        setFocusableButton(phoneNumberInquiryButton);
        setFocusableButton(licenseNumberInquiryButton);
        setFocusableButton(confirmButton);
        if(phoneNumberInquirySuccess && licenseNumberInquirySuccess) {   // 둘 다 인증성공
            // 휴대폰 인증버튼 관련
            phoneNumberInquiryButton.setHint(inquiryAfter);

            // 사업자등록번호 인증버튼 관련
            licenseNumberInquiryButton.setHint(inquiryAfter);
            businessLicenseNumber.setEnabled(false);

            // 회원등록 버튼 관련
            confirmButton.setText("등록");
        } else if(phoneNumberInquirySuccess) { // SMS 인증만 성공
            // 휴대폰 인증버튼 관련
            phoneNumberInquiryButton.setHint(inquiryAfter);

            // 사업자등록번호 인증버튼 관련
            licenseNumberInquiryButton.setHint(companyNumberInquiryBefore);
            businessLicenseNumber.setEnabled(true);

            // 회원등록 버튼 관련
            confirmButton.setHint("인증필요");
        } else if(licenseNumberInquirySuccess) { // 사업자번호 인증만 성공
            // 휴대폰 인증버튼 관련
            phoneNumberInquiryButton.setHint(phoneNumberInquiryBefore);

            // 사업자등록번호 인증버튼 관련
            licenseNumberInquiryButton.setHint(inquiryAfter);
            businessLicenseNumber.setEnabled(false);


            // 회원등록 버튼 관련
            confirmButton.setHint("인증필요");
        } else { // 둘 다 인증하기 전
            // 휴대폰 인증버튼 관련
            phoneNumberInquiryButton.setHint(phoneNumberInquiryBefore);

            // 사업자등록번호 인증버튼 관련
            licenseNumberInquiryButton.setHint(companyNumberInquiryBefore);
            businessLicenseNumber.setEnabled(true);

            // 회원등록 버튼 관련
            confirmButton.setHint("인증필요");
        }
    }

    // 버튼 활성화, 비활성화 메소드
    private void setFocusableButton(Button button) {
        button.setFocusable(false);
        button.setFocusableInTouchMode(false);
    }
}