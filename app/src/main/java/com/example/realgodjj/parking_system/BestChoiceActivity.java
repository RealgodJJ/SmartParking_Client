package com.example.realgodjj.parking_system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BestChoiceActivity extends AppCompatActivity {

    private double ParkingFreeRate, parkFee, distance;
    private int lightNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_choice);

    }
}
