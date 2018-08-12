package com.nerdcastle.eparkingprovider.Fragment;



import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nerdcastle.eparkingprovider.DataModel.ParkPlace;
import com.nerdcastle.eparkingprovider.R;
import com.nerdcastle.eparkingprovider.UserPreferences;
import java.util.Calendar;

import static android.content.ContentValues.TAG;


public class AddParkPlaceFragment extends Fragment {





    private DatabaseReference mFirebaseDatabaseForMoment;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorage;
    private FirebaseAuth auth;

    private EditText mProviderRateET,mPlaceTitle;
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
    private String mParkingType = "Car";
    private String mProviderRatePerHour, mParkPlaceTitle;
    private String mParkPlaceEncodedPhoto;
    public interface AddParkPlaceFragmentInterface {
        public void goToAddPark();
    }
    public AddParkPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_park, container, false);



        auth = FirebaseAuth.getInstance();

        userPreferences = new UserPreferences(getActivity());



        mProviderID = auth.getCurrentUser().getUid();


        progressDialog = new ProgressDialog(getActivity());
        mProviderRateET = view.findViewById(R.id.mProviderRateET);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        mPlaceTitle = view.findViewById(R.id.mPlaceTitleET);
        mProviderRegisterButton = view.findViewById(R.id.mProviderRegisterButton);



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

        //initialization ();

        return view;
    }





    public void addParkPlaceToFirebase()
    {
        progressDialog.setMessage("Adding Parking Place to Server.");
        progressDialog.show();
        mProviderRatePerHour = mProviderRateET.getText().toString();
        mParkPlaceTitle = mPlaceTitle.getText().toString();

        mParkPlaceID = mFirebaseDatabaseForMoment.push().getKey();
        createNewParkPlace(new ParkPlace(mProviderID,mParkPlaceID,mParkPlaceTitle,mParkingType,"true","null",mProviderRatePerHour));

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
                    mPlaceTitle.setText("");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }





















}
