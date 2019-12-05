package com.example.pssin.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalInformationActivity extends AppCompatActivity {

    TextView idText;
    String id;
    Intent intent;
    LinearLayout change, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        idText = findViewById(R.id.idText);
        change = findViewById(R.id.changeLay);
        logout = findViewById(R.id.logoutLay);
        id = getIntent().getStringExtra("id");
        idText.setText(id);


        change.setOnClickListener(v -> {
            String memberClassification = getIntent().getStringExtra("memberClassification");

            RequestTaskMemberInformation getMemberInformation = new RequestTaskMemberInformation(this);
            getMemberInformation.execute(memberClassification, id);

            boolean completeTask;
            while(true) {
                completeTask = getMemberInformation.getCompleteTask();
                Log.i(this.getClass().getName(), "completeTask : " + completeTask);

                if(completeTask) {
                    String id           = getMemberInformation.getId();
                    String phoneNumber  = getMemberInformation.getPhoneNumber();

                    if(memberClassification.equals("normal")) {
                        gotoNormalMemberImfomationChangeActivity(id, phoneNumber);
                    } else if(memberClassification.equals("company")) {
                        String address = getMemberInformation.getAddress();
                        gotoCompanyMemberInformationChangeActivity(id, phoneNumber, address);
                    }
                    break;
                }
            }
        });

        logout.setOnClickListener(v -> new AlertDialog.Builder(PersonalInformationActivity.this)
                .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, whichButton) -> {
                    Intent i = new Intent(PersonalInformationActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                })
                .setNegativeButton("취소", (dialog, whichButton) -> {

                })
                .show());
    }

    public void gotoNormalMemberImfomationChangeActivity(String id, String phoneNumber) {
        Intent intent = new Intent(this, NormalMemberImfomationChangeActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("phoneNumber", phoneNumber);

        this.startActivity(intent);
    }

    void gotoCompanyMemberInformationChangeActivity(String id, String phoneNumber, String address) {
        Intent intent = new Intent(this, CompanyMemberImfomationChangeActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("address", address);

        this.startActivity(intent);
    }
}
