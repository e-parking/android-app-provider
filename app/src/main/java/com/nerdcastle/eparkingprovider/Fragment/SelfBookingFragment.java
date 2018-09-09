package com.nerdcastle.eparkingprovider.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nerdcastle.eparkingprovider.DataModel.SelfBook;
import com.nerdcastle.eparkingprovider.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SelfBookingFragment extends Fragment implements View.OnClickListener {

    private TextView dateTV, fromTimeTV, toTimeTV;
    private int year, month, day, hour, minute;
    private Context context;
    private Button saveBtn;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private DatabaseReference databaseReference;
    private DatabaseReference selfBookDbReference;
    private String providerId;
    private String placeId;
    private String bookId;
    private SelfBook selfBook;
    String formattedCurrentDate;
    private long fromTimeInMilis, toTimeInMilis;

    public SelfBookingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_self_booking, container, false);
        init(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            placeId = bundle.getString("PlaceId");
        }
        //getDataBaseReference();
        return view;
    }

    private void init(View view) {

        fm = getActivity().getSupportFragmentManager();

        dateTV = view.findViewById(R.id.date);
        fromTimeTV = view.findViewById(R.id.fromTime);
        toTimeTV = view.findViewById(R.id.toTime);
        saveBtn = view.findViewById(R.id.saveBtn);

        fromTimeTV.setOnClickListener(this);
        toTimeTV.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        dateTV.setOnClickListener(this);
        Format formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedCurrentDate = formatter.format(new Date());
        dateTV.setText(formattedCurrentDate);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            /*case R.id.date:
                selectDate();
                break;*/
            case R.id.fromTime:
                selectFromTime(fromTimeTV);
                break;
            case R.id.toTime:
                selectToTime(toTimeTV);
                break;
            case R.id.saveBtn:
                saveToFireBase();
                break;
        }
    }

    private void saveToFireBase() {
        getDataBaseReference();
    }


    private void selectFromTime(final TextView textView) {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        Format formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        textView.setText(formatter.format(cal.getTime()));
                        fromTimeInMilis = cal.getTimeInMillis();
                        Log.e("Provider", "onTimeSet: ");
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectToTime(final TextView textView) {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        Format formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        textView.setText(formatter.format(cal.getTime()));
                        toTimeInMilis = cal.getTimeInMillis();
                        Log.e("Provider", "onTimeSet: ");
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

   /* private void selectDate() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,

                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        dateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);
                        dateInmilis = cal.getTimeInMillis();

                    }
                }, year, month, day);

        datePickerDialog.show();
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void getDataBaseReference() {

        providerId = FirebaseAuth.getInstance().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("ProviderList/" + providerId + "/ParkPlaceList/" + placeId + "/SelfBookList");
        bookId = databaseReference.push().getKey();
        databaseReference.child(bookId).setValue(new SelfBook(bookId, formattedCurrentDate, fromTimeInMilis, toTimeInMilis)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Success", "onSuccess: ");
                Toast.makeText(context, "Self Booking Successful", Toast.LENGTH_SHORT).show();
                ft = fm.beginTransaction();
                MyParkingPlacesFragment fragment = new MyParkingPlacesFragment();
                ft.replace(R.id.fragmentContainer, fragment);
                ft.addToBackStack("goToMyPlace");
                ft.commit();
            }
        });

    }
}
