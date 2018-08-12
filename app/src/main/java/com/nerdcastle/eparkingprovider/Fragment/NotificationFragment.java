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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.Adapters.NotificationAdapter;
import com.nerdcastle.eparkingprovider.DataModel.Request;
import com.nerdcastle.eparkingprovider.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    //---------------------------------------------------
    private NotificationAdapter notificationAdapter;
    private List<Request> requestList = new ArrayList<>();
    private RecyclerView mNotificationRecyclerView;
    private LinearLayoutManager llm;

    private static final String TAG = "MainActivity";
    //---------------------------------------------------
    private FirebaseAuth auth;
    private DatabaseReference mFirebaseRequestRef;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;

    //---------------------------------------------------
    private TextView mInfoText;

    public interface NotificationFragmentInterface {
        public void goToNotification ();
    }
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        mInfoText = view.findViewById(R.id.mInfoText);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        mNotificationRecyclerView = (RecyclerView) view.findViewById(R.id.mNotificationRecyclerView);
        llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mNotificationRecyclerView.setLayoutManager(llm);

        addRequestChangeListener();

        return view;
    }





    private void addRequestChangeListener() {
        // User requestList change listener
        String mProviderID = auth.getCurrentUser().getUid();
        System.out.println(">>>>>>>>>>>>>> Provider ID >>>>>>>> "+mProviderID);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRequestRef = mFirebaseInstance.getReference("ProviderList/"+mProviderID+"/Request");
        mFirebaseRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Request request = data.getValue(Request.class);
                    requestList.add(request);
                }
                setNotifactionRecyclerView ();
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
        notificationAdapter = new NotificationAdapter(requestList,getActivity());
        mNotificationRecyclerView.setAdapter(notificationAdapter);
    }


}
