package bd.com.universal.eparking.owner.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import bd.com.universal.eparking.owner.Adapters.MyAllPlacesAdapter;
import bd.com.universal.eparking.owner.DataModel.ParkPlace;
import bd.com.universal.eparking.owner.R;

import java.util.ArrayList;
import java.util.List;

public class MyParkingPlacesFragment extends Fragment implements MyAllPlacesAdapter.PlaceClickListener {

    private SwipeRefreshLayout mRefreshParkPlaces;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private RecyclerView mEventRecyclerView;
    private MyAllPlacesAdapter adapter;
    private List<ParkPlace> parkPlaceList = new ArrayList<>();
    LinearLayoutManager llm;
    private ProgressDialog progressDialog;
    private String mProviderID;
    private Button addPlaceBtn;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public void onPlaceClicked(ParkPlace parkPlace) {

        ft = fm.beginTransaction();
        ParkPlaceInfoFragment fragment = new ParkPlaceInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PlaceTitle", parkPlace.getmParkPlaceTitle());
        bundle.putString("PlaceAddress", parkPlace.getmAddress());
        bundle.putString("PalceImage", parkPlace.getmParkPlacePhotoUrl());
        bundle.putString("VehicleType", parkPlace.getmParkingType());
        bundle.putString("Latitude",parkPlace.getmLatitude());
        bundle.putString("Logitude",parkPlace.getmLongitude());

        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentContainer, fragment);
        ft.addToBackStack("goToMain");
        ft.commit();
    }

    public interface MainFragmentInterface {
        void goToMyParkingPlace();

    }

    public MyParkingPlacesFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_my_parking_places, container, false);

        mRefreshParkPlaces = view.findViewById(R.id.mRefreshParkPlaces);
        mEventRecyclerView = view.findViewById(R.id.mParkPlaceRecyclerView);
        addPlaceBtn = view.findViewById(R.id.addPlaceBtn);

        fm = getActivity().getSupportFragmentManager();

        mAuth = FirebaseAuth.getInstance();
        mProviderID = mAuth.getCurrentUser().getUid();
        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList/" + mProviderID + "/ParkPlaceList");

        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "while we are preparing your app.", true);

        mRefreshParkPlaces.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllParkingPlaces();
            }
        });

        getAllParkingPlaces();

        addPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = fm.beginTransaction();
                AddParkPlaceFragment fragment = new AddParkPlaceFragment();
                ft.replace(R.id.fragmentContainer, fragment);
                ft.addToBackStack("goToAddNewPlace");
                ft.commit();
            }
        });

        return view;
    }


    public void getAllParkingPlaces() {

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkPlaceList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ParkPlace parkPlace = data.getValue(ParkPlace.class);
                    parkPlaceList.add(parkPlace);
                }
                progressDialog.dismiss();
                adapter = new MyAllPlacesAdapter(parkPlaceList, getActivity(), getActivity(), MyParkingPlacesFragment.this);
                mEventRecyclerView.setAdapter(adapter);
                mRefreshParkPlaces.setRefreshing(false);
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event requestList: " + databaseError.getCode());
            }
        });

    }
}
