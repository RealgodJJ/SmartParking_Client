package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.realgodjj.parking_system.baidu.RoutLinePlanots;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkClient;

import java.util.Calendar;

import static com.example.realgodjj.parking_system.RoutePlanningActivity.ROUTE_PLANNING;

public class ReserveActivity extends AppCompatActivity {

    private ImageView guide;
    private EditText e_parkingLotName, e_parkingLotAddress, e_parkingLotTotalSpaces, e_parkingLotTotalAvailable, e_startTime_hour, e_startTime_minute, e_endTime_hour, e_endTime_minute, e_total_time;
    private Button createOrder;
    private String getPrice = "";
    private String parkingLotUid = "";
    private double totalHour = 0, totalMinute = 0;
    private double dayTime, nightTime;//以小时记录的日间时间和夜间时间
    private String parkingLotName, parkingLotAddress, totalSpaces, totalAvailable;
    private int currentHour, currentMinute;
    private double parkPrice, parkNightPrice;
    private double totalTime;
    private double parkFee;
    private static final int GETPARKINFO_SUCCESS = 1;
    private static final int GETPARKINFO_ERROR = 2;
    private RoutLinePlanots routLinePlanots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        guide = (ImageView) findViewById(R.id.guide_image);
        e_parkingLotName = (EditText) findViewById(R.id.reserve_park_name_edit_text);
        e_parkingLotAddress = (EditText) findViewById(R.id.reserve_park_address_edit_text);
        e_parkingLotTotalSpaces = (EditText) findViewById(R.id.reserve_total_spaces_edit_text);
        e_parkingLotTotalAvailable = (EditText) findViewById(R.id.reserve_total_available_edit_text);
        e_startTime_hour = (EditText) findViewById(R.id.reserve_start_time_hour_edit_text);
        e_startTime_minute = (EditText) findViewById(R.id.reserve_start_time_minute_edit_text);
        e_endTime_hour = (EditText) findViewById(R.id.reserve_end_time_hour_edit_text);
        e_endTime_minute = (EditText) findViewById(R.id.reserve_end_time_minute_edit_text);
        e_total_time = (EditText) findViewById(R.id.reserve_total_time_edit_text);
        createOrder = (Button) findViewById(R.id.reserve_create_order_button);
        guide.setImageResource(R.drawable.guide);

        if (MyApp.isIntent()) {
            getIntentInfo();
            e_parkingLotName.setText(parkingLotName);
            e_parkingLotAddress.setText(parkingLotAddress);
            e_parkingLotName.setEnabled(false);
            e_parkingLotAddress.setEnabled(false);
            e_total_time.setEnabled(false);
            e_parkingLotTotalSpaces.setEnabled(false);
            e_parkingLotTotalAvailable.setEnabled(false);
        }

        Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        e_startTime_hour.setText(String.valueOf(currentHour));
        e_startTime_minute.setText(String.valueOf(currentMinute));

        //获取部分停车场信息
        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getPrice = ParkClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid);
                    if (getPrice == null) {
                        Message message = new Message();
                        message.what = GETPARKINFO_ERROR;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = GETPARKINFO_SUCCESS;
                        handler.sendMessage(message);
                    }
