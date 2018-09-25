package com.universal.eparkingowner.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.universal.eparkingowner.DataModel.ParkPlace;
import com.universal.eparkingowner.R;

import java.util.List;


public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.Viewholder> {

    private List<ParkPlace> parkPlaceList;
    private ParkPlace model;
    private Context context;

    public MomentAdapter(List<ParkPlace> parkPlaceList, Context context) {
        this.parkPlaceList = parkPlaceList;
        this.context = context;
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        public ImageView momentImageView;
        public TextView momentTitle;


        public Viewholder(View itemView) {
            super(itemView);

            momentImageView = (ImageView)itemView.findViewById(R.id.momentImageView);
            momentTitle = (TextView)itemView.findViewById(R.id.mPlaceStatus);
        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moment_model, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

        model = parkPlaceList.get(position);
        holder.momentImageView.setImageBitmap(decodeBitmap(model.getmParkedVehicle()));

        if (model.getmParkingStatus().equals("true"))
        { holder.momentTitle.setText("Available"); }
        else { holder.momentTitle.setText("Booked"); }




    }

    @Override
    public int getItemCount() {
        return parkPlaceList.size();
    }


    public Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


}

