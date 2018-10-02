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
import com.google.firebase.firestore.FirebaseFirestore;
import bd.com.universal.eparking.owner.DataModel.ParkPlace;
import bd.com.universal.eparking.owner.DataModel.ParkingRequest;
import bd.com.universal.eparking.owner.DataModel.Status;
import bd.com.universal.eparking.owner.R;
import de.hdodenhof.circleimageview.CircleImageView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<ParkingRequest> requestList;
    public static List<ParkPlace> parkPlaceList = new ArrayList<>();
    public static Context context;
    public static String mConsumerID;
    public static String mConsumerName;
    public static String mConsumerPhone;
    public static String mConsumerVehicleNumber;

    public static String mProviderID;
    public static String mParkPlaceID;
    public static String mRequestID;

    SimpleDateFormat formatedDate = new SimpleDateFormat("dd MMM yyyy");

    public NotificationAdapter(List<ParkingRequest> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mProviderID = auth.getCurrentUser().getUid();
        //getAllParkingPlaces ();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        // 1. Declare your Views here

        public CircleImageView mSenderImage;
        public me.zhanghai.android.materialratingbar.MaterialRatingBar mSenderRating;
        public TextView mRequestSenderName,requestTimeTV;
        public TextView mRequestSenderInfo;
        public TextView mVehicleNumber,phoneNumberTv;
        public Button mIgnoreButton;
        public Button mAcceptButton,callButton;



        public Viewholder(final View itemView) {
            super(itemView);

            // 2. Define your Views here

            mSenderImage = (CircleImageView) itemView.findViewById(R.id.mSenderImage);
            mRequestSenderName = (TextView)itemView.findViewById(R.id.mRequestSenderName);
            mRequestSenderInfo = (TextView)itemView.findViewById(R.id.mRequestSenderInfo);
            mVehicleNumber = (TextView)itemView.findViewById(R.id.mVehicleNumber);
            mIgnoreButton = (Button)itemView.findViewById(R.id.mIgnoreButton);
            mAcceptButton = (Button)itemView.findViewById(R.id.mAcceptButton);
            callButton=(Button)itemView.findViewById(R.id.callButton);
            phoneNumberTv=(TextView)itemView.findViewById(R.id.phoneNumberTV);
            requestTimeTV=itemView.findViewById(R.id.requestDateId);


        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_notification, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {


        final ParkingRequest model = requestList.get(position);
        holder.mRequestSenderName.setText(model.getmConsumerName());
        holder.mRequestSenderInfo.setText("wants to park vehicle in "+model.getmParkPlaceTitle()+", "+model.getmParkPlaceAddress());
        holder.mVehicleNumber.setText(model.getmConsumerVehicleNumber());
        holder.phoneNumberTv.setText(model.getmConsumerPhone());
        holder.requestTimeTV.setText(formatedDate.format(model.getmRequestTime()));

        if (model.getmConsumerPhotoUrl()==null | model.getmConsumerPhotoUrl().isEmpty()){
        }
        else {
            Picasso.get().load(model.getmConsumerPhotoUrl()).placeholder(R.drawable.default_person_logo).error(R.drawable.default_person_logo).into(holder.mSenderImage);
        }
        // 3. set the requestList to your Views here


        holder.callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ShowToast("  You are calling "+model.getmConsumerPhone()+"  ");
                        //Toast.makeText(context, "You are calling "+model.getmConsumerPhone(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + model.getmConsumerPhone()));
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 456);
                            return;
                        }
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                    }
        });



        holder.mIgnoreButton.setOnClickListener(new View.OnClickListener() {
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

                                parkPlaceRequestDB.child("mStatus").setValue(Status.REJECTED,new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        ShowToast("  "+Status.REJECTED+" Successfully ! ");
                                        //Toast.makeText(context, Status.REJECTED+" Successfully !", Toast.LENGTH_LONG).show();
                                    }
                                });
                                consumerRequestDB.child("mStatus").setValue(Status.REJECTED);
                                parkPlaceDB.child("mIsAvailable").setValue("true");
                                holder.mAcceptButton.setEnabled(false);
                                holder.mIgnoreButton.setEnabled(false);
                                holder.mIgnoreButton.setText(Status.REJECTED);
                                holder.mAcceptButton.setVisibility(View.GONE);


                                FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
                                Map<String,Object> notificationMap=new HashMap<>();
                                notificationMap.put("message",model.getmProviderName()+" has rejected your request.");
                                notificationMap.put("consumer",mProviderID);

                                mFireStore.collection("Users").document(model.getmConsumerID()).collection("Notifications").add(notificationMap);

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
                builder.setMessage("Are you sure to ignore this request?").setPositiveButton("YES",onClickListener)
                        .setNegativeButton("NO",onClickListener).show();

            }
        });

        holder.mAcceptButton.setOnClickListener(new View.OnClickListener() {
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

                                parkPlaceRequestDB.child("mStatus").setValue(Status.ACCEPTED, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        ShowToast("  "+Status.ACCEPTED+" ! Please Check in your Dashboard  ");
                                        //Toast.makeText(context, Status.ACCEPTED+" ! Please Check in your Dashboard", Toast.LENGTH_LONG).show();
                                    }
                                });
                                consumerRequestDB.child("mStatus").setValue(Status.ACCEPTED);
                                parkPlaceDB.child("mIsAvailable").setValue("false");

                                holder.mAcceptButton.setEnabled(false);
                                holder.mIgnoreButton.setEnabled(false);
                                holder.mAcceptButton.setText(Status.ACCEPTED);
                                holder.mIgnoreButton.setVisibility(View.GONE);


                                FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
                                Map<String,Object> notificationMap=new HashMap<>();
                                notificationMap.put("message",model.getmProviderName()+" has accepted your request. "+model.getmParkPlaceAddress());
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
                builder.setMessage("Do you want to accept this parking request?").setPositiveButton("YES",onClickListener)
                        .setNegativeButton("NO",onClickListener).show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }






  /*  public static void getAllParkingPlaces ()
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

*/



    public void ShowToast(String text){

        Toast ToastMessage = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        View toastView = ToastMessage.getView();
        toastView.setBackgroundResource(R.drawable.custom_toast);
        ToastMessage.show();

    }


}