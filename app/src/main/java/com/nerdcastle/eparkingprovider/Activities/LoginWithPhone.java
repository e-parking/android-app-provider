package com.nerdcastle.eparkingprovider.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.DataModel.Consumer;
import com.nerdcastle.eparkingprovider.DataModel.Provider;
import com.nerdcastle.eparkingprovider.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.Gravity.LEFT;

public class LoginWithPhone extends AppCompatActivity {

    @BindView(R.id.ivback)
    ImageView ivBack;

    @BindView(R.id.etPhoneNo)
    EditText etPhoneNo;

    @BindView(R.id.tvMoving)
    TextView tvMoving;

    @BindView(R.id.ivFlag)
    ImageView ivFlag;

    @BindView(R.id.tvCode)
    TextView tvCode;

    @BindView(R.id.fabProgressCircle)
    FABProgressCircle fabProgressCircle;

    @BindView(R.id.rootFrame)
    FrameLayout rootFrame;

    @BindView(R.id.llphone)
    LinearLayout llPhone;


    private Dialog mUserAlertDialog;
    //Firebase Section
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseRefConsumer;
    private DatabaseReference mFirebaseRefProvider;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    List<Provider> providerList = new ArrayList<>();
    List<Consumer> consumerList = new ArrayList<>();
    Boolean status = false;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_with_phone);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRefProvider = mFirebaseInstance.getReference("ProviderList");
        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

    }

    @OnClick(R.id.fabProgressCircle)
    void nextPager() {

        String mobile = etPhoneNo.getText().toString().trim();
        if (mobile.isEmpty() || mobile.length() < 11 || mobile.length()>11) {
            etPhoneNo.setError("Enter a valid mobile");
            etPhoneNo.requestFocus();
            return;
        }

        phoneNumber = etPhoneNo.getText().toString();
        etPhoneNo.setCursorVisible(false);
        rootFrame.setAlpha(0.4f);
        fabProgressCircle.show();
        getAllProvider();

    }

    @OnClick(R.id.ivback)
    void startReturnTransition() {
        this.finish();
    }


    public void getAllProvider() {

        status = false;
        mFirebaseRefProvider.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Provider provider = data.getValue(Provider.class);
                    System.out.println(provider);
                    providerList.add(provider);

                    if (provider != null && provider.getmPhone() != null) {
                        if (provider.getmPhone().contains(phoneNumber) || provider.getmPhone().equals("+88" + phoneNumber)){
                            status = true;
                            break;
                        }
                    }
                }

                if (status) {
                    Intent intent = new Intent(LoginWithPhone.this, VerifyPhoneActivity.class);
                    intent.putExtra("user", "old_user");
                    intent.putExtra("mobile", phoneNumber);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginWithPhone.this);
                    startActivity(intent, options.toBundle());
                } else {

                    Intent intent=new Intent(LoginWithPhone.this,VerifyPhoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("mobile", phoneNumber);
                    intent.putExtra("user", "new_user");
                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginWithPhone.this, "We cant read.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