//                    System.out.println("\n\n\n===============================" + dayTime + "===============================\n\n\n");
//                    System.out.println("\n\n\n===============================" + nightTime + "===============================\n\n\n");
//                    System.out.println("\n\n\n===============================" + parkPrice + "===============================\n\n\n");
//                    System.out.println("\n\n\n===============================" + parkNightPrice + "===============================\n\n\n");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });
        post_thread.start();

        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("dayTime : " + dayTime + "\n" + "nightTime : " + nightTime);

                if (TextUtils.isEmpty(e_startTime_hour.getText().toString()) || TextUtils.isEmpty(e_startTime_minute.getText().toString())
                        || TextUtils.isEmpty(e_endTime_hour.getText().toString()) || TextUtils.isEmpty(e_endTime_minute.getText().toString())) {
                    Toast.makeText(ReserveActivity.this, R.string.reserve_time_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(e_parkingLotTotalSpaces.getText().toString()) && TextUtils.isEmpty(e_parkingLotTotalAvailable.getText().toString())) {
                    Toast.makeText(ReserveActivity.this, R.string.create_order_not_allowed, Toast.LENGTH_SHORT).show();
                } else if(totalHour < 1) {
                    Toast.makeText(ReserveActivity.this, R.string.create_order_not_allowed, Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(e_startTime_hour.getText().toString()) >= 0 && Integer.parseInt(e_startTime_hour.getText().toString())  < 24
                        && Integer.parseInt(e_startTime_minute.getText().toString()) >= 0 && Integer.parseInt(e_startTime_minute.getText().toString()) < 60
                        && Integer.parseInt(e_endTime_hour.getText().toString()) >= 0 && Integer.parseInt(e_endTime_hour.getText().toString())  < 24
                        && Integer.parseInt(e_endTime_minute.getText().toString()) >= 0 && Integer.parseInt(e_endTime_minute.getText().toString()) < 60) {
                    //计算停车费用
                    Thread post_thread;
                    post_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parkFee = dayTime * parkPrice + nightTime * parkNightPrice;
                                System.out.println("parkFee : " + parkFee);
                                totalTime = dayTime + nightTime;
                                Intent intent = new Intent(ReserveActivity.this, OrderActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("parkingLotUid", parkingLotUid);
                                bundle.putString("parkingLotName", parkingLotName);
                                bundle.putString("parkingLotAddress", parkingLotAddress);
                                bundle.putString("totalSpaces", totalSpaces);
                                bundle.putString("totalAvailable", totalAvailable);
                                bundle.putString("startTime_hour", e_startTime_hour.getText().toString());
                                bundle.putString("startTime_minute", e_startTime_minute.getText().toString());
                                bundle.putString("endTime_hour", e_endTime_hour.getText().toString());
                                bundle.putString("endTime_minute", e_endTime_minute.getText().toString());
                                bundle.putString("totalTime", String.valueOf(totalTime));
                                bundle.putString("totalHour", String.valueOf(totalHour));
                                bundle.putString("totalMinute", String.valueOf(totalMinute));
                                bundle.putString("parkFee", String.valueOf(parkFee));

                                routLinePlanots = setPlanningRoad();
                                bundle.putParcelable(RoutePlanningActivity.ROUTE_PLANNING, routLinePlanots);

                                intent.putExtras(bundle);
                                startActivity(intent);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    post_thread.start();
                } else {
                    Toast.makeText(ReserveActivity.this, R.string.reserve_time_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //开始时间小时监听事件
        e_startTime_hour.addTextChangedListener(new TextWatcher() {
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
                editStart = e_startTime_hour.getSelectionStart();
                editEnd = e_startTime_hour.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        //开始时间分钟监听事件
        e_startTime_minute.addTextChangedListener(new TextWatcher() {
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
                editStart = e_startTime_minute.getSelectionStart();
                editEnd = e_startTime_minute.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_minute.setText(temp);
                    e_startTime_minute.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

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
    }

    //获取停车规划路沿线所需信息
    private void getIntentInfo() {
        Bundle bundle = this.getIntent().getExtras();
        parkingLotUid = bundle.getString("parkingLotUid");
        parkingLotName = bundle.getString("parkingLotName");
        parkingLotAddress = bundle.getString("parkingLotAddress");
        Intent intent = getIntent();
        routLinePlanots = intent.getParcelableExtra(ROUTE_PLANNING);
    }

    //设定导航的起点和终点
    @NonNull
    private RoutLinePlanots setPlanningRoad() {
        routLinePlanots = new RoutLinePlanots();
        PlanNode startNode = PlanNode.withLocation(new LatLng(MyApp.getCurrBDLocation().getLatitude(), MyApp.getCurrBDLocation().getLongitude()));
        PlanNode targetNode = PlanNode.withLocation(new LatLng(MyApp.getCurrClickPoi().location.latitude, MyApp.getCurrClickPoi().location.longitude));
        routLinePlanots.setStartPlanNode(startNode);
        routLinePlanots.setTargetPlanNode(targetNode);
        return routLinePlanots;
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
                case GETPARKINFO_SUCCESS:
                    String[] strArray = getPrice.split("!");
                    totalSpaces = strArray[0];
                    totalAvailable = strArray[1];
                    parkPrice = Double.parseDouble(strArray[2]);
                    parkNightPrice = Double.parseDouble(strArray[3]);
                    e_parkingLotTotalSpaces.setText(strArray[0]);
                    e_parkingLotTotalAvailable.setText(totalAvailable);
//                    Toast.makeText(ReserveActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
                    break;
                case GETPARKINFO_ERROR:
                    Toast.makeText(ReserveActivity.this, R.string.reserve_space_not_allowed, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
