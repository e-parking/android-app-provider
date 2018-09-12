package com.nerdcastle.eparkingprovider.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.Adapters.ParkPlaceAdapter;
import com.nerdcastle.eparkingprovider.DataModel.ParkPlace;
import com.nerdcastle.eparkingprovider.R;
import com.nerdcastle.eparkingprovider.UserPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private SwipeRefreshLayout mRefreshParkPlaces;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private RecyclerView mEventRecyclerView;
    private ParkPlaceAdapter mParkPlaceAdapter;
    private List<ParkPlace> parkPlaceList = new ArrayList<>();
    LinearLayoutManager llm;
    private ProgressDialog progressDialog;
    private String eventID;
    private String mUserID;
    private UserPreferences userPreferences;
    private String mParkPlaceID;
    private String mProviderID;
    private TextView mItemInfo;

    public interface  MainFragmentInterface {
        public void goToMain ();

    }
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        mRefreshParkPlaces = view.findViewById(R.id.mRefreshParkPlaces);

        mAuth = FirebaseAuth.getInstance();

        userPreferences = new UserPreferences(getActivity());
        mProviderID = mAuth.getCurrentUser().getUid();
        mParkPlaceID = userPreferences.getUserID();
        if (getArguments()!=null)
        {
            eventID = getArguments().get("providerID").toString();
            Toast.makeText(getActivity(), eventID, Toast.LENGTH_SHORT).show();
        }

        mEventRecyclerView = view.findViewById(R.id.mParkPlaceRecyclerView);
        mItemInfo = view.findViewById(R.id.mItemInfo);

        mEventRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList");

        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "while we are preparing your app.", true);



        mRefreshParkPlaces.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllParkingPlaces();
            }
        });





        getAllParkingPlaces();


        return view;
    }




    public void getAllParkingPlaces ()
    {
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkPlaceList.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    ParkPlace parkPlace = data.getValue(ParkPlace.class);
                    parkPlaceList.add(parkPlace);
                }
                progressDialog.dismiss();
                if (parkPlaceList.size() != 0)
                {
                    mItemInfo.setVisibility(View.INVISIBLE);
                }else
                {
                    mItemInfo.setVisibility(View.VISIBLE);
                }
                mParkPlaceAdapter = new ParkPlaceAdapter(parkPlaceList,getActivity(),getActivity());
                mEventRecyclerView.setAdapter(mParkPlaceAdapter);
                mRefreshParkPlaces.setRefreshing(false);
                //setMomentRecyclerView (momentList);
            }
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event requestList: " + databaseError.getCode());
            }
        });

    }





}
