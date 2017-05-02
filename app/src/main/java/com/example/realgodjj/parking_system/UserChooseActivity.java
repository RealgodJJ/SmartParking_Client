package com.example.realgodjj.parking_system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;



public class UserChooseActivity extends AppCompatActivity {

    private TextView userName;
    private EditText e_parkingFreeRate, e_distance, e_parkFee, e_lightNum;
    private String s_parkingFreeRate, s_distance, s_parkFee, s_lightNum;
    private EditText e_startTime_hour, e_startTime_minute, e_endTime_hour, e_endTime_minute;
    private Button bestEstimate;

//    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choose);
        userName = (TextView) findViewById(R.id.user_choose_username_text_view);
        e_parkingFreeRate = (EditText) findViewById(R.id.user_choose_parking_free_rate_edit_text);
        e_distance = (EditText) findViewById(R.id.user_choose_distance_edit_text);
        e_parkFee = (EditText) findViewById(R.id.user_choose_parkFee_edit_text);
        e_lightNum = (EditText) findViewById(R.id.user_choose_lightNum_edit_text);
        e_startTime_hour = (EditText) findViewById(R.id.user_choose_start_time_hour_edit_text);
        e_startTime_minute = (EditText) findViewById(R.id.user_choose_start_time_minute_edit_text);
        e_endTime_hour = (EditText) findViewById(R.id.user_choose_end_time_hour_edit_text);
        e_endTime_minute = (EditText) findViewById(R.id.user_choose_end_time_minute_edit_text);
        bestEstimate = (Button) findViewById(R.id.user_choose_best_estimate_button);

        userName.setText(MyApp.getUserName());//get userName who is logging
        //获取用户信息
        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });
        post_thread.start();

        bestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_parkingFreeRate = e_parkingFreeRate.getText().toString();
                s_distance = e_distance.getText().toString();
                s_parkFee = e_parkFee.getText().toString();
                s_lightNum = e_lightNum.getText().toString();
                if (TextUtils.isEmpty(s_parkingFreeRate) || TextUtils.isEmpty(s_distance) || TextUtils.isEmpty(s_parkFee)
                        || TextUtils.isEmpty(s_lightNum) || TextUtils.isEmpty(e_startTime_hour.getText().toString())
                        || TextUtils.isEmpty(e_startTime_minute.getText().toString()) || TextUtils.isEmpty(e_endTime_hour.getText().toString())
                        || TextUtils.isEmpty(e_endTime_minute.getText().toString())) {
                    Toast.makeText(UserChooseActivity.this, R.string.info_empty, Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(s_parkingFreeRate) < 1 || Integer.parseInt(s_parkingFreeRate) > 4
                        || Integer.parseInt(s_distance) < 1 || Integer.parseInt(s_distance) > 4
                        || Integer.parseInt(s_parkFee) < 1 || Integer.parseInt(s_parkFee) > 4
                        || Integer.parseInt(s_lightNum) < 1 || Integer.parseInt(s_lightNum) > 4) {
                    Toast.makeText(UserChooseActivity.this, R.string.input_number_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
