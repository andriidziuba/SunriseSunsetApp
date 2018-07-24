package com.example.riseset;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

public class DatePickerActivity extends AppCompatActivity {

    private CalendarView calendarView = null;
    private static long prevDate = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);
        calendarView = findViewById(R.id.calendarView);
        if(prevDate == 0) prevDate = calendarView.getDate();
        else calendarView.setDate(prevDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView cal, int i, int i1, int i2) {
                if(cal.getDate() != prevDate) {
                    Log.d("myTag", "onSelectedDayChange: " + calendarView.getDate());
                    prevDate = cal.getDate();
                    Intent intent = new Intent();
                    intent.putExtra("date", cal.getDate());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
