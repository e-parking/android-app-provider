package bd.com.universal.eparking.owner.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import bd.com.universal.eparking.owner.DataModel.Consumer;
import bd.com.universal.eparking.owner.DataModel.Provider;

import java.util.ArrayList;
import java.util.List;

import bd.com.universal.eparking.owner.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private Dialog mInternetDialog;
    private Boolean mInternetStatus;

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
        mInternetStatus = isNetworkAvailable();
        if (mInternetStatus == true){
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
                        intent.putExtra("mobile", phoneNumber);
                        intent.putExtra("user", "new_user");
                        startActivity(intent);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginWithPhone.this, "We can't your data read.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            showInternetDialogBox();
        }
        }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showInternetDialogBox ()
    {
        mInternetDialog = new Dialog(this);
        mInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mInternetDialog.setContentView(R.layout.dialog_internet);
        mInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mInternetDialog.setCancelable(false);

        TextView mRefresh = mInternetDialog.findViewById(R.id.mTurnOnInternet);

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInternetDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),LoginWithPhone.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        mInternetDialog.show();
    }


}
