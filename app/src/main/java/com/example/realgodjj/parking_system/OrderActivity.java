package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.realgodjj.parking_system.baidu.RoutLinePlanots;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ReserveClient;

import java.math.BigDecimal;

import static com.example.realgodjj.parking_system.RoutePlanningActivity.ROUTE_PLANNING;

public class OrderActivity extends AppCompatActivity {

    private static final int RESERVE_SUCCESS = 1;//reserve success
    private static final int RESERVE_ERROR = 2;//reserve fail
    private EditText e_bestPark, e_parkingLotAddress, e_totalSpaces, e_totalAvailable, e_startTime_hour, e_startTime_minute, e_endTime_hour, e_endTime_minute, e_totalTime, e_parkFee;
    private Button reserve;

    private String parkingLotUid, parkingLotName, parkingLotAddress, totalSpaces, totalAvailable, startTime_hour, startTime_minute, endTime_hour, endTime_minute, totalTime, totalHour, totalMinute, parkFee;
    private String isSuccess = "";
    private double fee;

    private RoutLinePlanots routLinePlanots;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        e_bestPark = (EditText) findViewById(R.id.order_best_park_edit_text);
        e_parkingLotAddress = (EditText) findViewById(R.id.order_park_address_edit_text);
        e_totalSpaces = (EditText) findViewById(R.id.order_total_spaces_edit_text);
        e_totalAvailable = (EditText) findViewById(R.id.order_total_available_edit_text);
        e_startTime_hour = (EditText) findViewById(R.id.order_start_time_hour_edit_text);
        e_startTime_minute = (EditText) findViewById(R.id.order_start_time_minute_edit_text);
        e_endTime_hour = (EditText) findViewById(R.id.order_end_time_hour_edit_text);
        e_endTime_minute = (EditText) findViewById(R.id.order_end_time_minute_edit_text);
        e_totalTime = (EditText) findViewById(R.id.order_total_time_edit_text);
        e_parkFee = (EditText) findViewById(R.id.order_park_fee_edit_text);
        reserve = (Button) findViewById(R.id.order_reserve_button);

        getIntentInfo();

//        System.out.println(parkingLotName + "\n" + parkingLotAddress + "\n" + totalSpaces + "\n" + totalAvailable
//                + "\n" + startTime_hour + ":" + startTime_minute + "\n" + endTime_hour + ":" + endTime_minute
//                + "\n" + totalTime + "\n" + parkFee);
        e_bestPark.setText(parkingLotName);
        e_parkingLotAddress.setText(parkingLotAddress);
        e_totalSpaces.setText(totalSpaces);
        e_totalAvailable.setText(totalAvailable);
        e_startTime_hour.setText(startTime_hour);
        e_startTime_minute.setText(startTime_minute);
        e_endTime_hour.setText(endTime_hour);
        e_endTime_minute.setText(endTime_minute);
        e_totalTime.setText(totalHour + "小时" + totalMinute + "分钟");
        BigDecimal b = new BigDecimal(parkFee);
        fee = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        e_parkFee.setText(fee + "元");

        e_bestPark.setEnabled(false);
        e_parkingLotAddress.setEnabled(false);
        e_totalSpaces.setEnabled(false);
        e_totalAvailable.setEnabled(false);
        e_startTime_hour.setEnabled(false);
        e_startTime_minute.setEnabled(false);
        e_endTime_hour.setEnabled(false);
        e_endTime_minute.setEnabled(false);
        e_totalTime.setEnabled(false);
        e_parkFee.setEnabled(false);

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //预约停车位(对数据库进行修改)
                Thread post_thread;
                post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(OrderActivity.this, RoutePlanningActivity.class);

                            isSuccess = ReserveClient.reserve(MyApp.getIpAddress(), MyApp.getUserName(), parkingLotUid);
                            if (TextUtils.isEmpty(isSuccess)) {
                                Message message = new Message();
                                message.what = RESERVE_ERROR;
                                handler.sendMessage(message);
                            }
                            else {
                                Message message = new Message();
                                message.what = RESERVE_SUCCESS;
                                handler.sendMessage(message);
                                MyApp.setIntent(true);
                                MyApp.setReserve(true);
                                isSuccess = "";
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("totalHour", totalHour);
                            bundle.putString("totalMinute", totalMinute);
                            bundle.putString("parkFee", String.valueOf(fee));
                            bundle.putString("parkingLotUid", parkingLotUid);
                            routLinePlanots = setPlanningRoad();
                            bundle.putParcelable(RoutePlanningActivity.ROUTE_PLANNING, routLinePlanots);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                post_thread.start();
            }
        });
    }

    //获取停车规划路沿线所需信息
    private void getIntentInfo() {
        Bundle bundle = this.getIntent().getExtras();
        parkingLotUid = bundle.getString("parkingLotUid");
        parkingLotName = bundle.getString("parkingLotName");
        parkingLotAddress = bundle.getString("parkingLotAddress");
        totalSpaces = bundle.getString("totalSpaces");
        totalAvailable = bundle.getString("totalAvailable");
        startTime_hour = bundle.getString("startTime_hour");
        startTime_minute = bundle.getString("startTime_minute");
        endTime_hour = bundle.getString("endTime_hour");
        endTime_minute = bundle.getString("endTime_minute");
        totalTime = bundle.getString("totalTime");
        totalHour = bundle.getString("totalHour");
        totalMinute = bundle.getString("totalMinute");
        parkFee = bundle.getString("parkFee");
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

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case RESERVE_ERROR:
                    Toast.makeText(OrderActivity.this, R.string.reserve_space_error, Toast.LENGTH_SHORT).show();
                    break;
                case RESERVE_SUCCESS:
                    Toast.makeText(OrderActivity.this, R.string.reserve_space_success, Toast.LENGTH_SHORT).show();
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
