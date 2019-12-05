package com.example.pssin.auction;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final RadioButton normalRadioButton     = findViewById(R.id.normalRadioButton);
        final RadioButton companyRadioButton    = findViewById(R.id.companyRadioButton);
        final Button confirmButton              = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(v -> {
            if(normalRadioButton.isChecked()) {
                Log.i(this.getClass().getName() + "건호", "일반회원 radio 버튼을 누름");
                Intent Intent = new Intent(RegisterActivity.this, RegisterNormalMemberActivity.class);
                RegisterActivity.this.startActivity(Intent);
            }
            else if(companyRadioButton.isChecked()) {
                Log.i(this.getClass().getName() + "건호", "기업회원 radio 버튼을 누름");
                Intent Intent = new Intent(RegisterActivity.this, RegisterCompanyMemberActivity.class);
                RegisterActivity.this.startActivity(Intent);
            }
        });
    }
}

