package com.universal.eparkingowner.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.universal.eparkingowner.R;
import com.squareup.picasso.Picasso;

public class ParkPlaceInfoFragment extends Fragment implements OnMapReadyCallback {

 private TextView prakTitle,parkAddress,vehicletype;
 private ImageView parkImage;
 String title,address,photo,vehicle;
 Double mLat,mLong;

 private GoogleMap map;
 private GoogleMapOptions options;
    private LatLng mCurrentLocation;

    private android.support.v4.app.FragmentManager fm;
    private android.support.v4.app.FragmentTransaction ft;

    public ParkPlaceInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_park_place_info, container, false);
        init(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            title = bundle.getString("PlaceTitle");
            address = bundle.getString("PlaceAddress");
            photo = bundle.getString("PalceImage");
            vehicle = bundle.getString("VehicleType");
            mLat = Double.parseDouble((bundle.getString("Latitude")));
            mLong = Double.parseDouble((bundle.getString("Logitude")));
        }

        prakTitle.setText(title);
        parkAddress.setText(address);
        if (photo.contains("https://")) {
            Picasso.get().load(photo).placeholder(R.drawable.ic_park_false).error(R.drawable.ic_park_false).into(parkImage);
        } else {
            Bitmap bitmap = decodeBase64(bundle.getString("PalceImage"));
            parkImage.setImageBitmap(bitmap);
        }


       // parkImage.setText(photo);
        vehicletype.setText(vehicle);
        if (mLat !=null && mLong != null)
        {
            innitializeMap ();
        }

        return view;
    }

    private void init(View view) {
        fm = getActivity().getSupportFragmentManager();

        prakTitle = view.findViewById(R.id.mParkTitle);
        parkAddress = view.findViewById(R.id.mParkAddress);
        parkImage = view.findViewById(R.id.mParkImageView);
        vehicletype = view.findViewById(R.id.typeVehical);


    }
    public void innitializeMap ()
    {
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft =fm.beginTransaction().replace(R.id.mapcontainer,mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mCurrentLocation = new LatLng(mLat,mLong);
        map.addMarker(new MarkerOptions().position(mCurrentLocation).title("Park Location").snippet(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation,20));
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
