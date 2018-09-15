package com.nerdcastle.eparkingprovider.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.Adapters.DashboardAdapter;
import com.nerdcastle.eparkingprovider.DataModel.ParkPlace;
import com.nerdcastle.eparkingprovider.DataModel.ParkingRequest;
import com.nerdcastle.eparkingprovider.DataModel.Status;
import com.nerdcastle.eparkingprovider.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {


    //---------------------------------------------------
    private DashboardAdapter dashboardAdapter;
    private List<ParkingRequest> requestList = new ArrayList<>();
    private RecyclerView mNotificationRecyclerView;
    private LinearLayoutManager llm;

    private static final String TAG = "MainActivity";
    //---------------------------------------------------
    private FirebaseAuth auth;
    private DatabaseReference mFirebaseRequestRef;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;
    String mProviderID;

    //---------------------------------------------------
    private TextView mInfoText;

    public interface  MainFragmentInterface {
        public void goToDashBoard ();

    }
    public DashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mInfoText = view.findViewById(R.id.mInfoText);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mProviderID = auth.getCurrentUser().getUid();

        mNotificationRecyclerView = (RecyclerView) view.findViewById(R.id.mNotificationRecyclerView);
        llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mNotificationRecyclerView.setLayoutManager(llm);

        addRequestChangeListener();

        return view;
    }





    private void addRequestChangeListener() {
        // User requestList change listener

        System.out.println(">>>>>>>>>>>>>> Provider ID >>>>>>>> "+mProviderID);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRequestRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList");
        mFirebaseRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    ParkPlace parkPlace = data.getValue(ParkPlace.class);
                    DatabaseReference requestDB=mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/ParkPlaceList/" + parkPlace.getmParkPlaceID()+"/Request");

                    Query query=requestDB.orderByKey().limitToLast(7);


                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data:dataSnapshot.getChildren()) {

                                ParkingRequest parkingRequest = data.getValue(ParkingRequest.class);
                                if (parkingRequest.getmStatus().equals(Status.ACCEPTED)
                                        || parkingRequest.getmStatus().equals(Status.STARTED)
                                        || parkingRequest.getmStatus().equals(Status.ENDED)
                                        || parkingRequest.getmStatus().equals(Status.REJECTED))
                                {
                                    requestList.add(parkingRequest);
                                    //Toast.makeText(getActivity(), parkingRequest.getmStatus(), Toast.LENGTH_SHORT).show();
                                    setNotifactionRecyclerView ();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }


    public void setNotifactionRecyclerView ()
    {
        if (requestList.size()!=0)
        {
            mInfoText.setVisibility(View.INVISIBLE);
        }
        dashboardAdapter = new DashboardAdapter(requestList,getActivity());
        mNotificationRecyclerView.setAdapter(dashboardAdapter);
    }


}
