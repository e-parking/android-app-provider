package com.nerdcastle.eparkingprovider;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.Activities.LoginActivity;
import com.nerdcastle.eparkingprovider.Activities.SignUpActivity;
import com.nerdcastle.eparkingprovider.DataModel.Provider;
import com.nerdcastle.eparkingprovider.DataModel.Request;
import com.nerdcastle.eparkingprovider.DataModel.TempHolder;
import com.nerdcastle.eparkingprovider.Fragment.AddParkPlaceFragment;
import com.nerdcastle.eparkingprovider.Fragment.GarageRegistrationFragment;
import com.nerdcastle.eparkingprovider.Fragment.DashBoardFragment;
import com.nerdcastle.eparkingprovider.Fragment.MainFragment;
import com.nerdcastle.eparkingprovider.Fragment.MyParkingPlacesFragment;
import com.nerdcastle.eparkingprovider.Fragment.NotificationFragment;
import com.nerdcastle.eparkingprovider.Fragment.PaymentFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
        , GarageRegistrationFragment.GarageRegistrationFragmentInterface
        , DashBoardFragment.MainFragmentInterface
        ,MainFragment.MainFragmentInterface
        , PaymentFragment.PaymentFragmentInterface
        , AddParkPlaceFragment.AddParkPlaceFragmentInterface
        , NotificationFragment.NotificationFragmentInterface
        , MyParkingPlacesFragment.MainFragmentInterface {


    private FragmentManager fm;
    private FragmentTransaction ft;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseUserRef;
    private DatabaseReference mFirebaseRequestRef;
    private DatabaseReference mFirebaseLocationUpdate;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseUser mCurrentUser;


    //------------Custom Dialogs ---------
    private Dialog mGpsDialog;
    private Dialog mInternetDialog;
    private Dialog mAddLocationDialog;

    //---------- Navigatin Header Info ----------
    private TextView mUserName;
    private TextView mUserEmailAddress;
    private CircleImageView mProfileImage;
    //-------------------------------------------

    private static final String TAG = "MainActivity";
    private UserPreferences userPreferences;
    private static final int NOTIFICATION_ID = 1;
    private final int REQUEST_CODE_PLACEPICKER = 199;
    private static LatLngBounds BOUNDS_BD;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private String mProviderID = "";
    private Boolean mInternetStatus;

    private String mSelectedLatitude = "";
    private String mSelectedLongitude = "";
    private String mProviderAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BOUNDS_BD = new LatLngBounds(new LatLng(23.725289, 90.393089), new LatLng(23.899557, 90.408220));


        //-------------- Custom Dialog -------------------
        mGpsDialog = new Dialog(this);
        mInternetDialog = new Dialog(this);
        mAddLocationDialog = new Dialog(this);

        //------------------------------------------------------------------------------------------
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        //------------------------------------------------------------------------------------------
        mInternetStatus = isNetworkAvailable();
        if (!mInternetStatus) {
            showInternetDialogBox();
        } else {
            //--------------------------------------------------------------------------------------
            MainFragment mainFragment = new MainFragment();
            ft.replace(R.id.fragmentContainer, mainFragment);
            ft.commit();
        }


        //statusCheck();
        //------------------------------------------------------------------------------------------
        userPreferences = new UserPreferences(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProviderID = mAuth.getUid();
        mCurrentUser = mAuth.getCurrentUser();
        //------------------------------------------------------------------------------------------

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        mUserName = header.findViewById(R.id.mUserNameOnHeader);
        mUserEmailAddress = header.findViewById(R.id.mUserEmailOnHeader);
        mProfileImage = header.findViewById(R.id.circularImageView);
        //Picasso.get().load(mCurrentUser.getPhotoUrl()).into(mProfileImage);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
            }
        });

        /*if (mCurrentUser.getDisplayName()==null)
        {
            mUserName.setText(TempHolder.mUserName);

        }else
        {
            mUserName.setText(mCurrentUser.getDisplayName());
        }

        mUserEmailAddress.setText(mCurrentUser.getEmail());*/


        //------------------------- Nottification --------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

