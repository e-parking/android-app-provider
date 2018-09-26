package bd.com.universal.eparking.owner.Fragment;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import bd.com.universal.eparking.owner.DataModel.ParkPlace;
import bd.com.universal.eparking.owner.R;
import bd.com.universal.eparking.owner.UserPreferences;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


public class GarageRegistrationFragment extends Fragment {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_TAKE_PHOTO = 200;

    private static final int REQUEST_IMAGE_CAPTURE = 111;



    private DatabaseReference mFirebaseDatabaseForMoment;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorage;

    private final int IMG1_REQUEST = 1;
    private final int IMG2_REQUEST = 2;
    private final int IMG3_REQUEST = 3;


    private ImageButton mParkingPlacePhoto;
    private EditText mProviderRateET;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Button mProviderRegisterButton;

    private String mProviderID;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String thisDate;
    private String deparatureDate;
    private int year,day,month;
    private ProgressDialog progressDialog;
    private Uri picUri;
    private String mCurrentPhotoPath;
    private UserPreferences userPreferences;
    private String mUserID;
    private String mParkPlaceID;
    private String mParkingType;
    private String mProviderRatePerHour;
    private String mParkPlaceEncodedPhoto;
    public interface GarageRegistrationFragmentInterface {
        public void goToParkRegistration();
    }
    public GarageRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_garage_registration, container, false);




        userPreferences = new UserPreferences(getActivity());



        mProviderID = userPreferences.getUserID();
        if (mProviderID.equals("null"))
        {
            mProviderID = "-LEEDH6ryQ9l1-OWAAiF";
        }

        progressDialog = new ProgressDialog(getActivity());
        mParkingPlacePhoto = view.findViewById(R.id.mParkingPlacePhoto);
        mProviderRateET = view.findViewById(R.id.mProviderRateET);
        mRadioGroup = view.findViewById(R.id.radioGroup);


        mProviderRegisterButton = view.findViewById(R.id.mProviderRegisterButton);




        //------------- Getting Time --------------
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);




        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList");

        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseForMoment = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList");




        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioButton = view.findViewById(checkedId);
                mParkingType = mRadioButton.getText().toString();
                Toast.makeText(getActivity(), "You Selected "+mRadioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });



        mProviderRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParkPlaceToFirebase();
                }
            });

        mParkingPlacePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });


        getCameraPermission();

        return view;
    }











    public void addParkPlaceToFirebase()
    {
        progressDialog.setMessage("Adding Parking Place to Server.");
        progressDialog.show();
        Uri uri = picUri;
        mProviderRatePerHour = mProviderRateET.getText().toString();
        mParkPlaceID = mFirebaseDatabaseForMoment.push().getKey();
        createNewParkPlace(new ParkPlace(mProviderID,mParkPlaceID,"null",mParkingType,"true",mParkPlaceEncodedPhoto,mProviderRatePerHour));
    }

    private void createNewParkPlace(ParkPlace parkPlace) {

        mFirebaseDatabaseForMoment.child(mParkPlaceID).setValue(parkPlace);
        addParkPlaceChangeListener();
    }

    private void addParkPlaceChangeListener() {
        // User requestList change listener
        mFirebaseDatabaseForMoment.child(mParkPlaceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ParkPlace parkPlace = dataSnapshot.getValue(ParkPlace.class);

                // Check for null
                if (parkPlace == null) {
                    Log.e(TAG, "Something went wrong Park Place is null!");
                    return;
                }else
                {
                    Toast.makeText(getActivity(), "New ParkPlace Added", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    mProviderRateET.setText("");
                    mParkingPlacePhoto.setImageResource(R.drawable.ic_add_picture);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("requestList");
            mParkingPlacePhoto.setImageBitmap(imageBitmap);
            encodeBitmap(imageBitmap);


        }
    }


    public void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        mParkPlaceEncodedPhoto = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        System.out.println(mParkPlaceEncodedPhoto);
    }




    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    public void getCameraPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }
        }
    }




}
