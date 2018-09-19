package com.nerdcastle.eparkingprovider.Fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nerdcastle.eparkingprovider.DataModel.Day;
import com.nerdcastle.eparkingprovider.DataModel.Schedule;
import com.nerdcastle.eparkingprovider.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private TextView satFromTV,sunFromTV,monFromTV,tueFromTV,wedFromTV,thuFromTV,friFromTV;
    private long satFrom,sunFrom,monFrom,tuesFrom,wedFrom,thurFrom,friFrom;
    private long satTo,sunTo,monTo,tuesTo,wedTo,thurTo,friTo;
    private String satCheck,sunCheck,monCheck,tueCheck,wedCheck,thuCheck,friCheck;
    private TextView satToTV,sunToTV,monToTV,tueToTV,wedToTV,thuToTV,friToTV;
    private TextView satDayTV,sunDayTV,monDayTV,tueDayTV,wedDayTV,thuDayTV,friDayTV;
    private TextView placeTitleTV;
    private CheckBox satCheckbox,sunCheckbox,monCheckbox,tueCheckbox,wedCheckbox,thuCheckbox,friCheckbox;
    private Button saveScheduleBtn;
    private int count=0;
    private String PlaceTitle;
    private int hour,minute;
    private long fromTimeInMilis;
    private Context context;
    private DatabaseReference scheduleDB,addScheduleDB;
    private FirebaseAuth mAuth;
    private String UserId;
    private String parkPlaceId;
    private boolean uiLoad=false;
    Format formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
    String[] day={Day.Satar,Day.Sun,Day.Mon,Day.Tues,Day.Wednes,Day.Thurs,Day.Fri};


    public interface ScheduleFragmentInterface {
        public void goToSchedule();
    }
    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mAuth=FirebaseAuth.getInstance();
        UserId=mAuth.getCurrentUser().getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            parkPlaceId = bundle.getString("PlaceId");
            PlaceTitle = bundle.getString("PlaceTitle");
        }



        count=0;
        scheduleDB=FirebaseDatabase.getInstance().getReference("ProviderList/"+UserId+"/ParkPlaceList/"+parkPlaceId+"/Schedule");
        scheduleDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        Schedule schedule=data.getValue(Schedule.class);
                        count++;

                        if (schedule.getmDay().equals(Day.Satar)){

                            FormatedTime(satFromTV,schedule.getmFromTime());
                            FormatedTime(satToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                satCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Sun)){
                            FormatedTime(sunFromTV,schedule.getmFromTime());
                            FormatedTime(sunToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                sunCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Mon)){
                            FormatedTime(monFromTV,schedule.getmFromTime());
                            FormatedTime(monToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                monCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Tues)){
                            FormatedTime(tueFromTV,schedule.getmFromTime());
                            FormatedTime(tueToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                tueCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Wednes)){
                            FormatedTime(wedFromTV,schedule.getmFromTime());
                            FormatedTime(wedToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                wedCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Thurs)){
                            FormatedTime(thuFromTV,schedule.getmFromTime());
                            FormatedTime(thuToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                thuCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Fri)){
                            FormatedTime(friFromTV,schedule.getmFromTime());
                            FormatedTime(friToTV,schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                friCheckbox.setChecked(true);
                            }
                        }

                    }
                }
                else {
                    //Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
                    CreatNewSchedule();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        init(view);



        return view;

    }

    private void CreatNewSchedule() {

        HashMap<String,Object>addSchedule=new HashMap<>();

        for (int i=0;i<=6;i++){
            scheduleDB=FirebaseDatabase.getInstance().getReference("ProviderList/"+UserId+"/ParkPlaceList/"+parkPlaceId+"/Schedule/"+day[i]);
            addSchedule.put("mDay",day[i]);
            addSchedule.put("mFromTime",62476999);
            addSchedule.put("mToTime",62476999);
            addSchedule.put("mIsForRent","false");
            scheduleDB.setValue(addSchedule);
        }

    }

    private void init(View view) {


        placeTitleTV=view.findViewById(R.id.placeTitleId);
        satDayTV = view.findViewById(R.id.satDay);
        sunDayTV = view.findViewById(R.id.sunDay);
        monDayTV = view.findViewById(R.id.monDay);
        tueDayTV = view.findViewById(R.id.tueDay);
        wedDayTV = view.findViewById(R.id.wedDay);
        thuDayTV = view.findViewById(R.id.thuDay);
        friDayTV = view.findViewById(R.id.friDay);

        satFromTV = view.findViewById(R.id.satFromId);
        sunFromTV = view.findViewById(R.id.sunFromId);
        monFromTV = view.findViewById(R.id.monFromId);
        tueFromTV = view.findViewById(R.id.tueFromId);
        wedFromTV = view.findViewById(R.id.wedFromId);
        thuFromTV = view.findViewById(R.id.thusFromId);
        friFromTV = view.findViewById(R.id.friFromId);

        satToTV = view.findViewById(R.id.satToId);
        sunToTV = view.findViewById(R.id.sunToId);
        monToTV = view.findViewById(R.id.monToId);
        tueToTV = view.findViewById(R.id.tueToId);
        wedToTV = view.findViewById(R.id.wedToId);
        thuToTV = view.findViewById(R.id.thuToId);
        friToTV = view.findViewById(R.id.friToId);


        satCheckbox = view.findViewById(R.id.satCheckboxId);
        sunCheckbox = view.findViewById(R.id.sunCheckboxId);
        monCheckbox = view.findViewById(R.id.monCheckboxId);
        tueCheckbox = view.findViewById(R.id.tueCheckboxId);
        wedCheckbox = view.findViewById(R.id.wedCheckboxId);
        thuCheckbox = view.findViewById(R.id.thusCheckboxId);
        friCheckbox = view.findViewById(R.id.friCheckboxId);

        saveScheduleBtn = view.findViewById(R.id.saveSchedule);


        satFromTV.setOnClickListener(this);
        sunFromTV.setOnClickListener(this);
        monFromTV.setOnClickListener(this);
        tueFromTV.setOnClickListener(this);
        wedFromTV.setOnClickListener(this);
        thuFromTV.setOnClickListener(this);
        friFromTV.setOnClickListener(this);

        satToTV.setOnClickListener(this);
        sunToTV.setOnClickListener(this);
        monToTV.setOnClickListener(this);
        tueToTV.setOnClickListener(this);
        wedToTV.setOnClickListener(this);
        thuToTV.setOnClickListener(this);
        friToTV.setOnClickListener(this);
        saveScheduleBtn.setOnClickListener(this);


        if (!PlaceTitle.isEmpty()){
            placeTitleTV.setText("Schedule of "+PlaceTitle);
        }
    }



    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.satFromId:
                if ((satCheckbox.isChecked())){
                    selectTime(satFromTV);
                }
                else {
                    ToastError();
                }
                break;
            case R.id.sunFromId:
                if ((sunCheckbox.isChecked())){
                    selectTime(sunFromTV);
                } else {
                   ToastError();
                }
                break;
            case R.id.monFromId:
                if((monCheckbox.isChecked())){
                    selectTime(monFromTV);
                } else {
                    ToastError();
                }
                break;
            case R.id.tueFromId:
                if((tueCheckbox.isChecked())){
                    selectTime(tueFromTV);
                } else {
                    ToastError();
                }
                break;
            case R.id.wedFromId:
                if((wedCheckbox.isChecked())){
                    selectTime(wedFromTV);
                } else {
                    ToastError();
                }
                break;
            case R.id.thusFromId:
                if ((thuCheckbox.isChecked())){
                    selectTime(thuFromTV);
                } else {
                   ToastError();
                }
                break;
            case R.id.friFromId:
                if(friCheckbox.isChecked()){
                    selectTime(friFromTV);
                } else {
                   ToastError();
                }
                break;

            case R.id.satToId:
                if(satCheckbox.isChecked()){
                    selectTime(satToTV);
                } else {
                    ToastError();
                }
                break;

            case R.id.sunToId:
                if(sunCheckbox.isChecked()){
                    selectTime(sunToTV);
                } else {
                   ToastError();
                }
                break;
            case R.id.monToId:
                if(monCheckbox.isChecked()){
                    selectTime(monToTV);
                } else {
                   ToastError();
                }
                break;
            case R.id.tueToId:
                if(tueCheckbox.isChecked()){
                    selectTime(tueToTV);
                } else {
                   ToastError();
                }
                break;
            case R.id.wedToId:
                if(wedCheckbox.isChecked()){
                    selectTime(wedToTV);
                } else {
                    ToastError();
                }
                break;
            case R.id.thuToId:
                if (thuCheckbox.isChecked()){
                    selectTime(thuToTV);
                } else {
                    ToastError();
                }
                break;
            case R.id.friToId:
                if (friCheckbox.isChecked()){
                    selectTime(friToTV);
                } else {

                }
                break;
            case R.id.saveSchedule:
                SavedToFirebase();
                break;
        }

    }

    private void SavedToFirebase() {
        if (satCheckbox.isChecked()){

            satCheck="true";
            satFrom=ConvertToMiliSeconds(satFromTV.getText().toString());
            satTo=ConvertToMiliSeconds(satToTV.getText().toString());
            UpdateToFirebase(satCheck,satFrom,satTo,Day.Satar);

        }
        else {
            satCheck="false";
            satFrom=ConvertToMiliSeconds(satFromTV.getText().toString());
            satTo=ConvertToMiliSeconds(satToTV.getText().toString());
            UpdateToFirebase(satCheck,satFrom,satTo,Day.Satar);
        }

        if (sunCheckbox.isChecked()){

            sunCheck="true";
            sunFrom=ConvertToMiliSeconds(sunFromTV.getText().toString());
            sunTo=ConvertToMiliSeconds(sunToTV.getText().toString());
            UpdateToFirebase(sunCheck,sunFrom,sunTo,Day.Sun);

        }
        else {
            sunCheck="false";
            sunFrom=ConvertToMiliSeconds(sunFromTV.getText().toString());
            sunTo=ConvertToMiliSeconds(sunToTV.getText().toString());
            UpdateToFirebase(sunCheck,sunFrom,sunTo,Day.Sun);
        }


        if (monCheckbox.isChecked()){

            monCheck="true";
            monFrom=ConvertToMiliSeconds(monFromTV.getText().toString());
            monTo=ConvertToMiliSeconds(monToTV.getText().toString());
            UpdateToFirebase(monCheck,monFrom,monTo,Day.Mon);

        }
        else {
            monCheck="false";
            monFrom=ConvertToMiliSeconds(monFromTV.getText().toString());
            monTo=ConvertToMiliSeconds(monToTV.getText().toString());
            UpdateToFirebase(monCheck,monFrom,monTo,Day.Mon);
        }

        if (tueCheckbox.isChecked()){

            tueCheck="true";
            tuesFrom=ConvertToMiliSeconds(tueFromTV.getText().toString());
            tuesTo=ConvertToMiliSeconds(tueToTV.getText().toString());
            UpdateToFirebase(tueCheck,tuesFrom,tuesTo,Day.Tues);

        }
        else {
            tueCheck="false";
            tuesFrom=ConvertToMiliSeconds(tueFromTV.getText().toString());
            tuesTo=ConvertToMiliSeconds(tueToTV.getText().toString());
            UpdateToFirebase(tueCheck,tuesFrom,tuesTo,Day.Tues);
        }
        if (wedCheckbox.isChecked()){

            wedCheck="true";
            wedFrom=ConvertToMiliSeconds(wedFromTV.getText().toString());
            wedTo=ConvertToMiliSeconds(wedToTV.getText().toString());
            UpdateToFirebase(wedCheck,wedFrom,wedTo,Day.Wednes);

        }
        else {
            wedCheck="false";
            wedFrom=ConvertToMiliSeconds(wedFromTV.getText().toString());
            wedTo=ConvertToMiliSeconds(wedToTV.getText().toString());
            UpdateToFirebase(wedCheck,wedFrom,wedTo,Day.Wednes);
        }
        if (thuCheckbox.isChecked()){

            thuCheck="true";
            thurFrom=ConvertToMiliSeconds(thuFromTV.getText().toString());
            thurTo=ConvertToMiliSeconds(thuToTV.getText().toString());
            UpdateToFirebase(thuCheck,thurFrom,thurTo,Day.Thurs);

        }
        else {
            thuCheck="false";
            thurFrom=ConvertToMiliSeconds(thuFromTV.getText().toString());
            thurTo=ConvertToMiliSeconds(thuToTV.getText().toString());
            UpdateToFirebase(thuCheck,thurFrom,thurTo,Day.Thurs);
        }
        if (friCheckbox.isChecked()){

            friCheck="true";
            friFrom=ConvertToMiliSeconds(friFromTV.getText().toString());
            friTo=ConvertToMiliSeconds(friToTV.getText().toString());
            UpdateToFirebase(friCheck,friFrom,friTo,Day.Fri);
        }
        else {
            friCheck="false";
            friFrom=ConvertToMiliSeconds(friFromTV.getText().toString());
            friTo=ConvertToMiliSeconds(friToTV.getText().toString());
            UpdateToFirebase(friCheck,friFrom,friTo,Day.Fri);
        }

        Toast.makeText(context, "Schedule Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private void UpdateToFirebase(String check, long from, long to,String day) {

        HashMap<String,Object>updateSchedule=new HashMap<>();
        updateSchedule.put("mDay",day);
        updateSchedule.put("mFromTime",from);
        updateSchedule.put("mToTime",to);
        updateSchedule.put("mIsForRent",check);

        DatabaseReference scheduleDBUpdate=FirebaseDatabase.getInstance().getReference("ProviderList/"+UserId+"/ParkPlaceList/"+parkPlaceId+"/Schedule/"+day);
        scheduleDBUpdate.setValue(updateSchedule);
    }

    private long ConvertToMiliSeconds(String s) {
        String[] spitedTime=s.split(":| ");
        int curretHour=Integer.valueOf(spitedTime[0]);
        int curretMin=Integer.valueOf(spitedTime[1]);

        if (spitedTime[2].equals("PM")){
            curretHour=curretHour+12;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, curretHour);
        cal.set(Calendar.MINUTE, curretMin);

        return cal.getTimeInMillis();
    }

    private void ToastError() {
        Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
    }

    private void selectTime(final TextView textView) {
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
                        textView.setText(formatter.format(cal.getTime()));
                        fromTimeInMilis = cal.getTimeInMillis();
                        Log.e("Provider", "onTimeSet: ");
                    }
                }, hour, minute, false);
        timePickerDialog.show();

    }


    private void FormatedTime(TextView textView,long timeInMilli){

        textView.setText(formatter.format(timeInMilli));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


}
