package com.example.realgodjj.parking_system;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkClient;
import com.example.realgodjj.parking_system.client.ReserveClient;
import com.example.realgodjj.parking_system.simulation.Park;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SetParkingActivity extends AppCompatActivity {

    private EditText e_parkingLotName, e_parkingLotAddress, e_totalAvailable, e_startTime_hour, e_startTime_minute, e_endTime_hour, e_endTime_minute, e_total_time;
    private Button beginCountTime;
    private String parkingLotUid;
    private double totalHour = 0, totalMinute = 0;
    private double dayTime, nightTime;//以小时记录的日间时间和夜间时间
    private String isSuccess = "";
    private static final int GET_TOTAL_AVAILABLE_ERROR = 1;
    private static final int GET_TOTAL_AVAILABLE_SUCCESS = 2;
    private static final int SYNCHRONIZE_TIME = 3;
    private Timer timer;
    private TimerTask timerTask_synchronize;
    private int hour, minute;
    private double parkPrice, parkNightPrice;
    private double parkFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_parking);
        e_parkingLotName = (EditText) findViewById(R.id.set_parking_park_name_edit_text);
        e_parkingLotAddress = (EditText) findViewById(R.id.set_parking_park_address_edit_text);
        e_totalAvailable = (EditText) findViewById(R.id.set_parking_total_available_edit_text);
        e_startTime_hour = (EditText) findViewById(R.id.set_parking_start_time_hour_edit_text);
        e_startTime_minute = (EditText) findViewById(R.id.set_parking_start_time_minute_edit_text);
        e_endTime_hour = (EditText) findViewById(R.id.set_parking_end_time_hour_edit_text);
        e_endTime_minute = (EditText) findViewById(R.id.set_parking_end_time_minute_edit_text);
        e_total_time = (EditText) findViewById(R.id.set_parking_total_time_edit_text);
        beginCountTime = (Button) findViewById(R.id.set_parking_create_order_button);

        Bundle bundle = this.getIntent().getExtras();
        parkingLotUid = bundle.getString("parkingLotUid");
        String parkingLotName = bundle.getString("parkingLotName");
        String parkingLotAddress = bundle.getString("parkingLotAddress");

        e_parkingLotName.setText(parkingLotName);
        e_parkingLotAddress.setText(parkingLotAddress);

        e_parkingLotName.setEnabled(false);
        e_parkingLotAddress.setEnabled(false);
        e_totalAvailable.setEnabled(false);
        e_startTime_hour.setEnabled(false);
        e_startTime_minute.setEnabled(false);
        e_total_time.setEnabled(false);

        timer = new Timer();
        synchronize();

        //获取停车空余车位
        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Park park = new Park();
                    park.setParkName(parkingLotUid);
                    isSuccess = ParkClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid);
                    if (isSuccess.equals("")) {
                        Message message = new Message();
                        message.what = GET_TOTAL_AVAILABLE_ERROR;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = GET_TOTAL_AVAILABLE_SUCCESS;
                        handler.sendMessage(message);
                    }
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        });
        post_thread.start();

        //结束时间小时监听事件
        e_endTime_hour.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_endTime_hour.getSelectionStart();
                editEnd = e_endTime_hour.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_endTime_hour.setText(temp);
                    e_endTime_hour.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        //结束时间分钟监听事件
        e_endTime_minute.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_endTime_minute.getSelectionStart();
                editEnd = e_endTime_minute.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_endTime_minute.setText(temp);
                    e_endTime_minute.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        beginCountTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //占据停车位(对数据库进行修改)
                Thread post_thread;
                post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isSuccess = ReserveClient.reserve(MyApp.getIpAddress(), MyApp.getUserName(), parkingLotUid);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                post_thread.start();
                System.out.println("dayTime : " + dayTime + "\nparkPrice : " + parkPrice + "\nnightTime : " + nightTime + "\n" + "\nparkNightPrice" + parkNightPrice);
                parkFee = dayTime * parkPrice + nightTime * parkNightPrice;
                Intent intent = new Intent(SetParkingActivity.this, TimingActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("totalHour", String.valueOf(totalHour));
                bundle1.putString("totalMinute", String.valueOf(totalMinute));
                bundle1.putString("parkFee", String.valueOf(parkFee));
                bundle1.putString("parkingLotUid", parkingLotUid);
                intent.putExtras(bundle1);
                startActivity(intent);
                finish();
            }
        });
    }

    //同步时间
    private void synchronize() {
        timerTask_synchronize = new TimerTask() {
            @Override
            public void run() {
                try {
                    Message message = new Message();
                    message.what = SYNCHRONIZE_TIME;
                    handler.sendMessage(message);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask_synchronize, 0, 1000);
    }

    private void setTotalTime(String startH, String startM, String endH, String endM) {
        if (!("".equals(startH) || "".equals(startM) || "".equals(endH) || "".equals(endM))) {
            if (Integer.parseInt(startH) >= 24) {
                e_total_time.setText(R.string.start_time_hour_error);
                return;
            }

            if (Integer.parseInt(startM) >= 60) {
                e_total_time.setText(R.string.start_time_minute_error);
                return;
            }

            if (Integer.parseInt(endH) >= 24) {
                e_total_time.setText(R.string.end_time_hour_error);
                return;
            }

            if (Integer.parseInt(endM) >= 60) {
                e_total_time.setText(R.string.end_time_minute_error);
                return;
            }

            if (Integer.parseInt(endM) >= Integer.parseInt(startM)) {
                totalMinute = Double.parseDouble(endM) - Double.parseDouble(startM);
                totalHour = Double.parseDouble(endH) - Double.parseDouble(startH);
            } else {
                totalMinute = Double.parseDouble(endM) + 60 - Double.parseDouble(startM);
                totalHour = Double.parseDouble(endH) - 1 - Double.parseDouble(startH);
            }
            if(totalHour < 0) {
                //以小时记录的日间时间和夜间时间
                if(Integer.parseInt(startH) >= 20 && Integer.parseInt(endH) >= 7) {
                    nightTime = 24.0 - Double.parseDouble(startH) - 1.0 + (60.0 - Double.parseDouble(startM)) / 60.0 + 7.0;
                    dayTime = totalHour + totalMinute / 60 + 24.0 - nightTime;
                } else if (Integer.parseInt(startH) >= 20 && Integer.parseInt(endH) < 7) {
                    nightTime = 24.0 - Double.parseDouble(startH) - 1.0 + (60.0 - Double.parseDouble(startM)) / 60.0 + Double.parseDouble(endH) + Double.parseDouble(endM) / 60;
                    dayTime = 0;
                } else if (Integer.parseInt(startH) < 20 && Integer.parseInt(endH) >= 7) {
                    nightTime = 4.0 +  7.0;
                    dayTime = totalHour + totalMinute / 60.0 + 24.0 - nightTime;
                } else {
                    nightTime = 4.0 + Double.parseDouble(endH) + Double.parseDouble(endM) / 60;
                    dayTime = totalHour + totalMinute / 60.0 + 24.0 - nightTime;
                }
                //显示停车总时间
                totalHour = totalHour + 24;
                if (totalHour == 0) {
                    e_total_time.setText(String.valueOf(totalMinute) + "分钟(隔夜)");
                } else if (totalMinute == 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时(隔夜)");
                } else if (totalHour != 0 && totalMinute != 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时" + String.valueOf(totalMinute) + "分钟(隔夜)");
                }
            } else {
                //以小时记录的日间时间和夜间时间
                if(Integer.parseInt(startH) < 7 && Integer.parseInt(endH) < 20) {
                    dayTime = Double.parseDouble(endH) - 7.0 + Double.parseDouble(endM) / 60;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                } else if (Integer.parseInt(startH) < 7 && Integer.parseInt(endH) >= 20) {
                    dayTime = 13.0;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                } else if (Integer.parseInt(startH) >= 7 && Integer.parseInt(endH) < 20) {
                    if (Integer.parseInt(endM) >= Integer.parseInt(startM)) {
                        totalMinute = Double.parseDouble(endM) - Double.parseDouble(startM);
                        totalHour = Double.parseDouble(endH) - Double.parseDouble(startH);
                    } else {
                        totalMinute = Double.parseDouble(endM) + 60 - Double.parseDouble(startM);
                        totalHour = Double.parseDouble(endH) - 1 - Double.parseDouble(startH);
                    }
                    dayTime = totalHour + totalMinute / 60;
                    nightTime = 0;
                } else {
                    dayTime = 20.0 - Double.parseDouble(startH) - 1.0 + (60 - Double.parseDouble(startM)) / 60;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                }
                //显示停车总时间
                if (totalHour == 0) {
                    e_total_time.setText(String.valueOf(totalMinute) + "分钟");
                } else if (totalMinute == 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时");
                } else if (totalHour != 0 && totalMinute != 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时" + String.valueOf(totalMinute) + "分钟");
                }
            }
        } else {
            e_total_time.setText("");
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GET_TOTAL_AVAILABLE_ERROR:
                    Toast.makeText(SetParkingActivity.this, R.string.get_total_available_error, Toast.LENGTH_SHORT).show();
                    break;

                case GET_TOTAL_AVAILABLE_SUCCESS:
                    String[] strArray = isSuccess.split("!");
                    e_totalAvailable.setText(strArray[1]);
                    parkPrice = Double.parseDouble(strArray[2]);
                    parkNightPrice = Double.parseDouble(strArray[3]);
//                    parkFee = dayTime * parkPrice + nightTime * parkNightPrice;
                    System.out.println("-----------------------" + parkPrice + "+++++++++++++++++++" + parkNightPrice);
                    break;

                case SYNCHRONIZE_TIME:
                    Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                    e_startTime_hour.setText(String.valueOf(hour));
                    e_startTime_minute.setText(String.valueOf(minute));
                    setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(),
                            e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
                    break;

                default:
                    break;
            }
        }
    };
}
