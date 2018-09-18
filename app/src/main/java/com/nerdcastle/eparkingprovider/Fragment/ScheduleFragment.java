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

import com.nerdcastle.eparkingprovider.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private TextView satFromTV,sunFromTV,monFromTV,tueFromTV,wedFromTV,thuFromTV,friFromTV;
    private TextView satToTV,sunToTV,monToTV,tueToTV,wedToTV,thuToTV,friToTV;
    private TextView satDayTV,sunDayTV,monDayTV,tueDayTV,wedDayTV,thuDayTV,friDayTV;
    private CheckBox satCheckbox,sunCheckbox,monCheckbox,tueCheckbox,wedCheckbox,thuCheckbox,friCheckbox;
    private Button saveScheduleBtn;
    private int hour,minute;
    private long fromTimeInMilis;
    private Context context;



    public interface ScheduleFragmentInterface {
        public void goToSchedule();
    }
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_schedule, container, false);
          init(view);

        return view;
    }

    private void init(View view) {

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
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sunFromId:
                if ((sunCheckbox.isChecked())){
                    selectTime(sunFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.monFromId:
                if((monCheckbox.isChecked())){
                    selectTime(monFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tueFromId:
                if((tueCheckbox.isChecked())){
                    selectTime(tueFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wedFromId:
                if((wedCheckbox.isChecked())){
                    selectTime(wedFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.thusFromId:
                if ((thuCheckbox.isChecked())){
                    selectTime(thuFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.friFromId:
                if(friCheckbox.isChecked()){
                    selectTime(friFromTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.satToId:
                if(satCheckbox.isChecked()){
                    selectTime(satToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.sunToId:
                if(sunCheckbox.isChecked()){
                    selectTime(sunToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.monToId:
                if(monCheckbox.isChecked()){
                    selectTime(monToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tueToId:
                if(tueCheckbox.isChecked()){
                    selectTime(tueToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wedToId:
                if(wedCheckbox.isChecked()){
                    selectTime(wedToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.thuToId:
                if (thuCheckbox.isChecked()){
                    selectTime(thuToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.friToId:
                if (friCheckbox.isChecked()){
                    selectTime(friToTV);
                } else {
                    Toast.makeText(context, "Please checked for rent option to set parking Schedule", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.saveSchedule:
                Toast.makeText(context, "Schedule Saved", Toast.LENGTH_SHORT).show();
                break;
        }

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
                        Format formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        textView.setText(formatter.format(cal.getTime()));
                        fromTimeInMilis = cal.getTimeInMillis();
                        Log.e("Provider", "onTimeSet: ");
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


}
