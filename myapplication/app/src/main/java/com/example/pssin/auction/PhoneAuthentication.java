package com.example.pssin.auction;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends Activity{

    private static final String TAG = "PhoneAuthentication";

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth firebaseAuth;

    private Activity activity;

    String phoneNumber;

    PhoneAuthentication(Activity activity) { this.activity = activity; }

    public void getObject() {
        Log.e(TAG, "getObject() 호출");
    }

    public void init() {
        Log.e(TAG, "init() 호출");
        getObject();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("kr");
        // To apply the default app language instead of explicitly setting it.
        firebaseAuth.useAppLanguage();
    }

    public void sendCode(String phoneNumber) {

        phoneNumber = "+82" + phoneNumber.substring(1);
        this.phoneNumber = phoneNumber;

        Log.e(TAG, "sendCode : " + phoneNumber);

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                    // Phone number to verify
                60,                          // Timeout duration
                TimeUnit.SECONDS,               // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(
                            @NonNull PhoneAuthCredential credential) {
//                        signoutButton.setEnabled(true);
//                        statusText.setText("Signed in");
//                        resendButton.setEnabled(false);
//                        verifyButton.setEnabled(false);
//                        codeText.setText("");
//                        signInWithPhoneAuthCredential(credential); // 이전에 인증내역을 이용해서 인증코드없이 자동으로 인증진행
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid Request
                            Log.e(TAG, "Invalid credential: "
                            + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.e(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                    }
                };
    }

    public void verifyCode(String code) {

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, (task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "인증 성공");
                        EditText phoneNumber = activity.findViewById(R.id.phoneNumber);
                        Button sendButton = activity.findViewById(R.id.phoneNumberInquiryButton);
                        LinearLayout verifyLayout = activity.findViewById(R.id.verifyLayout);

                        phoneNumber.setEnabled(false);
                        sendButton.setText("인증 완료");
                        sendButton.setEnabled(false);
                        verifyLayout.setVisibility(View.GONE);
                    } else {
                        if (task.getException() instanceof
                                FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                        Log.e(TAG, "인증 실패,,,");
                    }
                }));
    }
}
