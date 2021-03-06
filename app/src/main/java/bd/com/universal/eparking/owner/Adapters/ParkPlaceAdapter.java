package bd.com.universal.eparking.owner.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import bd.com.universal.eparking.owner.DataModel.ParkPlace;
import bd.com.universal.eparking.owner.DataModel.Request;
import bd.com.universal.eparking.owner.DataModel.VehicleType;
import bd.com.universal.eparking.owner.R;

import java.util.List;

public class ParkPlaceAdapter extends RecyclerView.Adapter<ParkPlaceAdapter.Viewholder> {

    private List<ParkPlace> parkPlaceList;
    private ParkPlace model;
    private static Context context;
    private Activity mActivity;
    private static Request mRequest;
    private Dialog mAddLocationDialog;
    private DatabaseReference mFirebaseLocationUpdate;

    //------------ Firebase -----------------------------
    public static FirebaseDatabase mFirebaseInstance;

    //-------------- Transaction -------------------------

    public static FirebaseAuth mAuth;
    public static String mProviderID;
    public static String mProviderName;
    public static String mParkPlaceID;

    public static ProgressDialog progressDialog;

    private ParkPlaceAdapter.PlaceClickListener listener;


    public ParkPlaceAdapter(List<ParkPlace> parkPlaceList, Context context, Activity mActivity, ParkPlaceAdapter.PlaceClickListener listener) {
        this.parkPlaceList = parkPlaceList;
        this.context = context;
        this.listener = listener;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProviderID = mAuth.getCurrentUser().getUid();
        mProviderName = mAuth.getCurrentUser().getDisplayName();
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ParkPlaceAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_my_places, parent, false);

        return new ParkPlaceAdapter.Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(ParkPlaceAdapter.Viewholder holder, int position) {

        model = parkPlaceList.get(position);
        if (model.getmParkingType().equals(VehicleType.Car)){
            holder.momentImageView.setImageResource(R.drawable.car_park_place_icon);
        }else if (model.getmParkingType().equals(VehicleType.MotorCycle)){
            holder.momentImageView.setImageResource(R.drawable.bike_park_place_icon);
        }

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

                    listener.onPlaceClicked(parkPlaceList.get(getAdapterPosition()));
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return parkPlaceList.size();
    }


    public interface PlaceClickListener {
        void onPlaceClicked(ParkPlace parkPlace);
    }
}