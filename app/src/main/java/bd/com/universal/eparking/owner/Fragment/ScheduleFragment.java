package bd.com.universal.eparking.owner.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import bd.com.universal.eparking.owner.DataModel.Day;
import bd.com.universal.eparking.owner.DataModel.Schedule;
import bd.com.universal.eparking.owner.R;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private TextView satFromTV,sunFromTV,monFromTV,tueFromTV,wedFromTV,thuFromTV,friFromTV;
    private String satFrom,sunFrom,monFrom,tuesFrom,wedFrom,thurFrom,friFrom;
    private String satTo,sunTo,monTo,tuesTo,wedTo,thurTo,friTo;
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
        init(view);
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

                            satFromTV.setText(schedule.getmFromTime());
                            satToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                satCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Sun)){
                            sunFromTV.setText(schedule.getmFromTime());
                            sunToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                sunCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Mon)){
                            monFromTV.setText(schedule.getmFromTime());
                            monToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                monCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Tues)){
                            tueFromTV.setText(schedule.getmFromTime());
                            tueToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                tueCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Wednes)){
                            wedFromTV.setText(schedule.getmFromTime());
                            wedToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                wedCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Thurs)){
                            thuFromTV.setText(schedule.getmFromTime());
                            thuToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                thuCheckbox.setChecked(true);
                            }
                        }
                        else if (schedule.getmDay().equals(Day.Fri)){
                            friFromTV.setText(schedule.getmFromTime());
                            friToTV.setText(schedule.getmToTime());
                            if (schedule.getmIsForRent().equals("true")){
                                friCheckbox.setChecked(true);
                            }
                        }

                    }
                }
                else {
                    CreatNewSchedule();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;



    }

    private void CreatNewSchedule() {

        HashMap<String,Object>addSchedule=new HashMap<>();

        for (int i=0;i<=6;i++){
            scheduleDB=FirebaseDatabase.getInstance().getReference("ProviderList/"+UserId+"/ParkPlaceList/"+parkPlaceId+"/Schedule/"+day[i]);
            addSchedule.put("mDay",day[i]);
            addSchedule.put("mFromTime","09:00 AM");
            addSchedule.put("mToTime","05:00 PM");
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

        satCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    satFromTV.setTextColor(getResources().getColor(R.color.black));
                    satFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    satToTV.setTextColor(getResources().getColor(R.color.black));
                    satToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    satFromTV.setTextColor(getResources().getColor(R.color.ash));
                    satFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    satToTV.setTextColor(getResources().getColor(R.color.ash));
                    satToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });

        sunCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sunFromTV.setTextColor(getResources().getColor(R.color.black));
                    sunFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    sunToTV.setTextColor(getResources().getColor(R.color.black));
                    sunToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    sunFromTV.setTextColor(getResources().getColor(R.color.ash));
                    sunFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    sunToTV.setTextColor(getResources().getColor(R.color.ash));
                    sunToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });

        monCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    monFromTV.setTextColor(getResources().getColor(R.color.black));
                    monFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    monToTV.setTextColor(getResources().getColor(R.color.black));
                    monToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    monFromTV.setTextColor(getResources().getColor(R.color.ash));
                    monFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    monToTV.setTextColor(getResources().getColor(R.color.ash));
                    monToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });

        tueCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tueFromTV.setTextColor(getResources().getColor(R.color.black));
                    tueFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    tueToTV.setTextColor(getResources().getColor(R.color.black));
                    tueToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    tueFromTV.setTextColor(getResources().getColor(R.color.ash));
                    tueFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    tueToTV.setTextColor(getResources().getColor(R.color.ash));
                    tueToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });

        wedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    wedFromTV.setTextColor(getResources().getColor(R.color.black));
                    wedFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    wedToTV.setTextColor(getResources().getColor(R.color.black));
                    wedToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    wedFromTV.setTextColor(getResources().getColor(R.color.ash));
                    wedFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    wedToTV.setTextColor(getResources().getColor(R.color.ash));
                    wedToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });

        thuCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    thuFromTV.setTextColor(getResources().getColor(R.color.black));
                    thuFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    thuToTV.setTextColor(getResources().getColor(R.color.black));
                    thuToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    thuFromTV.setTextColor(getResources().getColor(R.color.ash));
                    thuFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    thuToTV.setTextColor(getResources().getColor(R.color.ash));
                    thuToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });
        friCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    friFromTV.setTextColor(getResources().getColor(R.color.black));
                    friFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                    friToTV.setTextColor(getResources().getColor(R.color.black));
                    friToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow_2,0);
                }
                else{
                    friFromTV.setTextColor(getResources().getColor(R.color.ash));
                    friFromTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                    friToTV.setTextColor(getResources().getColor(R.color.ash));
                    friToTV.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.arrow,0);
                }

            }
        });
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
            satFrom=satFromTV.getText().toString();
            satTo=satToTV.getText().toString();
            UpdateToFirebase(satCheck,satFrom,satTo,Day.Satar);

        }
        else {
            satCheck="false";
            satFrom=satFromTV.getText().toString();
            satTo=satToTV.getText().toString();
            UpdateToFirebase(satCheck,satFrom,satTo,Day.Satar);
        }

        if (sunCheckbox.isChecked()){

            sunCheck="true";
            sunFrom=sunFromTV.getText().toString();
            sunTo=sunToTV.getText().toString();
            UpdateToFirebase(sunCheck,sunFrom,sunTo,Day.Sun);

        }
        else {
            sunCheck="false";
            sunFrom=sunFromTV.getText().toString();
            sunTo=sunToTV.getText().toString();
            UpdateToFirebase(sunCheck,sunFrom,sunTo,Day.Sun);
        }


        if (monCheckbox.isChecked()){

            monCheck="true";
            monFrom=monFromTV.getText().toString();
            monTo=monToTV.getText().toString();
            UpdateToFirebase(monCheck,monFrom,monTo,Day.Mon);

        }
        else {
            monCheck="false";
            monFrom=monFromTV.getText().toString();
            monTo=monToTV.getText().toString();
            UpdateToFirebase(monCheck,monFrom,monTo,Day.Mon);
        }

        if (tueCheckbox.isChecked()){

            tueCheck="true";
            tuesFrom=tueFromTV.getText().toString();
            tuesTo=tueToTV.getText().toString();
            UpdateToFirebase(tueCheck,tuesFrom,tuesTo,Day.Tues);

        }
        else {
            tueCheck="false";
            tuesFrom=tueFromTV.getText().toString();
            tuesTo=tueToTV.getText().toString();
            UpdateToFirebase(tueCheck,tuesFrom,tuesTo,Day.Tues);
        }
        if (wedCheckbox.isChecked()){

            wedCheck="true";
            wedFrom=wedFromTV.getText().toString();
            wedTo=wedToTV.getText().toString();
            UpdateToFirebase(wedCheck,wedFrom,wedTo,Day.Wednes);

        }
        else {
            wedCheck="false";
            wedFrom=wedFromTV.getText().toString();
            wedTo=wedToTV.getText().toString();
            UpdateToFirebase(wedCheck,wedFrom,wedTo,Day.Wednes);
        }
        if (thuCheckbox.isChecked()){

            thuCheck="true";
            thurFrom=thuFromTV.getText().toString();
            thurTo=thuToTV.getText().toString();
            UpdateToFirebase(thuCheck,thurFrom,thurTo,Day.Thurs);

        }
        else {
            thuCheck="false";
            thurFrom=thuFromTV.getText().toString();
            thurTo=thuToTV.getText().toString();
            UpdateToFirebase(thuCheck,thurFrom,thurTo,Day.Thurs);
        }
        if (friCheckbox.isChecked()){

            friCheck="true";
            friFrom=friFromTV.getText().toString();
            friTo=friToTV.getText().toString();
            UpdateToFirebase(friCheck,friFrom,friTo,Day.Fri);
        }
        else {
            friCheck="false";
            friFrom=friFromTV.getText().toString();
            friTo=friToTV.getText().toString();
            UpdateToFirebase(friCheck,friFrom,friTo,Day.Fri);
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage("New Schedule Saved Successfully").setPositiveButton("OK",onClickListener).show();

    }



    DialogInterface.OnClickListener onClickListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    break;

            }
        }
    };


    private void UpdateToFirebase(String check, String from, String to,String day) {

        HashMap<String,Object>updateSchedule=new HashMap<>();
        updateSchedule.put("mDay",day);
        updateSchedule.put("mFromTime",from);
        updateSchedule.put("mToTime",to);
        updateSchedule.put("mIsForRent",check);

        DatabaseReference scheduleDBUpdate=FirebaseDatabase.getInstance().getReference("ProviderList/"+UserId+"/ParkPlaceList/"+parkPlaceId+"/Schedule/"+day);
        scheduleDBUpdate.setValue(updateSchedule);
    }


    private void ToastError() {
        Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_LONG).show();
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
