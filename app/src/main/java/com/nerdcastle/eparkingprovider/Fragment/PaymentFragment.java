package com.nerdcastle.eparkingprovider.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.R;
import com.nerdcastle.eparkingprovider.Adapters.PaymentAdapter;
import com.nerdcastle.eparkingprovider.DataModel.TransactionHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {


    private SwipeRefreshLayout mPaymentRefresh;
    //----------- Firebase -------------------
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseInstance;
    public static DatabaseReference mDatabaseTransactionHistory;
    //----------------------------------------

    private RecyclerView mPaymentRecyclerView;
    private LinearLayoutManager llm;
    private List<TransactionHistory> mTransactions = new ArrayList<>();
    private PaymentAdapter paymentAdapter;
    private ProgressDialog progressDialog;
    private String mProviderID;
    public interface PaymentFragmentInterface {
        public void goToPayment();
    }
    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        mPaymentRefresh = view.findViewById(R.id.mPaymentRefresh);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            mProviderID = mAuth.getCurrentUser().getUid();
            mFirebaseInstance = FirebaseDatabase.getInstance();
        }

        mPaymentRecyclerView = view.findViewById(R.id.mPaymentRecyclerView);
        llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mPaymentRecyclerView.setLayoutManager(llm);


        mPaymentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTransaction();
            }
        });
        getAllTransaction ();



        return view;
    }


    public void getAllTransaction ()
    {

        mDatabaseTransactionHistory = mFirebaseInstance.getReference("ProviderList/"+ mProviderID +"/TransactionHistory");
        progressDialog = ProgressDialog.show(getContext(), "Please wait.",
                "We are collecting Parking Owners.", true);
        mDatabaseTransactionHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTransactions.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    TransactionHistory mTransactionHistory = data.getValue(TransactionHistory.class);
                    if (mTransactionHistory != null)
                    {
                         System.out.println(">>>>>>>>>>>>>>>>>>>>>> "+mTransactionHistory.getmTransactionID());
                         mTransactions.add(mTransactionHistory);
                    }

                }
                progressDialog.dismiss();
                mPaymentRefresh.setRefreshing(false);

                Collections.sort(mTransactions,new sortByCreatedTime());
                paymentAdapter = new PaymentAdapter(mTransactions,getActivity());
                mPaymentRecyclerView.setAdapter(paymentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The to read event data: " + databaseError.getCode());
            }
        });
    }

    class sortByCreatedTime implements Comparator<TransactionHistory>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(TransactionHistory a, TransactionHistory b)
        {
            return (int) (Long.parseLong(b.getmTransactionCreated()) - Long.parseLong(a.getmTransactionCreated()));
        }
    }

}
