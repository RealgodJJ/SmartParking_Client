package com.example.realgodjj.parking_system;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ReserveOperateClient;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimingActivity extends AppCompatActivity {

    private String totalHour, totalMinute, parkFee, parkingLotUid;
    private int hour, minute;
    private TextView timing;
    private Button sureToStop;
    private String isSuccess = "";
    private Timer timer;
    private TimerTask timerTask_start;
    private int count = 0;
    private static final int UPDATE_TIME = 1;
    private static final int UNDO_RESERVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        timing = (TextView) findViewById(R.id.timing_timing_text_view);
        sureToStop = (Button) findViewById(R.id.timing_sure_to_stop_button);

        Bundle bundle = this.getIntent().getExtras();
        totalHour = bundle.getString("totalHour");
        totalMinute = bundle.getString("totalMinute");
        parkFee = bundle.getString("parkFee");
        parkingLotUid = bundle.getString("parkingLotUid");
        hour = (int) Double.parseDouble(totalHour);
        minute = (int) Double.parseDouble(totalMinute);
        System.out.println("parkFee : " + parkFee);

        timer = new Timer();
        start();

        sureToStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    //开始计时
    private void start() {
        timerTask_start = new TimerTask() {
            @Override
            public void run() {
                try {
                    Message message = new Message();
                    message.what = UPDATE_TIME;
                    handler.sendMessage(message);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask_start, 0, 1000);
    }


    //显示时间
    private String getStringTime(int count) {
        int h = count / 3600;
        int m = count % 3600 / 60;
        int s = count % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", h, m, s);
    }

    public void showDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(TimingActivity.this);
        build.setIcon(R.drawable.clock);
        build.setTitle(R.string.sure_parking_over);

        build.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!timerTask_start.cancel()) {
                    timerTask_start.cancel();
                    timer.cancel();
                }

                MyApp.setReserve(false);

                //归还预约停车位(对数据库进行修改)
                Thread post_thread;
                post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isSuccess = ReserveOperateClient.undoReserve(MyApp.getIpAddress(), MyApp.getUserName(), parkingLotUid);
                            if(!TextUtils.isEmpty(isSuccess)) {
                                Message message = new Message();
                                message.what = UNDO_RESERVE;
                                handler.sendMessage(message);
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                post_thread.start();
                System.out.println("11111111111111111111" + parkFee + "22222222222222222");
                Intent intent = new Intent(TimingActivity.this, PayingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("parkFee", parkFee);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        build.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        build.show();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_TIME:
                    timing.setText(getStringTime(count++));
//                    System.out.println("Park Timing..........................." + count);
                    //到达预订时间
                    if (hour * 3600 + minute * 60 == count) {

                        Thread new_post_thread;
                        new_post_thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    isSuccess = ReserveOperateClient.undoReserve(MyApp.getIpAddress(), MyApp.getUserName(), parkingLotUid);
                                    if(!TextUtils.isEmpty(isSuccess)) {
                                        Message message = new Message();
                                        message.what = UNDO_RESERVE;
                                        handler.sendMessage(message);
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        new_post_thread.start();

                        Intent intent = new Intent(TimingActivity.this, PayingActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("parkFee", parkFee);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    break;

                case UNDO_RESERVE:
                    Toast.makeText(TimingActivity.this, R.string.undo_reserve, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            Toast.makeText(TimingActivity.this, R.string.timing, Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);
        }
        return super.dispatchKeyEvent(event);
    }
}
