package com.nerdcastle.eparkingprovider.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.DataModel.ParkPlace;
import com.nerdcastle.eparkingprovider.DataModel.Request;
import com.nerdcastle.eparkingprovider.DataModel.StatusOfConsumer;
import com.nerdcastle.eparkingprovider.DataModel.StatusOfProvider;
import com.nerdcastle.eparkingprovider.DataModel.TempHolder;
import com.nerdcastle.eparkingprovider.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder> {

    public static FirebaseDatabase mFirebaseInstance;
    public static DatabaseReference mDatabaseNotificationRef;
    public static DatabaseReference mDatabaseParkPlaceRef;
    public static DatabaseReference mDatabaseParkPlacingRef;

    public static DatabaseReference mDatabaseConsumerStatus;
    public static DatabaseReference mDatabaseProviderStatus;

    public static DatabaseReference mFromPath;
    public static DatabaseReference mToPath;
    public static FirebaseAuth auth;
    public static ProgressDialog progressDialog;
    public static List<Request> requestList;
    public static List<ParkPlace> parkPlaceList = new ArrayList<>();
    private Request model;
    public static Context context;
    public static String mConsumerID;
    public static String mConsumerName;
    public static String mConsumerPhone;
    public static String mConsumerVehicleNumber;

    public static String mProviderID;
    public static String mParkPlaceID;
    public static String mRequestID;

    public NotificationAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mProviderID = auth.getCurrentUser().getUid();
        getAllParkingPlaces ();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        // 1. Declare your Views here

        public ImageView mSenderImage;
        public me.zhanghai.android.materialratingbar.MaterialRatingBar mSenderRating;
        public TextView mRequestSenderName;
        public TextView mRequestSenderInfo;
        public TextView mVehicleNumber;
        public Button mIgnoreButton;
        public Button mAcceptButton;


        public Viewholder(final View itemView) {
            super(itemView);

            // 2. Define your Views here

            mSenderImage = (ImageView)itemView.findViewById(R.id.mSenderImage);
            mSenderRating = (me.zhanghai.android.materialratingbar.MaterialRatingBar)itemView.findViewById(R.id.mSenderRating);
            mRequestSenderName = (TextView)itemView.findViewById(R.id.mRequestSenderName);
            mRequestSenderInfo = (TextView)itemView.findViewById(R.id.mRequestSenderInfo);
            mVehicleNumber = (TextView)itemView.findViewById(R.id.mVehicleNumber);
            mIgnoreButton = (Button)itemView.findViewById(R.id.mIgnoreButton);
            mAcceptButton = (Button)itemView.findViewById(R.id.mAcceptButton);



            mIgnoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    removeRequest(requestList.get(getAdapterPosition()).getmRequestID());

                }
            });


            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "You Tapped. and the size "+parkPlaceList.size(), Toast.LENGTH_SHORT).show();
                    for (ParkPlace mParkPlace: parkPlaceList)
                    {
                        if (!mParkPlace.getmParkedVehicle().equals("null") && TempHolder.mProvider !=null)
                        {
                            System.out.println("All Place is booked.");
                            Toast.makeText(context, "All Place is booked.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            mParkPlaceID =mParkPlace.getmParkPlaceID();
                            mRequestID =  requestList.get(getAdapterPosition()).getmRequestID();

                            mDatabaseParkPlacingRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/"+mParkPlaceID);
                            mDatabaseParkPlacingRef.child("mParkedVehicle").setValue(mRequestID);
                            mFromPath = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/Request/"+mRequestID);
                            mToPath = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/"+mParkPlaceID+"/Request/"+mRequestID);

                            parkTheCar(mFromPath,mToPath);
                            removeRequest(requestList.get(getAdapterPosition()).getmRequestID());
                            break;
                        }
                    }


                }
            });


        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_notification, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

        model = requestList.get(position);
        holder.mRequestSenderName.setText(model.getmRequstSenderName());
        holder.mRequestSenderInfo.setText("wants to park a "+model.getmVehicleType()+" in your garage.");
        // 3. set the requestList to your Views here

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }






    public static void getAllParkingPlaces ()
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseParkPlaceRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList");

        mDatabaseParkPlaceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkPlaceList.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    ParkPlace parkPlace = data.getValue(ParkPlace.class);
                    parkPlaceList.add(parkPlace);
                }
                return;
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event requestList: " + databaseError.getCode());
            }
        });
    }



    public static void parkTheCar (String mParkPlaceID, String mRequestID)
    {
        mDatabaseParkPlacingRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/"+mParkPlaceID);
        mDatabaseParkPlacingRef.child("mParkedVehicle").setValue(mRequestID);

    }



    private static void parkTheCar(final DatabaseReference fromPath, final DatabaseReference toPath) {


        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mConsumerID = dataSnapshot.getValue(Request.class).getmSenderUID();

                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> "+mConsumerID);
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");




                                StatusOfProvider mStatusOfProvider = new StatusOfProvider(mConsumerID+"<<>>"+mProviderID,mConsumerID,mProviderID,mParkPlaceID,mRequestID,mConsumerName,mConsumerPhone,mConsumerVehicleNumber,"","","");
                                StatusOfConsumer mStatusOfConsumer = new StatusOfConsumer(mConsumerID+"<<>>"+mProviderID,mConsumerID,mProviderID,mParkPlaceID,mRequestID,TempHolder.mProvider.getmName(),TempHolder.mProvider.getmPhone(),TempHolder.mProvider.getmAddress(),TempHolder.mProvider.getmLatitude(),TempHolder.mProvider.getmLongitude());
                                mAcceptRequest(mStatusOfProvider,mStatusOfConsumer);



                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Sorry couldn't copy.");
            }
        });
    }


    private static void removeRequest(String mRequestID)
    {
        mDatabaseNotificationRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/Request/");
        mDatabaseNotificationRef.child(mRequestID).removeValue();
        mDatabaseNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(">>>>>>>>>>>>> Notification Removed.");
                //notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private static void mAcceptRequest(StatusOfProvider mStatusOfProvider, StatusOfConsumer mStatusOfConsumer) {

        mDatabaseProviderStatus = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/"+mParkPlaceID+"/Status/");
        mDatabaseConsumerStatus = mFirebaseInstance.getReference("ConsumerList/"+mConsumerID+"/Status/");

        mDatabaseProviderStatus.setValue(mStatusOfProvider, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Request Not Accepted.");
                        } else {
                            System.out.println("Request Successfully Accepted.");
                            mParkPlaceID = "";
                            mRequestID = "";

                        }
                    }
                });

        mDatabaseConsumerStatus.setValue(mStatusOfConsumer);

    }




}