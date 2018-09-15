package com.nerdcastle.eparkingprovider.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nerdcastle.eparkingprovider.DataModel.ParkingRequest;
import com.nerdcastle.eparkingprovider.DataModel.Status;
import com.nerdcastle.eparkingprovider.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.Viewholder> {

    public static FirebaseDatabase mFirebaseInstance;
    public static FirebaseAuth auth;
    public static ProgressDialog progressDialog;
    public static List<ParkingRequest> requestList;
    public static Context context;

    public static String mProviderID;
    public static String mParkPlaceID;

    public DashboardAdapter(List<ParkingRequest> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mProviderID = auth.getCurrentUser().getUid();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {


        public ImageView mSenderImage;
        public TextView mRequestSenderName;
        public TextView mRequestSenderInfo;
        public TextView mVehicleNumber;
        public TextView mDurationTV,phoneNumberTV;
        public Button mStartButton,callButton;


        public Viewholder(final View itemView) {
            super(itemView);

            // 2. Define your Views here

            mSenderImage = (ImageView)itemView.findViewById(R.id.mSenderImage);
            mRequestSenderName = (TextView)itemView.findViewById(R.id.mRequestSenderName);
            mRequestSenderInfo = (TextView)itemView.findViewById(R.id.mRequestSenderInfo);
            mVehicleNumber = (TextView)itemView.findViewById(R.id.mVehicleNumber);
            mDurationTV = (TextView)itemView.findViewById(R.id.durationTV);
            mStartButton = (Button)itemView.findViewById(R.id.mAcceptButton);
            phoneNumberTV=(TextView)itemView.findViewById(R.id.phoneNumberTV);
            callButton=(Button)itemView.findViewById(R.id.callButton);


        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_dashboard, parent, false);
        return new Viewholder(itemView);

    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {

        final ParkingRequest model = requestList.get(position);
        holder.mRequestSenderName.setText(model.getmConsumerName());
        holder.mRequestSenderInfo.setText("wants to park his car in "+model.getmParkPlaceTitle()+", "+model.getmParkPlaceAddress());
        holder.mVehicleNumber.setText(model.getmConsumerVehicleNumber());

        if (model.getmConsumerPhotoUrl().isEmpty()){
        }
        else {
            Picasso.get().load(model.getmConsumerPhotoUrl())
                    .placeholder(context.getResources().getDrawable(R.drawable.header_cover))
                    .error(context.getResources().getDrawable(R.drawable.header_cover))
                    .into(holder.mSenderImage);
        }

        final String number=model.getmConsumerPhone().toString();

        holder.phoneNumberTV.setText(number);

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "You are calling "+number, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 456);
                    return;
                }
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });



        final long startTime=model.getmStartTime();
        if (model.getmStatus().equals(Status.STARTED))
        {
            if (startTime!=0){
                holder.mDurationTV.setVisibility(View.VISIBLE);
                long currentTime=System.currentTimeMillis();
                long timeDistance=currentTime-startTime;
                int hour= (int) (timeDistance/3600000);
                timeDistance=timeDistance-(hour*3600000);
                int min= (int) (timeDistance/60000);
                timeDistance=timeDistance-(min*60000);
                int sec= (int) (timeDistance/1000);
                holder.mDurationTV.setText(hour+"h:"+min+"m:"+sec+"s ago");
            }
        }
        else if (model.getmStatus().equals(Status.ENDED))
        {
            holder.mDurationTV.setVisibility(View.VISIBLE);
            long endTime=model.getmEndTime();
            long timeDistance=endTime-startTime;
            int hour= (int) (timeDistance/3600000);
            timeDistance=timeDistance-(hour*3600000);
            int min= (int) (timeDistance/60000);
            timeDistance=timeDistance-(min*60000);
            int sec= (int) (timeDistance/1000);
            holder.mDurationTV.setText("Total- "+hour+"h:"+min+"m:"+sec+"s");
        }





        String mStatus=model.getmStatus();
        if (mStatus.equals(Status.STARTED))
        {
            holder.mStartButton.setText(Status.STARTED);
            holder.mStartButton.setEnabled(false);
        }
        else if (mStatus.equals(Status.ENDED)){
            holder.mStartButton.setText(Status.ENDED);
            holder.mStartButton.setEnabled(false);
        }
        else if (mStatus.equals(Status.REJECTED)){
            holder.mStartButton.setText(Status.REJECTED);
            holder.mStartButton.setEnabled(false);
        }


        // 3. set the requestList to your Views here


        holder.mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference parkPlaceRequestDB=mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/" + model.getmParkPlaceID()+"/Request/"+model.getmRequestID());
                DatabaseReference consumerRequestDB=mFirebaseInstance.getReference("ConsumerList/"+model.getmConsumerID()+"/Request/"+model.getmRequestID());
                DatabaseReference parkPlaceDB=mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/" + model.getmParkPlaceID());

                parkPlaceRequestDB.child("mStatus").setValue(Status.STARTED, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(context, Status.STARTED+" ", Toast.LENGTH_LONG).show();
                    }
                });
                consumerRequestDB.child("mStatus").setValue(Status.STARTED);
                consumerRequestDB.child("mStartTime").setValue(System.currentTimeMillis());
                parkPlaceRequestDB.child("mStartTime").setValue(System.currentTimeMillis());
                parkPlaceDB.child("mIsAvailable").setValue("false");

                holder.mStartButton.setText(Status.STARTED);
                holder.mStartButton.setEnabled(false);




            }
        });



    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }






}