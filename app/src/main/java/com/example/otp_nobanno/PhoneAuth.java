package com.example.otp_nobanno;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    private EditText edtPhone, edtCode;
    private Button btnGetOTP, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        edtCode = (EditText) findViewById(R.id.edtOTPAuth);
        edtPhone = (EditText) findViewById(R.id.edtPhoneNumberAuth);
        btnGetOTP = (Button) findViewById(R.id.btnGetOTP);
        btnLogin = (Button) findViewById(R.id.btnLoginPhoneAuth);
        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = edtPhone.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                    edtPhone.setError("please enter valid phone");
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+88" + phoneNumber, 60, TimeUnit.SECONDS, PhoneAuth.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    signInUser(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    edtCode.setError("Verification Failed!!");
                                }

                                @Override
                                public void onCodeSent(final String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);

                                    btnLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String verificationCode = edtCode.getText().toString();
                                            if (verificationId.isEmpty()) return;
                                            //create a credential
                                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                                            signInUser(credential);
                                        }
                                    });
                                }
                            }
                    );
                }
            }
        });
    }

    private void signInUser(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(PhoneAuth.this, MainActivity.class));
                            finish();
                        } else {
//                             Log.d(TAG, "onComplete:"+task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
}