package com.nerdcastle.eparkingprovider.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.DataModel.Provider;
import com.nerdcastle.eparkingprovider.DataModel.TempHolder;
import com.nerdcastle.eparkingprovider.HomeActivity;
import com.nerdcastle.eparkingprovider.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;
    private Button signIn;
    private ProgressBar progressBar;
    //Firebase Section
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseUserInformation;
    private String mThisDate;
    private String mPhoneNumber;
    private String mUserType;
    private String mobile;
    private int waitingTime=15;
    private Handler handler = new Handler();
    private TextView resendOTP,waitingTimeTV;
    String mProviderID;

    private Runnable timedTask = new Runnable() {
        @Override
        public void run() {


            if (waitingTime>0)
            {
                waitingTime--;
                waitingTimeTV.setText("Resend Code in "+String.valueOf(waitingTime)+" Seconds");
            }

            if (waitingTime<1)
            {
                resendOTP.setVisibility(View.VISIBLE);
                waitingTimeTV.setVisibility(View.GONE);
            }

            handler.postDelayed(timedTask, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_phone);

        //Firebase initialization
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList");
        mAuth = FirebaseAuth.getInstance();


        handler.post(timedTask);

        //initializing objects
        editTextCode = findViewById(R.id.editTextCode);
        resendOTP=findViewById(R.id.resend_code_id);
        waitingTimeTV=findViewById(R.id.textView_id);
        progressBar = findViewById(R.id.progressBar_cyclic);
        signIn = findViewById(R.id.buttonSignIn);
        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        mUserType = intent.getStringExtra("user");
        mPhoneNumber = mobile;
        sendVerificationCode(mobile);



        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode(mobile);
                resendOTP.setVisibility(View.GONE);
                waitingTime=15;
                waitingTimeTV.setVisibility(View.VISIBLE);
            }
        });

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+ e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        signIn.setVisibility(View.INVISIBLE);
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(VerifyPhoneActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(VerifyPhoneActivity.this);
                            startActivity(intent, options.toBundle());



                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mAuth !=null)
                                    {
                                        if (mUserType.equals("new_user")){
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            user.updateEmail(mPhoneNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });


                                            Date todayDate = new Date();
                                            long currentDayMillis = todayDate.getTime();
                                            mThisDate = Long.toString(currentDayMillis);
                                            mProviderID = mAuth.getUid();
                                            Provider provider = new Provider(mProviderID,"","","","","","","",mPhoneNumber,"","","", "0", "0");
                                            mFirebaseDatabase.child(mProviderID).setValue(provider);
                                        }
                                        else if (mUserType.equals("old_user")){
                                            Toast.makeText(VerifyPhoneActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            }, 2000);


                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            signIn.setVisibility(View.VISIBLE);
                            Toast.makeText(VerifyPhoneActivity.this, "Invalid OTP Code", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }



}