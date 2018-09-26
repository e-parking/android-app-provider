package bd.com.universal.eparking.owner.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import bd.com.universal.eparking.owner.DataModel.ParkPlace;
import bd.com.universal.eparking.owner.R;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class AddParkPlaceFragment extends Fragment implements OnMapReadyCallback {

    private DatabaseReference mFirebaseRefParkPlace;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorage;
    private StorageReference mStorageForProfile;
    private FirebaseAuth auth;

    private EditText mProviderRateET, mPlaceTitle;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Button mRegisterParkingPlace;

    private Dialog mImagePickerDialog;

    private String mProviderID;
    private String mProviderName;
    private String mProviderContactNumber;
    private String mParkPlaceID;
    private String mParkingType = "Car";
    private String mProviderAddress = "";
    private String mSelectedLatitude = "";
    private String mSelectedLongitude = "";
    private String mProviderRatePerHour = "50";
    private String mParkPlaceTitle = "";
    private String mParkPlaceHouseNo = "";
    private String mParkPlaceRoadNo = "";
    private String mParkPlaceCityName = "";
    private String mParkPlaceAreaName = "";


    private TextView mProviderNameTV;
    private TextView mProviderPhoneTV;
    private EditText mProviderAddressET;
    private EditText mProviderHouseNoET;
    private EditText mProviderRoadNoET;
    private Spinner mProviderCitySpinner;
    private Spinner mProviderAreaSpinner;
    private String mParkPlacePhotoUrl = "";

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private String thisDate;
    private String deparatureDate;
    private int year, day, month;
    private ProgressDialog progressDialog;
    private Uri picUri;
    private String mCurrentPhotoPath;
    private String mUserID;


    private String mParkPlaceEncodedPhoto;
    private DatabaseReference mFirebaseLocationUpdate;

    private final int REQUEST_CODE_PLACEPICKER = 199;
    private static LatLngBounds BOUNDS_BD;

    private GoogleMap map;
    private GoogleMapOptions options;
    private LatLng mCurrentLocation;

    private Dialog mGpsDialog;

    private Double mLatitude;
    private Double mLongitude;

    //---------------- Location Stuff ---------

    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    private Double latitude, longitude;
    private Boolean isMapInitialized = false;

    private ImageView mPickParkPlaceLocation;

    //-------------- Park Place Photo -------------------
    private Uri profileUri;
    private final int IMG4_REQUEST = 4;
    private Bitmap mBitmapImage4;
    private String mImageName4 = "null";
    private ImageView mParkingPlacePhoto;
    //---------------------------------------------------


    private List<String> mCityList = new ArrayList<>();
    private List<String> mAreaList = new ArrayList<>();

    private SearchView searchView;

    //--------------- Camera ------------------
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private android.support.v4.app.FragmentManager fm;
    private android.support.v4.app.FragmentTransaction ft;
    //Faravy
    private FirebaseAuth mAuth;


    public interface AddParkPlaceFragmentInterface {
        public void goToAddPark();
    }

    public AddParkPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_add_park, container, false);

        initView(view);
        mAuth = FirebaseAuth.getInstance();
        mProviderID = mAuth.getUid();

        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        getCameraPermission();

        mGpsDialog = new Dialog(getActivity());
        statusCheck();
        mInitializeData();

        BOUNDS_BD = new LatLngBounds(new LatLng(23.725289, 90.393089), new LatLng(23.899557, 90.408220));
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList");

        mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        Log.e(TAG, "ProviderId :" + mProviderID);
        mFirebaseRefParkPlace = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList");


        ArrayAdapter mCityAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, mCityList);
        mProviderCitySpinner.setAdapter(mCityAdapter);

        mProviderCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mParkPlaceCityName = mCityList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mParkPlaceCityName = "";
            }
        });

        ArrayAdapter mAreaAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, mAreaList);
        mProviderAreaSpinner.setAdapter(mAreaAdapter);

        mProviderAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mParkPlaceAreaName = mAreaList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mParkPlaceAreaName = "";
            }
        });


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioButton = view.findViewById(checkedId);
                mParkingType = mRadioButton.getText().toString();
            }
        });


        mPickParkPlaceLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacePickerActivity();
            }
        });


        mRegisterParkingPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParkPlaceTitle = mPlaceTitle.getText().toString();
                mParkPlaceHouseNo = mProviderHouseNoET.getText().toString();
                mParkPlaceRoadNo = mProviderRoadNoET.getText().toString();
                mProviderAddress = mProviderAddressET.getText().toString();

                addParkPlaceToFirebase();
            }
        });


        mParkingPlacePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerDialog();

            }
        });


        //----------------------------------- Location ---------------------------------------------

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    if (latitude != 0 && longitude != 0) {
                        if (isMapInitialized == false) {
                            isMapInitialized = true;
                            mGpsDialog.dismiss();
                            // TempData.latitude = latitude;
                            // TempData.longitude = longitude;
                        }

                    }
                }
            }
        };


        //------------------------------- End Of location Section ----------------------------------

        getDeviceCurrentLocation();
        getLocationUpdates();


        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(getActivity());
        mProviderRateET = view.findViewById(R.id.mProviderRateET);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        mPlaceTitle = view.findViewById(R.id.mPlaceTitleET);
        mRegisterParkingPlace = view.findViewById(R.id.mRegisterParkingPlace);
        mPickParkPlaceLocation = view.findViewById(R.id.mPickParkPlaceLocation);
        mProviderAddressET = view.findViewById(R.id.mProviderAddressET);
        mProviderHouseNoET = view.findViewById(R.id.mHouseNoET);
        mProviderRoadNoET = view.findViewById(R.id.mRoadNoET);
        mProviderCitySpinner = view.findViewById(R.id.mCitySpinner);
        mProviderAreaSpinner = view.findViewById(R.id.mAreaSpinner);
        mParkingPlacePhoto = view.findViewById(R.id.mParkingPlacePhoto);
        mProviderNameTV = view.findViewById(R.id.mName);
        mProviderPhoneTV = view.findViewById(R.id.mContactNumber);
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            return false;
        }
        return true;
    }


    private void getLocationUpdates() {
        if (checkLocationPermission()) {
            client.requestLocationUpdates(request, callback, null);
        }
    }

    private void getDeviceCurrentLocation() {
        if (checkLocationPermission()) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        return;
                    }
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //TempData.latitude = latitude;
                    //TempData.longitude = longitude;
                    mGpsDialog.dismiss();
                    getLocationUpdates();
                }
            });
        }
    }

    public void addParkPlaceToFirebase() {
        if (mParkPlaceTitle.isEmpty()) {
            mPlaceTitle.setError("Give a title in order to identify.");
        } else if (mProviderAddress.equals("")) {
            mProviderRoadNoET.setError("Please give your detailed address.");
        } else if (mSelectedLatitude.equals("") && mSelectedLongitude.equals("")) {

            mShowAddLocationAlert();

        } else if (mParkPlacePhotoUrl.isEmpty()) {
            mShowAddParkPhotoAlert();
        } else {
            progressDialog.setMessage("Adding Parking Place to Server.");
            progressDialog.show();
            //mProviderRatePerHour = mProviderRateET.getText().toString();
            mParkPlaceTitle = mPlaceTitle.getText().toString();
            mParkPlaceID = mFirebaseRefParkPlace.push().getKey();
            createNewParkPlace(new ParkPlace(mProviderID, mParkPlaceID, mParkPlaceTitle, mParkingType, "true", mParkingType, mProviderRatePerHour, mProviderAddress, mSelectedLatitude, mSelectedLongitude, "false", mParkPlaceHouseNo, mParkPlaceRoadNo, mParkPlaceCityName, mParkPlaceAreaName, mParkPlacePhotoUrl,"true"));
        }
    }

    private void createNewParkPlace(ParkPlace parkPlace) {

        mFirebaseRefParkPlace.child(mParkPlaceID).setValue(parkPlace);
        addParkPlaceChangeListener();
    }

    private void addParkPlaceChangeListener() {
        // User requestList change listener
        mFirebaseRefParkPlace.child(mParkPlaceID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ParkPlace parkPlace = dataSnapshot.getValue(ParkPlace.class);

                // Check for null
                if (parkPlace == null) {
                    Log.e(TAG, "Something went wrong Park Place is null!");
                    return;
                } else {
                    Toast.makeText(getActivity(), "New parking space added", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    mProviderRateET.setText("");
                    mPlaceTitle.setText("");

                    ft = fm.beginTransaction();
                    MyParkingPlacesFragment myParkingPlacesFragment = new MyParkingPlacesFragment();
                    ft.replace(R.id.fragmentContainer, myParkingPlacesFragment);
                    ft.addToBackStack("goToMain");
                    ft.commit();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    //--------------------------- Location Picker ---------------------
    private void startPlacePickerActivity() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(BOUNDS_BD);
        try {
            startActivityForResult(builder.build(getActivity()), REQUEST_CODE_PLACEPICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mParkPlacePhotoUrl = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 50);
            mParkingPlacePhoto.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        } else if (requestCode == IMG4_REQUEST && resultCode == RESULT_OK && data != null) {
            profileUri = data.getData();
            try {
                mBitmapImage4 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), profileUri);
                mParkingPlacePhoto.setImageBitmap(mBitmapImage4);
                mImageName4 = profileUri.getLastPathSegment();
                addProfilePictureToFirebase(profileUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addProfilePictureToFirebase(Uri uri) {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        mStorageForProfile = FirebaseStorage.getInstance().getReference();
        StorageReference filepath = mStorage.child("ParkingSpacePhotos").child(mProfileUrlMaker());

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mParkPlacePhotoUrl = taskSnapshot.getDownloadUrl().toString();
                Picasso.get().load(mParkPlacePhotoUrl).placeholder(R.drawable.ic_park_false).error(R.drawable.ic_park_false).into(mParkingPlacePhoto);
                Toast.makeText(getActivity(), "Parking space picture added successfully.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void displaySelectedPlaceFromPlacePicker(Intent data) {

        System.out.println(">>>>>>>>>>>>> " + "ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/");
        Place placeSelected = PlacePicker.getPlace(data, getActivity());
        //mProviderAddressET.setText(placeSelected.getAddress().toString());
        mProviderAddress = placeSelected.getAddress().toString();

        Toast.makeText(getActivity(), "Location selected.", Toast.LENGTH_SHORT).show();
        mLatitude = placeSelected.getLatLng().latitude;
        mLongitude = placeSelected.getLatLng().longitude;

        mSelectedLatitude = Double.toString(placeSelected.getLatLng().latitude);
        mSelectedLongitude = Double.toString(placeSelected.getLatLng().longitude);
        //innitializeMap ();
    }


    public void innitializeMap() {
        mGpsDialog.dismiss();
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        ft = fm.beginTransaction();
        ft.add(R.id.fragmentContainerOnAddPark, mapFragment);
        ft.addToBackStack("nun");
        ft.commit();
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        mCurrentLocation = new LatLng(mLatitude, mLongitude);
        map.addMarker(new MarkerOptions().position(mCurrentLocation).title("Selected Location").snippet(mProviderAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15));

    }


    public void mShowAddParkPhotoAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Please add a photo of your parking space.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pickDocument4();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


    public void mShowAddLocationAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Add Location");
        alertDialog.setMessage("Please pick your location from the map.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startPlacePickerActivity();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }



    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDialogBox();

        }
    }


    public void showGPSDialogBox() {
        mGpsDialog = new Dialog(getActivity());
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


    private void pickDocument4() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG4_REQUEST);
    }

    public String mProfileUrlMaker() {
        long time = System.currentTimeMillis();
        String millis = Long.toString(time);
        //String url = mProviderID+"<<>>"+millis;
        String url = mProviderID + "<<>>" + millis;
        return url;
    }


    public void mInitializeData() {
        mCityList.add("Select City");
        mCityList.add("Dhaka");

        mAreaList.add("Select Area");
        mAreaList.add("Adabor");
        mAreaList.add("Badda");
        mAreaList.add("Bandar");
        mAreaList.add("Bangshal");
        mAreaList.add("Biman Bandar");
        mAreaList.add("Cantonment");
        mAreaList.add("Chawkbazar");
        mAreaList.add("Dakshinkhan");
        mAreaList.add("Darus Salam");
        mAreaList.add("Demra");
        mAreaList.add("Dhanmondi");
        mAreaList.add("Gazipur Sadar");
        mAreaList.add("Gendaria");
        mAreaList.add("Gulshan");
        mAreaList.add("Hazaribagh");
        mAreaList.add("Jatrabari");
        mAreaList.add("Kadamtali");
        mAreaList.add("Kafrul");
        mAreaList.add("Kalabagan");
        mAreaList.add("Kamrangirchar");
        mAreaList.add("Keraniganj");
        mAreaList.add("Khilgaon");
        mAreaList.add("Khilkhet");
        mAreaList.add("Kotwali");
        mAreaList.add("Lalbagh");
        mAreaList.add("Mirpur");
        mAreaList.add("Mohammadpur");
        mAreaList.add("Motijheel");
        mAreaList.add("Narayanganj Sadar");
        mAreaList.add("New Market");
        mAreaList.add("Pallabi");
        mAreaList.add("Paltan");
        mAreaList.add("Ramna");
        mAreaList.add("Rampura");
        mAreaList.add("Sabujbagh");
        mAreaList.add("Savar");
        mAreaList.add("Shah Ali");
        mAreaList.add("Shahbagh");
        mAreaList.add("Sher-e-Bangla Nagar");
        mAreaList.add("Shyampur");
        mAreaList.add("Sutrapur");
        mAreaList.add("Tejgaon");
        mAreaList.add("Tejgaon Industrial Area");
        mAreaList.add("Turag");
        mAreaList.add("Uttara");
        mAreaList.add("Uttar Khan");


    }

    public void showImagePickerDialog() {
        mImagePickerDialog = new Dialog(getActivity());
        mImagePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mImagePickerDialog.setContentView(R.layout.dialog_image_picker);
        mImagePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mImagePickerDialog.setCancelable(true);


        ImageView mCamera = mImagePickerDialog.findViewById(R.id.mCameraImageView);
        ImageView mGallery = mImagePickerDialog.findViewById(R.id.mGalleryImageView);


        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                mImagePickerDialog.dismiss();
            }
        });

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDocument4();
                mImagePickerDialog.dismiss();
            }
        });
        mImagePickerDialog.show();
    }

    //---------------------- Camera -------------------------

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir/*directory*/);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {

        //openCameraForResult (REQUEST_IMAGE_CAPTURE);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }
    }


    private void openCameraForResult(int requestCode) {
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.parse("file:///sdcard/photo.jpg");
        photo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(photo, requestCode);
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
