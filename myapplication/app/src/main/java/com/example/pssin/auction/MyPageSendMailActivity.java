package com.example.pssin.auction;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MyPageSendMailActivity extends AppCompatActivity {
    String id;
    String title;
    String content;

    EditText mailTitleEditText;
    EditText mailContentEditText;
    Button sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_send_mail);

        mailTitleEditText   = findViewById(R.id.mail_title);
        mailContentEditText = findViewById(R.id.mail_content);
        sendButton          = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> onClickSendButton());
    }

    private void onClickSendButton() {
        id      = getIntent().getStringExtra("id");
        title   = mailTitleEditText.getText().toString();
        content = mailContentEditText.getText().toString() + "  보낸사람: " + id;

        Toast.makeText(MyPageSendMailActivity.this, "이메일전송", Toast.LENGTH_SHORT).show();

        RequestTaskSendMail sendMail = new RequestTaskSendMail(this);
        sendMail.execute(title, content);

        boolean completeTask, success;
        while(true) {
            completeTask = sendMail.getCompleteTask();

            if(completeTask) {
                success = sendMail.getSuccess();

                if(success) {
                    //   Toast.makeText(RegisterActivity.this, "이메일보내기성공", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            this);
                    builder.setMessage("문의메일을 성공적으로 보냈습니다.")
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                }
                else {
                    //Toast.makeText(RegisterActivity.this, "이메일보내기실패", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            this);
                    builder.setMessage("문의메일 보내기 실패")
                            .setNegativeButton("다시 시도", null)
                            .create()
                            .show();
                }

                break;
            }
        }
    }
}
