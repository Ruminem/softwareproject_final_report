package com.example.pssin.auction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CompanyMemberImfomationChangeActivity extends AppCompatActivity {


    TextView idText, phoneText, addText;
    EditText pwText, pwcText;
    Button Changebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_imfomation_change);

        idText = findViewById(R.id.idText);
        phoneText = findViewById(R.id.phoneText);
        addText = findViewById(R.id.addText);
        pwText = findViewById(R.id.pwText);
        pwcText = findViewById(R.id.pwcText);
        Changebtn = findViewById(R.id.changeBtn);

        idText.setText(getIntent().getStringExtra("id"));
        phoneText.setText(getIntent().getStringExtra("phoneNumber"));
        addText.setText(getIntent().getStringExtra("address"));

        addText.setOnClickListener(v -> {
            Intent intent = new Intent(CompanyMemberImfomationChangeActivity.this, AddressSearchActivity.class);
            intent.putExtra("id", idText.getText().toString());
            intent.putExtra("memberClassification", "company");
            intent.putExtra("update", "update");
            CompanyMemberImfomationChangeActivity.this.startActivity(intent);
        });

        Changebtn.setOnClickListener(v -> {

            if(pwText.getText().toString().replace(" ", "").equals("")
                    || pwcText.getText().toString().replace(" ", "").equals("") ){
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CompanyMemberImfomationChangeActivity.this);
                builder.setMessage("비밀번호를 입력해주세요.")
                        .setNegativeButton("확인", null)
                        .create()
                        .show();
            } else {
                if(pwText.getText().toString().equals(pwcText.getText().toString())) {
                    String id                   = idText.getText().toString();
                    String password             = pwcText.getText().toString();
                    String memberClassification = "company";
                    RequestTaskMemberUpdate memberUpdate = new RequestTaskMemberUpdate(this);
                    memberUpdate.execute(id, password, memberClassification);

                    boolean completeTask, success;

                    while(true) {
                        completeTask = memberUpdate.getCompleteTask();

                        if(completeTask) {
                            success = memberUpdate.getSuccess();

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        CompanyMemberImfomationChangeActivity.this);
                                builder.setMessage("변경되었습니다.")
                                        .setNegativeButton("확인", null)
                                        .create()
                                        .show();
                                Intent intent = new Intent(CompanyMemberImfomationChangeActivity.this, MainActivity.class);
                                intent.putExtra("id",getIntent().getStringExtra("id"));
                                intent.putExtra("memberClassification","company");
                                intent.putExtra("fragment", "mypage");
                                CompanyMemberImfomationChangeActivity.this.startActivity(intent);
                                // 화면전환 넣기 //
                                finish();
                            }

                            break;
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            CompanyMemberImfomationChangeActivity.this);
                    builder.setMessage("비밀번호가 다릅니다.")
                            .setNegativeButton("확인", null)
                            .create()
                            .show();
                }
            }
        });
    }
}