/*

        FirebaseMessaging.getInstance().subscribeToTopic("news").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
*/


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Not ready yet", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        addRequestChangeListener();
        getUserInformations();
    }


    @Override
    protected void onStart() {
        getUserInformations();
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            goToDashBoard();
        } else if (id == R.id.nav_requests) {
            goToNotification();
        } else if (id == R.id.nav_payments) {
            goToPayment();
        } else if (id == R.id.nav_addParkPlace) {
            //goToAddPark();
            goToMyParkingPlace();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {

            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void signOut() {

        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }


    private void addRequestChangeListener() {
        // User requestList change listener
        String mProviderID = mAuth.getCurrentUser().getUid();
        System.out.println(">>>>>>>>>>>>>> Provider ID >>>>>>>> " + mProviderID);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRequestRef = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/Request");
        mFirebaseRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Request request = data.getValue(Request.class);
                    System.out.println(">>>>>>>>>>>>>> Sent By >>>>>>>> " + request);
                    setNotification(request.getmRequstSenderName(), "Wants to park a " + request.getmVehicleType());
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    @Override
    public void goToAddPark() {

        ft = fm.beginTransaction();
        AddParkPlaceFragment addParkPlaceFragment = new AddParkPlaceFragment();
        ft.replace(R.id.fragmentContainer, addParkPlaceFragment);
        ft.addToBackStack("goToAddPark");
        ft.commit();

    }


    @Override
    public void goToParkRegistration() {
        ft = fm.beginTransaction();
        GarageRegistrationFragment garageRegistrationFragment = new GarageRegistrationFragment();
        ft.replace(R.id.fragmentContainer, garageRegistrationFragment);
        ft.addToBackStack("goToParkRegistration");
        ft.commit();
    }

    @Override
    public void goToDashBoard() {
        ft = fm.beginTransaction();
        DashBoardFragment dashBoardFragment = new DashBoardFragment();
        ft.replace(R.id.fragmentContainer, dashBoardFragment);
        ft.addToBackStack("goToDashBoard");
        ft.commit();
    }

    @Override
    public void goToMain() {
        ft = fm.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        ft.replace(R.id.fragmentContainer, mainFragment);
        ft.addToBackStack("goToMain");
        ft.commit();
    }

    @Override
    public void goToMyParkingPlace() {
        ft = fm.beginTransaction();
        MyParkingPlacesFragment myParkingPlacesFragment = new MyParkingPlacesFragment();
        ft.replace(R.id.fragmentContainer, myParkingPlacesFragment);
        ft.addToBackStack("goToMyPlace");
        ft.commit();
    }

    @Override
    public void goToPayment() {
        ft = fm.beginTransaction();
        PaymentFragment paymentFragment = new PaymentFragment();
        ft.replace(R.id.fragmentContainer, paymentFragment);
        ft.addToBackStack("goToPayment");
        ft.commit();
    }


    public void setNotification(String title, String msg) {


        Intent notificationIntent = new Intent(this, HomeActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        //Uri uri = Uri.parse("android.resource://"+this.getPackageName()+"/" + R.raw.notification);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes.Builder attrs = new AudioAttributes.Builder();
            attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attrs.setUsage(AudioAttributes.USAGE_NOTIFICATION);

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            //notificationChannel.setSound(uri,attrs.build());
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_icon);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                //.setSound(uri)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_small_notifiaction_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentIntent(intent)
                .setContentText(msg);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    @Override
    public void goToNotification() {
        ft = fm.beginTransaction();
        NotificationFragment notificationFragment = new NotificationFragment();
        ft.replace(R.id.fragmentContainer, notificationFragment);
        ft.addToBackStack("goToNotification");
        ft.commit();
    }


    public void getUserInformations() {

        mFirebaseUserRef = mFirebaseInstance.getReference("ProviderList/" + mProviderID);
        System.out.println(">>>>>>>>>>>>>> Get User  Called");
        mFirebaseUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TempHolder.mProvider = dataSnapshot.getValue(Provider.class);
                System.out.println(">>>>>>>>>>>>>> Get Status Called  from firebase");
                if (TempHolder.mProvider != null) {

                    if (!TempHolder.mProvider.getmName().equals("")) {
                        mUserName.setText(TempHolder.mProvider.getmName());
                    }

                    if (!TempHolder.mProvider.getmEmail().contains("@mail.com")) {
                        mUserEmailAddress.setText(TempHolder.mProvider.getmEmail());
                    }

                    if (!TempHolder.mProvider.getmProfilePhoto().equals("")) {
                        Picasso.get().load(TempHolder.mProvider.getmProfilePhoto()).into(mProfileImage);
                    } else {
                        mProfileImage.setImageResource(R.drawable.profile);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event data: " + databaseError.getCode());
            }
        });
    }

    //----------------------- Initial Device Status check --------------------------


    public void showGPSDialogBox() {
        mGpsDialog = new Dialog(this);
        mGpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGpsDialog.setContentView(R.layout.dialog_gps);
        mGpsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mGpsDialog.setCancelable(true);

        TextView mSetting = mGpsDialog.findViewById(R.id.mTurnOnSetting);

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        mGpsDialog.show();
    }


    public void showInternetDialogBox() {
        mInternetDialog = new Dialog(this);
        mInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mInternetDialog.setContentView(R.layout.dialog_internet);
        mInternetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mInternetDialog.setCancelable(false);

        TextView mRefresh = mInternetDialog.findViewById(R.id.mTurnOnInternet);

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
        });
        mInternetDialog.show();
    }


    public void showAddLocationDialogBox() {
        mAddLocationDialog = new Dialog(this);
        mAddLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAddLocationDialog.setContentView(R.layout.dialog_add_location);
        mAddLocationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mAddLocationDialog.setCancelable(false);

        TextView mAddLocation = mAddLocationDialog.findViewById(R.id.mAddLocation);

        mAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacePickerActivity();
                mAddLocationDialog.dismiss();
            }
        });
        mAddLocationDialog.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public Boolean mGPSstatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDialogBox();

            return false;
        }
        return true;
    }


    //------------------- onActivityResult ---------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            addParkPlaceLocation(data);
        }
    }

    //--------------------------- Location Picker ---------------------
    private void startPlacePickerActivity() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(BOUNDS_BD);
        try {
            startActivityForResult(builder.build(this), REQUEST_CODE_PLACEPICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    private void displaySelectedPlaceFromPlacePicker(Intent data) {

        Place placeSelected = PlacePicker.getPlace(data, this);
        String address = placeSelected.getAddress().toString();
        mSelectedLatitude = Double.toString(placeSelected.getLatLng().latitude);
        mSelectedLongitude = Double.toString(placeSelected.getLatLng().longitude);
        mProviderAddress = address;
        mFirebaseLocationUpdate = mFirebaseInstance.getReference("ProviderList/" + mProviderID);

        mFirebaseLocationUpdate.child("mAddress").setValue(mProviderAddress);
        mFirebaseLocationUpdate.child("mLatitude").setValue(mSelectedLatitude);
        mFirebaseLocationUpdate.child("mLongitude").setValue(mSelectedLongitude);
    }


    private void addParkPlaceLocation(Intent data) {

        System.out.println(">>>>>>>>>>>>> " + "ProviderList/" + mProviderID + "/ParkPlaceList/" + TempHolder.mParkPlaceID + "/");
        Place placeSelected = PlacePicker.getPlace(data, this);
        String mProviderAddress = placeSelected.getAddress().toString();
        String mSelectedLatitude = Double.toString(placeSelected.getLatLng().latitude);
        String mSelectedLongitude = Double.toString(placeSelected.getLatLng().longitude);

        if (!TempHolder.mParkPlaceID.equals("")) {
            mFirebaseLocationUpdate = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + TempHolder.mParkPlaceID + "/");

            mFirebaseLocationUpdate.child("mParkingIsApproved").setValue("false");
            mFirebaseLocationUpdate.child("mAddress").setValue(mProviderAddress);
            mFirebaseLocationUpdate.child("mLatitude").setValue(mSelectedLatitude);
            mFirebaseLocationUpdate.child("mLongitude").setValue(mSelectedLongitude);
            TempHolder.mParkPlaceID = "";
        }
    }

}
