package com.nerdcastle.eparkingprovider.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.DataModel.ParkPlace;
import com.nerdcastle.eparkingprovider.DataModel.Process;
import com.nerdcastle.eparkingprovider.DataModel.Request;
import com.nerdcastle.eparkingprovider.DataModel.TempHolder;
import com.nerdcastle.eparkingprovider.DataModel.TransactionHistory;
import com.nerdcastle.eparkingprovider.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MyAllPlacesAdapter extends RecyclerView.Adapter<MyAllPlacesAdapter.Viewholder> {

    private List<ParkPlace> parkPlaceList;
    private ParkPlace model;
    private static Context context;
    private Activity mActivity;
    private static Request mRequest;
    private Dialog mAddLocationDialog;
    private DatabaseReference mFirebaseLocationUpdate;

    //------------ Firebase -----------------------------
    public static FirebaseDatabase mFirebaseInstance;
    public static DatabaseReference mDatabaseRequestInfo;

    public static DatabaseReference mDatabaseConsumerStatus;
    public static DatabaseReference mDatabaseProviderStatus;
    public static DatabaseReference mDatabaseProcess;

    public static DatabaseReference mDatabaseTransactionConsumer;
    public static DatabaseReference mDatabaseTransactionProvider;
    public static DatabaseReference mDatabaseTransactionMain;

    public static DatabaseReference mDatabaseAcceptedRequest;
    public static DatabaseReference mDatabaseRemoveParkPlaceItem;


    //-------------- Transaction -------------------------

    public static FirebaseAuth mAuth;
    public static String mProviderID;
    public static String mProviderName;
    public static String mConsumerID = "";
    public static String mParkPlaceRate = "";
    public static String mConsumerName;
    public static String mConsumerPhone;
    public static String mParkPlaceID;
    public static String mRequestID;
    public static Process mProcess;

    public static ProgressDialog progressDialog;
    long MillisecondTime;
    long StartTime;
    Handler handler;
    boolean isStarted = false;
    long mStartedTime;
    long mCurrentTime;
    public static String mTransactionUiqueKey = "";
    public static TransactionHistory mTransactionHistory;


    private final int REQUEST_CODE_PLACEPICKER = 200;
    private static LatLngBounds BOUNDS_BD;

    private long mHour;
    private long mMinute;
    private long mSecond;
    private PlaceClickListener listener;


    public MyAllPlacesAdapter(List<ParkPlace> parkPlaceList, Context context, Activity mActivity,PlaceClickListener listener) {
        this.parkPlaceList = parkPlaceList;
        this.context = context;
        this.listener=listener;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProviderID = mAuth.getCurrentUser().getUid();
        mProviderName = mAuth.getCurrentUser().getDisplayName();
        this.mActivity = mActivity;
        BOUNDS_BD = new LatLngBounds(new LatLng(23.725289, 90.393089), new LatLng(23.899557, 90.408220));
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_my_places, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

        model = parkPlaceList.get(position);
        holder.momentImageView.setImageResource(R.drawable.parking_icon);
        holder.momentTitle.setText(model.getmParkPlaceTitle());

    }


    public class Viewholder extends RecyclerView.ViewHolder {

        public ImageView momentImageView;
        public TextView momentTitle;

        public Viewholder(final View itemView) {
            super(itemView);

            momentImageView = (ImageView) itemView.findViewById(R.id.momentImageView);
            momentTitle = (TextView) itemView.findViewById(R.id.mPlaceStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*mParkPlaceRate = "";
                    if (!parkPlaceList.get(getAdapterPosition()).getmParkedVehicle().equals("null")) {

                        mRequestID = parkPlaceList.get(getAdapterPosition()).getmParkedVehicle();
                        mParkPlaceID = parkPlaceList.get(getAdapterPosition()).getmParkPlaceID();
                        mProcess = null;
                        mParkPlaceRate = parkPlaceList.get(getAdapterPosition()).getmParkingChargePerHour();

                        getParkedUserInfo(mParkPlaceID, mRequestID);
                    }*/
                    listener.onPlaceClicked(parkPlaceList.get(getAdapterPosition()));
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return parkPlaceList.size();
    }


    public void showPlaceInfo(final Context context, final Request mRequest) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_start_park);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        final ImageView mConsumerProfilePicture = (ImageView) dialog.findViewById(R.id.mUserProfilePicture);
        final ImageView mCallIcon = (ImageView) dialog.findViewById(R.id.mCallIcon);
        final TextView mUserName = (TextView) dialog.findViewById(R.id.mUserName);
        final TextView mVehicleNumber = (TextView) dialog.findViewById(R.id.mVehicleNumber);
        final TextView mPhoneNumber = (TextView) dialog.findViewById(R.id.mPhoneNumber);
        final TextView mDuration = (TextView) dialog.findViewById(R.id.durationTV);
        Button mCancelButton = (Button) dialog.findViewById(R.id.mCancelButton);
        final Button mStartButton = (Button) dialog.findViewById(R.id.mStartButton);
        final Button mEndButton = (Button) dialog.findViewById(R.id.mEndButton);

        mUserName.setText(mRequest.getmRequstSenderName());
        mPhoneNumber.setText(mRequest.getmRequstSenderPhone());
        mVehicleNumber.setText(mRequest.getmVehicleNumber());
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(mConsumerProfilePicture);


        mCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(mPhoneNumber.getText().toString());
            }
        });
        mPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(mPhoneNumber.getText().toString());
            }
        });

        System.out.println(">>>>>>>>>>>>>> Dialog Opend");
        mStartedTime = 0;
        //------------------------------------------------

        if (mProcess != null) {
            if (mProcess.getIsStarted().equals("true")) {
                isStarted = true;
            } else {
                isStarted = false;
            }

            mStartedTime = Long.parseLong(mProcess.getmStartedTime());
        }

        //----------------- Thread ------------------------
        final Runnable runnable = new Runnable() {


            public void run() {
                if (isStarted) {
                    if (mStartedTime == 0) {
                        MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                        mStartedTime = StartTime;
                    } else {
                        MillisecondTime = SystemClock.uptimeMillis() - mStartedTime;
                    }

                    mHour = (MillisecondTime / (1000 * 60 * 60)) % 24;
                    mMinute = (MillisecondTime / (1000 * 60)) % 60;
                    mSecond = (MillisecondTime / 1000) % 60;


                    mDuration.setText("" + String.format("%02d", mHour) + ":"
                            + String.format("%02d", mMinute) + ":"
                            + String.format("%02d" + "s", mSecond));
                    handler.postDelayed(this, 0);
                }

            }

        };


        if (mStartedTime != 0) {
            StartTime = SystemClock.uptimeMillis();
            handler = new Handler();
            handler.postDelayed(runnable, 0);
            mEndButton.setVisibility(View.VISIBLE);
            mStartButton.setVisibility(View.GONE);
        } else {
            mEndButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.VISIBLE);
        }


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = true;
                StartTime = SystemClock.uptimeMillis();
                handler = new Handler();
                handler.postDelayed(runnable, 0);

                mEndButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.GONE);


                Process mProcess = new Process(mConsumerID + "<<>>" + mProviderID, "true", Long.toString(StartTime), "", "", "", mProviderID, mProviderName, mParkPlaceID, mRequestID, mConsumerID, mConsumerName, mConsumerPhone, mRequest.getmVehicleNumber(), mRequest.getmVehicleType());

                mStartProcess(mProcess);

            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCurrentTime = System.currentTimeMillis();
                isStarted = false;
                mStartedTime = 0;
                mEndButton.setVisibility(View.GONE);
                mStartButton.setVisibility(View.VISIBLE);


                mShowAlertDialog();
                dialog.dismiss();

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public static void mShowFinishDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_park_session_end);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final RatingBar mRatingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        final Button mFinishButton = (Button) dialog.findViewById(R.id.mFinishButton);
        final TextView mUserName = (TextView) dialog.findViewById(R.id.mUserNameTV);
        final TextView mFare = (TextView) dialog.findViewById(R.id.mFareTV);


        mUserName.setText(TempHolder.mProvider.getmName());
        mFare.setText(mTransactionHistory.getmTransactionAmount());
        mTransactionHistory = null;

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void getParkedUserInfo(String mParkPlaceID, final String mRequestID) {
        System.out.println("Got Request ID in the method." + mRequestID);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseRequestInfo = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/Request/");
        progressDialog = ProgressDialog.show(context, "Please wait.",
                "Finding Park Place to Place the car.", true);

        mDatabaseRequestInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    mRequest = data.getValue(Request.class);
                    mConsumerID = mRequest.getmSenderUID();
                    mConsumerName = mRequest.getmRequstSenderName();
                    System.out.println(mRequest);

                    getProcess(mProviderID, mConsumerID);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mConsumerID.equals("")) {
                                showPlaceInfo(context, mRequest);
                            }

                        }
                    }, 2000);
                }
                return;
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event requestList: " + databaseError.getCode());
            }
        });
    }

    private static void mStartProcess(Process mProcess) {

        mDatabaseProcess = mFirebaseInstance.getReference("OnGoingProcesses/" + mConsumerID + "<<>>" + mProviderID);
        mDatabaseProcess.setValue(mProcess, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                if (firebaseError != null) {
                    System.out.println("Failed to Create New Process");
                } else {
                    System.out.println("New Process Created.");
                    //mConsumerID = "";

                }
            }
        });
    }

    public void getProcess(String mProviderID, String mConsumerID) {

        mProcess = null;
        String mProcessID = mConsumerID + "<<>>" + mProviderID;
        mDatabaseProcess = mFirebaseInstance.getReference("OnGoingProcesses/" + mProcessID);
        System.out.println(">>>>>>>>>>>>>> Get Status Called");
        mDatabaseProcess.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProcess = dataSnapshot.getValue(Process.class);
                System.out.println(">>>>>>>>>>>>>> Get Process Called  from firebase");
                if (mProcess != null) {
                    System.out.println("The process is running.");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event data: " + databaseError.getCode());
            }
        });
    }

    private static void mSetTransactionHistoryToConsumer() {


        mDatabaseTransactionConsumer = mFirebaseInstance.getReference("ConsumerList/" + mConsumerID + "/TransactionHistory/");
        mDatabaseTransactionConsumer.child(mTransactionUiqueKey).setValue(mTransactionHistory, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference firebase) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Transaction saved to Consumer.");
                    mSetTransactionHistoryToProvider();
                } else {
                    System.out.println(">>>>>>>>>>>> Transaction NOT saved to Consumer.");
                }
            }
        });
    }

    private static void mSetTransactionHistoryToProvider() {

        mDatabaseTransactionProvider = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/TransactionHistory/");
        mDatabaseTransactionProvider.child(mTransactionUiqueKey).setValue(mTransactionHistory, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference firebase) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Transaction saved to Provider.");
                    mSetTransactionHistoryToMain();
                } else {
                    System.out.println(">>>>>>>>>>>> Transaction NOT saved to Provider.");
                }
            }
        });
    }

    private static void mSetTransactionHistoryToMain() {

        mDatabaseTransactionMain = mFirebaseInstance.getReference("TransactionHistory/");
        mDatabaseTransactionMain.child(mTransactionUiqueKey).setValue(mTransactionHistory, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference firebase) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Transaction saved to Main Table.");
                    mRemoveMainProcess();
                } else {
                    System.out.println(">>>>>>>>>>>> Transaction NOT saved to Main Table.");
                }
            }
        });
    }

    private static void mRemoveMainProcess() {
        String key = mConsumerID + "<<>>" + mProviderID;
        mDatabaseProcess = mFirebaseInstance.getReference("OnGoingProcesses/");
        mDatabaseProcess.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference databaseReference) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Process removed.");
                    mRemoveStausOfConsumer();
                } else {
                    System.out.println(">>>>>>>>>>>> Process NOT removed.");
                }
            }
        });


    }

    private static void mRemoveStausOfConsumer() {
        mDatabaseConsumerStatus = mFirebaseInstance.getReference("ConsumerList/" + mConsumerID + "/Status/");
        mDatabaseConsumerStatus.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference databaseReference) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Removed Consumer Status Successfully.");
                    mRemoveStausOfProvider();
                } else {
                    System.out.println(">>>>>>>>>>>> Failed to Remove Consumer Status Successfully.");
                }
            }
        });
    }

    private static void mRemoveStausOfProvider() {
        mDatabaseProviderStatus = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/Status/");
        mDatabaseProviderStatus.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference databaseReference) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Removed Provider Status Successfully.");
                    mRemoveRequestInProvider();
                } else {
                    System.out.println(">>>>>>>>>>>> Failed to Remove Provider Status Successfully.");
                }
            }
        });
    }

    private static void mRemoveRequestInProvider() {
        mDatabaseAcceptedRequest = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/Request/");
        mDatabaseAcceptedRequest.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError mDatabaseError, DatabaseReference databaseReference) {
                if (mDatabaseError == null) {
                    System.out.println(">>>>>>>>>>>> Request removed Successfully.");
                    mRemoveParkPlaceItem();
                } else {
                    System.out.println(">>>>>>>>>>>> Failed Remove Request.");
                }
            }
        });
    }

    private static void mRemoveParkPlaceItem() {
        mDatabaseRemoveParkPlaceItem = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID);
        mDatabaseRemoveParkPlaceItem.child("mParkedVehicle").setValue("null", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    mShowFinishDialog();

                }
            }
        });

    }


    public String mGenerateTransactionAmount(int mRate) {

        int mSecondRate = (mRate * 20) / 100;
        int mAmount = 0;
        if (mHour <= 1) {
            return Integer.toString(mRate);
        } else {
            mAmount = (int) (mHour * mSecondRate) + (int) (mMinute * mSecondRate) / 60 + (int) (mSecond * mSecondRate) / 3600;

        }
        return String.valueOf(mAmount);
    }


    public void mShowAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to end the session?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (!mConsumerID.equals("")) {
                            if (TempHolder.mProvider != null) {

                                progressDialog = ProgressDialog.show(context, "Please wait.", "You session is ending.", true);
                                mTransactionUiqueKey = Long.toHexString(System.currentTimeMillis());

                                mTransactionHistory = new TransactionHistory(mTransactionUiqueKey, mGenerateTransactionAmount(Integer.parseInt(mParkPlaceRate)), Long.toString(mCurrentTime), TempHolder.mProvider.getmAddress(), mProcess.getmStartedTime(), Long.toString(System.currentTimeMillis()), Long.toString(MillisecondTime), mConsumerID, mConsumerName, mProcess.getmVehicleNumber(), mProcess.getmVehicleType(), mProviderID, TempHolder.mProvider.getmName(), TempHolder.mProvider.getmLatitude(), TempHolder.mProvider.getmLongitude());
                                mSetTransactionHistoryToConsumer();
                            }
                        }
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        alertDialog.show();
    }


    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CALL_PHONE}, 456);
            return;
        }
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(intent);

        }
    }

    public void showAddLocationDialogBox() {
        mAddLocationDialog = new Dialog(mActivity);
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

    private void startPlacePickerActivity() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(BOUNDS_BD);
        try {
            mActivity.startActivityForResult(builder.build(mActivity), REQUEST_CODE_PLACEPICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data) {

        System.out.println(">>>>>>>>>>>>> " + "ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/");
        Place placeSelected = PlacePicker.getPlace(data, context);
        String mProviderAddress = placeSelected.getAddress().toString();
        String mSelectedLatitude = Double.toString(placeSelected.getLatLng().latitude);
        String mSelectedLongitude = Double.toString(placeSelected.getLatLng().longitude);

        mFirebaseLocationUpdate = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList/" + mParkPlaceID + "/");

        mFirebaseLocationUpdate.child("mParkingIsApproved").setValue("false");
        mFirebaseLocationUpdate.child("mAddress").setValue(mProviderAddress);
        //mFirebaseLocationUpdate.child("mLatitude").setValue(mSelectedLatitude);
        //mFirebaseLocationUpdate.child("mLongitude").setValue(mSelectedLongitude);
    }

    public interface PlaceClickListener {
        void onPlaceClicked(ParkPlace parkPlace);
    }
}
