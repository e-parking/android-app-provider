package bd.com.universal.eparking.owner.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import bd.com.universal.eparking.owner.DataModel.ParkingRequest;
import bd.com.universal.eparking.owner.DataModel.Status;
import bd.com.universal.eparking.owner.R;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.Viewholder> {

    public static FirebaseDatabase mFirebaseInstance;
    public static FirebaseAuth auth;
    public static ProgressDialog progressDialog;
    public static List<ParkingRequest> requestList;
    public static Context context;

    public static String mProviderID;
    public static String mParkPlaceID;

    SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy");

    public DashboardAdapter(List<ParkingRequest> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mProviderID = auth.getCurrentUser().getUid();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {


        public CircleImageView mSenderImage;
        public TextView mRequestSenderName,requestTimeTV;
        public TextView mRequestSenderInfo;
        public TextView mVehicleNumber;
        public ImageView mobileIcon;
        public TextView mDurationTV,phoneNumberTV,statusTV;
        public Button mStartButton,callButton;


        public Viewholder(final View itemView) {
            super(itemView);

            // 2. Define your Views here

            mSenderImage = (CircleImageView) itemView.findViewById(R.id.mSenderImage);
            mRequestSenderName = (TextView)itemView.findViewById(R.id.mRequestSenderName);
            mRequestSenderInfo = (TextView)itemView.findViewById(R.id.mRequestSenderInfo);
            mVehicleNumber = (TextView)itemView.findViewById(R.id.mVehicleNumber);
            mDurationTV = (TextView)itemView.findViewById(R.id.durationTV);
            mStartButton = (Button)itemView.findViewById(R.id.mAcceptButton);
            phoneNumberTV=(TextView)itemView.findViewById(R.id.phoneNumberTV);
            callButton=(Button)itemView.findViewById(R.id.callButton);
            requestTimeTV=itemView.findViewById(R.id.requestDateId);
            statusTV=itemView.findViewById(R.id.status_id);
            mobileIcon=itemView.findViewById(R.id.mobileIcon);
            mStartButton.setVisibility(View.GONE);


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
        holder.mRequestSenderInfo.setText(model.getmParkPlaceTitle()+", "+model.getmParkPlaceAddress());
        holder.mVehicleNumber.setText(model.getmConsumerVehicleNumber());

        holder.requestTimeTV.setText(formatedDate.format(model.getmRequestTime()));
        if (model.getmConsumerPhotoUrl().isEmpty())
        {
        }
        else {
            Picasso.get().load(model.getmConsumerPhotoUrl())
                    .placeholder(context.getResources().getDrawable(R.drawable.default_person_logo))
                    .error(context.getResources().getDrawable(R.drawable.default_person_logo))
                    .into(holder.mSenderImage);
        }

        final String number=model.getmConsumerPhone().toString();

        holder.phoneNumberTV.setText(number);

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowToast("   You are calling this "+number+"  ");
               // Toast.makeText(context, "You are calling this "+number, Toast.LENGTH_SHORT).show();
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

        final String mStatus=model.getmStatus();

        if (mStatus.equals(Status.STARTED))
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
                holder.mDurationTV.setText(hour+"h:"+min+"m"+" ago");
            }
        }
        else if (mStatus.equals(Status.ENDED))
        {
            holder.mDurationTV.setVisibility(View.VISIBLE);
            long endTime=model.getmEndTime();
            long timeDistance=endTime-startTime;
            int hour= (int) (timeDistance/3600000);
            timeDistance=timeDistance-(hour*3600000);
            int min= (int) (timeDistance/60000);
            timeDistance=timeDistance-(min*60000);
            int sec= (int) (timeDistance/1000);
            holder.mDurationTV.setText(hour+"h:"+min+"m"+"\n"+model.getmEstimatedCost()+" TK");
        }
        else if (mStatus.equals(Status.PENDING)){
            holder.mDurationTV.setText("0h:0m");
        }



        if (mStatus.equals(Status.ACCEPTED)){
            holder.statusTV.setVisibility(View.GONE);
            holder.mStartButton.setVisibility(View.VISIBLE);
            holder.mobileIcon.setVisibility(View.VISIBLE);
            holder.phoneNumberTV.setVisibility(View.VISIBLE);
            holder.callButton.setVisibility(View.VISIBLE);
        }
        else if (mStatus.equals(Status.STARTED))
        {
            holder.mStartButton.setVisibility(View.GONE);
            holder.statusTV.setVisibility(View.VISIBLE);
            holder.statusTV.setText(Status.STARTED);
            holder.mobileIcon.setVisibility(View.VISIBLE);
            holder.phoneNumberTV.setVisibility(View.VISIBLE);
            holder.callButton.setVisibility(View.VISIBLE);
        }
        else if (mStatus.equals(Status.ENDED)){

            holder.mStartButton.setVisibility(View.GONE);
            holder.statusTV.setVisibility(View.VISIBLE);
            holder.statusTV.setText(Status.ENDED);
            holder.mobileIcon.setVisibility(View.GONE);
            holder.phoneNumberTV.setVisibility(View.GONE);
            holder.callButton.setVisibility(View.GONE);
        }
        else if (mStatus.equals(Status.REJECTED)){
            holder.mStartButton.setVisibility(View.GONE);
            holder.statusTV.setVisibility(View.VISIBLE);
            holder.statusTV.setText(Status.REJECTED);
            holder.mobileIcon.setVisibility(View.GONE);
            holder.phoneNumberTV.setVisibility(View.GONE);
            holder.callButton.setVisibility(View.GONE);
        }


        // 3. set the requestList to your Views here


        holder.mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener onClickListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DatabaseReference parkPlaceRequestDB=mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/" + model.getmParkPlaceID()+"/Request/"+model.getmRequestID());
                                DatabaseReference consumerRequestDB=mFirebaseInstance.getReference("ConsumerList/"+model.getmConsumerID()+"/Request/"+model.getmRequestID());
                                DatabaseReference parkPlaceDB=mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/" + model.getmParkPlaceID());

                                parkPlaceRequestDB.child("mStatus").setValue(Status.STARTED, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        ShowToast("  "+Status.STARTED+"  ");

                                    }
                                });


                                consumerRequestDB.child("mStatus").setValue(Status.STARTED);
                                consumerRequestDB.child("mStartTime").setValue(System.currentTimeMillis());
                                parkPlaceRequestDB.child("mStartTime").setValue(System.currentTimeMillis());
                                parkPlaceDB.child("mIsAvailable").setValue("false");


                                holder.mStartButton.setVisibility(View.GONE);
                                holder.statusTV.setVisibility(View.VISIBLE);
                                holder.statusTV.setText(Status.STARTED);
                                holder.mobileIcon.setVisibility(View.VISIBLE);
                                holder.phoneNumberTV.setVisibility(View.VISIBLE);
                                holder.callButton.setVisibility(View.VISIBLE);

                                FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
                                Map<String,Object> notificationMap=new HashMap<>();
                                notificationMap.put("message",model.getmProviderName()+" has started perking session.");
                                notificationMap.put("consumer",mProviderID);

                                mFireStore.collection("Seekers").document(model.getmConsumerID()).collection("Notifications").add(notificationMap);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.warning_red);
                builder.setMessage("Do you want to start this parking session?").setPositiveButton("YES",onClickListener)
                        .setNegativeButton("NO",onClickListener).show();


            }
        });



    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }




    public void ShowToast(String text){

        Toast ToastMessage = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        View toastView = ToastMessage.getView();
        toastView.setBackgroundResource(R.drawable.custom_toast);
        ToastMessage.show();

    }



}