package com.nerdcastle.eparkingprovider.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nerdcastle.eparkingprovider.DataModel.Provider;
import com.nerdcastle.eparkingprovider.DataModel.TempHolder;
import com.nerdcastle.eparkingprovider.HomeActivity;
import com.nerdcastle.eparkingprovider.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.TooManyListenersException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class SignUpActivity extends AppCompatActivity {


    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mFirebaseUserInformation;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorage;
    private StorageReference mStorageForProfile;
    private FirebaseUser mFirebaseUser;

    private final int IMG1_REQUEST = 1;
    private final int IMG2_REQUEST = 2;
    private final int IMG3_REQUEST = 3;
    private final int IMG4_REQUEST = 4;

    private Bitmap mBitmapImage1;
    private Bitmap mBitmapImage2;
    private Bitmap mBitmapImage3;
    private Bitmap mBitmapImage4;

    private String mImageName1 = "null";
    private String mImageName2 = "null";
    private String mImageName3 = "null";
    private String mImageName4 = "null";

    private static  LatLngBounds BOUNDS_BD;

    private CircleImageView mProviderProfilePicture;
    private EditText mProviderName,mProviderEmail,mProviderPhone,mProviderAddress, mProviderNID, mProviderPassword1,mProviderPassword2;
    private Button mProviderRegisterButton,mProviderProfilePicker;
    private ImageButton mProviderDocument1, mProviderDocument2,mProviderDocument3;
    private ImageView mMapPicker;
    private ProgressDialog progressDialog;

    private String mProviderID, mSelectedLatitude ="0", mSelectedLongitude="0";
    private final int REQUEST_CODE_PLACEPICKER = 199;
    private FirebaseAuth mAuth;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String mThisDate;
    private int year,day,month;

    private Uri profileUri,documentUri1,documentUri2,documentUri3;
    private String mProfileURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        //----------------- Firebase initializations ------------------------
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mProviderID = mAuth.getUid();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList");
        mStorage = FirebaseStorage.getInstance().getReference();
        getUserInformation();

        BOUNDS_BD =  new LatLngBounds(new LatLng(23.725289 , 90.393089), new LatLng(23.899557,90.408220 ));

        mProviderName = findViewById(R.id.mProviderNameET);
        mProviderEmail = findViewById(R.id.mProviderEmailET);
        mProviderPhone = findViewById(R.id.mProviderPhoneET);
        mProviderAddress = findViewById(R.id.mProviderAddressET);
        mProviderNID = findViewById(R.id.mProviderNidET);
        mProviderPassword1 = findViewById(R.id.mProviderPassword1ET);
        mProviderPassword2 = findViewById(R.id.mProviderPassword2ET);


        mProviderRegisterButton = findViewById(R.id.mProviderRegisterButton);
        mProviderProfilePicker = findViewById(R.id.mProviderProfilePicker);

        mProviderDocument1 = findViewById(R.id.mDocument1);
        mProviderDocument2 = findViewById(R.id.mDocument2);
        mProviderDocument3 = findViewById(R.id.mDocument3);

        mMapPicker = findViewById(R.id.mMapPickerButton);
        mProviderProfilePicture = findViewById(R.id.mProviderProfile);



        mProviderPhone.setCursorVisible(false);
        mProviderPhone.setLongClickable(false);
        mProviderPhone.setClickable(false);
        mProviderPhone.setFocusable(false);
        mProviderPhone.setSelected(false);
        mProviderPhone.setKeyListener(null);



        //------------------------Profile Image load ----------------
        if (mAuth.getCurrentUser().getPhotoUrl() != null)
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.profile).error(R.drawable.profile).into(mProviderProfilePicture);
        //-----------------------------------------------------------


        mProviderProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocument4();
            }
        });

        mProviderProfilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocument4();
            }
        });


        mMapPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacePickerActivity();
            }
        });

        mProviderDocument1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocument1();
            }
        });

        mProviderDocument2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocument2();
            }
        });

        mProviderDocument3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocument3();
            }
        });


        mProviderRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showExitDialog();

            }
        });




    }




    public void mUpdateProfile()
    {
        mFirebaseUserInformation  = mFirebaseInstance.getReference("ProviderList/"+mProviderID);
        progressDialog.setMessage("We are updating your information.");
        progressDialog.show();

        final String name = mProviderName.getText().toString();
        final String email = mProviderEmail.getText().toString();
        final String password1 = mProviderPassword1.getText().toString();
        final String password2 = mProviderPassword2.getText().toString();
        final String phone = mProviderPhone.getText().toString();
        final String address = mProviderAddress.getText().toString();
        final String nationalid = mProviderNID.getText().toString();
        TempHolder.mUserName = name;


        mFirebaseUserInformation.child("mName").setValue(name);
        mFirebaseUserInformation.child("mAddress").setValue(address);
        mFirebaseUserInformation.child("mEmail").setValue(email);
        mFirebaseUserInformation.child("mNationalID").setValue(nationalid);
        mFirebaseUserInformation.child("mNationalID").setValue(nationalid);
        mFirebaseUserInformation.child("mPassword").setValue(password1);
        mFirebaseUserInformation.child("mPhoto").setValue(mProfileURL);


        Provider provider;
        //createNewProvider(provider);




        if (mSelectedLatitude.equals("0") && mSelectedLongitude.equals("0"))
        {
            mFirebaseUserInformation.child("mLatitude").setValue(TempHolder.mProvider.getmLatitude());
            mFirebaseUserInformation.child("mLongitude").setValue(TempHolder.mProvider.getmLongitude());
            provider= new Provider(mProviderID, name, mImageName4, mImageName1, mImageName2, mImageName3, address, email, phone, password1, nationalid, mThisDate, TempHolder.mProvider.getmLatitude(), TempHolder.mProvider.getmLongitude());

        }else
        {
            mFirebaseUserInformation.child("mLatitude").setValue(mSelectedLatitude);
            mFirebaseUserInformation.child("mLongitude").setValue(mSelectedLongitude);
            provider = new Provider(mProviderID, name, mProfileURL, mImageName1, mImageName2, mImageName3, address, email, phone, password1, nationalid, mThisDate, mSelectedLatitude, mSelectedLongitude);

        }


        TempHolder.mProvider = provider;

        mUpdateAllInputFields();

        // clear edit text
        mProviderName.setText("");
        mProviderEmail.setText("");
        mProviderPassword1.setText("");
        mProviderPassword1.setText("");
        mProviderPhone.setText("");
        mProviderAddress.setText("");
        mProviderNID.setText("");

        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        progressDialog.dismiss();
        finish();


        /*
      if (TextUtils.isEmpty(name))
      {
          mProviderName.setError("Put your name here.");

      }else if (TextUtils.isEmpty(email))
      {
          mProviderEmail.setError("Put your email address.");

      }else if (TextUtils.isEmpty(password1))
      {
          mProviderPassword1.setError("Put your password.");

      }else if (TextUtils.isEmpty(password2))
      {
          mProviderPassword2.setError("Confirm your password.");

      }else if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !password1.equals(password2) )
      {
          mProviderPassword2.setError("Please match your password again.");
          mProviderPassword2.setText("");

      }else if (TextUtils.isEmpty(phone))
      {

          mProviderPhone.setError("Put your phone number.");

      }else if ( !TextUtils.isEmpty(phone) && phone.length() < 11)
      {

          mProviderPhone.setError("Phone number is wrong. Put 11 digit phone number.");

      }else if (TextUtils.isEmpty(address))
      {
          mProviderAddress.setError("Pick your address with the button.");

      }else if (mSelectedLatitude.equals("0") || mSelectedLongitude.equals("0"))
      {
          mProviderAddress.setError("Pick your address with the blue button on the right.");

      }else if (TextUtils.isEmpty(nationalid))
      {
          mProviderNID.setError("Put your national ID.");

      } else {
          //create user
            mAuth.createUserWithEmailAndPassword(email, password1)
                  .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          Date todayDate = new Date();
                          long currentDayMillis = todayDate.getTime();
                          mThisDate = Long.toString(currentDayMillis);

                          // If sign in fails, display a message to the user. If sign in succeeds
                          // the mAuth state listener will be notified and logic to handle the
                          // signed in user can be handled in the listener.
                          if (!task.isSuccessful()) {
                              Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                          } else {
                              final Handler handler = new Handler();
                              handler.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {

                                      if (mAuth !=null)
                                      {
                                          mProviderID = mAuth.getUid();
                                          FirebaseUser user = mAuth.getCurrentUser();
                                          UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                          user.updateProfile(profileUpdates);
                                          Provider provider = new Provider(mProviderID, name, mImageName4, mImageName1, mImageName2, mImageName3, address, email, phone, password1, nationalid, mThisDate, mSelectedLatitude, mSelectedLongitude);
                                          createNewProvider(provider);
                                      }
                                  }
                              }, 2000);
                          }
                      }
                  });
            }*/



    }

    private void createNewProvider(Provider provider) {

        mFirebaseDatabase.child(mProviderID).setValue(provider);
        addUserChangeListener();
    }


    private void addUserChangeListener() {
        // User requestList change listener
        mFirebaseDatabase.child(mProviderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Provider provider = dataSnapshot.getValue(Provider.class);

                // Check for null
                if (provider == null) {
                    Log.e(TAG, "Provider is null!");
                    return;
                }else

                    Toast.makeText(SignUpActivity.this, "Profile successfully updated.", Toast.LENGTH_SHORT).show();

                // clear edit text
                mProviderName.setText("");
                mProviderEmail.setText("");
                mProviderPassword1.setText("");
                mProviderPassword1.setText("");
                mProviderPhone.setText("");
                mProviderAddress.setText("");
                mProviderNID.setText("");

                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                progressDialog.dismiss();
                finish();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }




    public void addPhotoToFirebase(Uri uri)
    {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        mStorageForProfile = FirebaseStorage.getInstance().getReference();
        StorageReference filepath  =  mStorage.child("Photos").child(uri.getLastPathSegment());

        filepath.putFile(uri).addOnSuccessListener(new    OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignUpActivity.this, "Upload Successful!",    Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }



    public void addProfilePictureToFirebase(Uri uri)
    {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        mStorageForProfile = FirebaseStorage.getInstance().getReference();
        StorageReference filepath  =  mStorage.child("ProviderProfilePictues").child(mProfileUrlMaker());

        filepath.putFile(uri).addOnSuccessListener(new    OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProfileURL = taskSnapshot.getDownloadUrl().toString();
                mFirebaseUserInformation.child("mProfilePhoto").setValue(mProfileURL);
                Toast.makeText(SignUpActivity.this, "Profile picture added successfully.",    Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG1_REQUEST && resultCode == RESULT_OK && data !=null)
        {
             documentUri1 = data.getData();
            try {
                mBitmapImage1 = MediaStore.Images.Media.getBitmap(getContentResolver(),documentUri1);
                mProviderDocument1.setImageBitmap(mBitmapImage1);
                mImageName1 = documentUri1.getLastPathSegment();
                addPhotoToFirebase(documentUri1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == IMG2_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            documentUri2 = data.getData();
            try {
                mBitmapImage2 = MediaStore.Images.Media.getBitmap(getContentResolver(),documentUri2);
                mProviderDocument2.setImageBitmap(mBitmapImage2);
                mImageName2 = documentUri2.getLastPathSegment ();
                addPhotoToFirebase(documentUri2);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == IMG3_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            documentUri3 = data.getData();
            try {
                mBitmapImage3 = MediaStore.Images.Media.getBitmap(getContentResolver(),documentUri3);
                mProviderDocument3.setImageBitmap(mBitmapImage3);
                mImageName3 = documentUri3.getLastPathSegment();
                addPhotoToFirebase(documentUri3);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == IMG4_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            profileUri = data.getData();
            try {
                mBitmapImage4 = MediaStore.Images.Media.getBitmap(getContentResolver(),profileUri);
                mProviderProfilePicture.setImageBitmap(mBitmapImage4);
                mImageName4 = profileUri.getLastPathSegment();
                addProfilePictureToFirebase(profileUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }
    }




    private void pickDocument1()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG1_REQUEST);
    }

    private void pickDocument2()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG2_REQUEST);
    }

    private void pickDocument3()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG3_REQUEST);
    }

    private void pickDocument4()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG4_REQUEST);
    }




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

        /*int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/


        Place placeSelected = PlacePicker.getPlace(data, this);
        String address = placeSelected.getAddress().toString();
        mSelectedLatitude = Double.toString(placeSelected.getLatLng().latitude);
        mSelectedLongitude = Double.toString(placeSelected.getLatLng().longitude);
        mProviderAddress.setText(address);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Location>>>>>>>>>>>>>>  "+ mSelectedLatitude +"  "+ mSelectedLongitude);
    }


/*    private String nameGenarator ()
    {
        final long time= System.currentTimeMillis();
        System.out.println(String.valueOf(time));
        return String.valueOf(time);
    }*/




    public void getUserInformation ()
    {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("We are Updating your profile...");
        progressDialog.show();

        mFirebaseUserInformation  = mFirebaseInstance.getReference("ProviderList/"+mProviderID);
        System.out.println(">>>>>>>>>>>>>> Get Status Called");
        mFirebaseUserInformation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                System.out.println(">>>>>>>>>>>>>> Get Status Called  from firebase");
                if (dataSnapshot.getValue(Provider.class) != null)
                {
                    TempHolder.mProvider = dataSnapshot.getValue(Provider.class);
                    mUpdateAllInputFields ();
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> USER : "+TempHolder.mProvider);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event data: " + databaseError.getCode());
            }
        });
    }



    public void mUpdateAllInputFields ()
    {

        if (TempHolder.mProvider != null)
        {
            mProviderName.setText(TempHolder.mProvider.getmName());
            if (!TempHolder.mProvider.getmEmail().contains("@mail.com"))
            {
                mProviderEmail.setText(TempHolder.mProvider.getmEmail());
            }

            if (!TempHolder.mProvider.getmProfilePhoto().equals(""))
            {
                Picasso.get().load(TempHolder.mProvider.getmProfilePhoto()).placeholder(R.drawable.profile).error(R.drawable.profile).into(mProviderProfilePicture);
            }else
            {
                mProviderProfilePicture.setImageResource(R.drawable.profile);
            }


            mProviderPassword1.setText(TempHolder.mProvider.getmPassword());
            mProviderPhone.setText(TempHolder.mProvider.getmPhone());
            mProviderAddress.setText(TempHolder.mProvider.getmAddress());
            mProviderNID.setText(TempHolder.mProvider.getmNationalID());
            progressDialog.dismiss();
        }

    }


    @Override
    public void onBackPressed() {
        showExitDialog ();
        return;
    }


    public void showExitDialog ()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);

        Button mYesButton = dialog.findViewById(R.id.yesButton);
        Button mNoButton = dialog.findViewById(R.id.noButton);
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateProfile();
                dialog.dismiss();
            }
        });


        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public String mProfileUrlMaker ()
    {
        long time= System.currentTimeMillis();
        String millis =  Long.toString(time);
        //String url = mProviderID+"<<>>"+millis;
        String url = mProviderID+"<<>>"+millis;
        return url;
    }
}
