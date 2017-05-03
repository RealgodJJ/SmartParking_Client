package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.*;
import com.example.realgodjj.parking_system.baidu.DrivingRouteOverlay;
import com.example.realgodjj.parking_system.baidu.PoiOverlay;
import com.example.realgodjj.parking_system.baidu.RoutLinePlanots;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkInfoClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BestChoiceActivity extends AppCompatActivity {

    private int parkingFreeRate_rate, distance_rate, parkFee_rate, lightNum_rate;
    private double nightTime, dayTime;
    private String getParkInfo2, getParkInfo4, getParkInfo5;

    private RoutePlanSearch routePlanSearch;
    private int duration;
    private double distance;
    private int reserveHour, reserveMinute;

    private static final int GETPARKINFO_ERROR = 1;
    private static final int GETPARKINFO_SUCCESS = 2;
    private String parkingLotUid[] = new String[10];
    private double parkingLotLatitude[] = new double[10];
    private double parkingLotLongitude[] = new double[10];
    private double endLatitude, endLongitude;
    private double totalSpaces2, totalAvailable2, nightPrice2, dayPrice2;
    private double totalSpaces4, totalAvailable4, nightPrice4, dayPrice4;
    private double totalSpaces5, totalAvailable5, nightPrice5, dayPrice5;
    private double parkingFreeRate2, parkFee2, distance2;
    private double parkingFreeRate4, parkFee4, distance4;
    private double parkingFreeRate5, parkFee5, distance5;
//    private int lightNum2, lightNum4, lightNum5;
    private double averageParkingFreeRate, averageParkFee, averageDistance, averageLightNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_choice);
        //Intent get 停车的夜间和日间时间
        Bundle bundle = this.getIntent().getExtras();
        parkingFreeRate_rate = Integer.parseInt(bundle.getString("parkingFreeRate_rate"));
        distance_rate = Integer.parseInt(bundle.getString("distance_rate"));
        parkFee_rate = Integer.parseInt(bundle.getString("parkFee_rate"));
        lightNum_rate = Integer.parseInt(bundle.getString("lightNum_rate"));
        endLatitude = bundle.getDouble("endLatitude");
        endLongitude = bundle.getDouble("endLongitude");
        //Intent get
        Intent intent = this.getIntent();
        parkingLotUid = intent.getStringArrayExtra("parkingLotUid");
        parkingLotLatitude = intent.getDoubleArrayExtra("parkingLotLatitude");
        parkingLotLongitude = intent.getDoubleArrayExtra("parkingLotLongitude");

        System.out.println("parkingFreeRate_rate : " + parkingFreeRate_rate +
                "distance_rate : " + distance_rate + "parkFee_rate : " + parkFee_rate
                + "lightNum_rate : " + lightNum_rate);
        nightTime = bundle.getDouble("nightTime");
        dayTime = bundle.getDouble("dayTime");

        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //通过uid获取所有的日间夜间停车费
                    getParkInfo2 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid[0]);
                    getParkInfo4 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid[1]);
                    getParkInfo5 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid[2]);
                    Message message = new Message();
                    message.what = GETPARKINFO_SUCCESS;
                    handler.sendMessage(message);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        });
        post_thread.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case GETPARKINFO_ERROR:
                    Toast.makeText(BestChoiceActivity.this, R.string.get_park_info_error, Toast.LENGTH_SHORT).show();
                    break;
                case GETPARKINFO_SUCCESS:
                    String[] strArray2 = getParkInfo2.split("!");
                    totalSpaces2 = Integer.parseInt(strArray2[0]);
                    totalAvailable2 = Integer.parseInt(strArray2[1]);
                    dayPrice2 = Double.parseDouble(strArray2[2]);
                    nightPrice2 = Double.parseDouble(strArray2[3]);
                    parkFee2 = nightPrice2 * nightTime + dayPrice2 * dayTime;
                    BigDecimal a = new BigDecimal(parkFee2);
                    parkFee2 = a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    parkingFreeRate2 = totalAvailable2 / totalSpaces2;

                    String[] strArray4 = getParkInfo4.split("!");
                    totalSpaces4 = Integer.parseInt(strArray4[0]);
                    totalAvailable4 = Integer.parseInt(strArray4[1]);
                    dayPrice4 = Double.parseDouble(strArray4[2]);
                    nightPrice4 = Double.parseDouble(strArray4[3]);
                    parkFee4 = nightPrice4 * nightTime + dayPrice4 * dayTime;
                    BigDecimal b = new BigDecimal(parkFee4);
                    parkFee4 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    parkingFreeRate4 = totalAvailable4 / totalSpaces4;

                    String[] strArray5 = getParkInfo5.split("!");
                    totalSpaces5 = Integer.parseInt(strArray5[0]);
                    totalAvailable5 = Integer.parseInt(strArray5[1]);
                    dayPrice5 = Double.parseDouble(strArray5[2]);
                    nightPrice5 = Double.parseDouble(strArray5[3]);
                    parkFee5 = nightPrice5 * nightTime + dayPrice5 * dayTime;
                    BigDecimal c = new BigDecimal(parkFee5);
                    parkFee5 = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    parkingFreeRate5 = totalAvailable5 / totalSpaces5;

                    averageParkingFreeRate = (parkingFreeRate2 + parkingFreeRate4 + parkingFreeRate5) / 3;
                    averageParkFee = (parkFee2 + parkFee4 + parkFee5) / 3;

                    System.out.println("\n\n\nnightTime : " + nightTime + "dayTime : " + dayTime +
                            "\nparkFee2 : " + parkFee2 + "\nparkFee4 : " + parkFee4 + "\nparkFee5 : " + parkFee5);

                    System.out.println("\n\n\nparkingFreeRate2 : " + parkingFreeRate2 + "\nparkingFreeRate4 : " +
                            parkingFreeRate4 + "\nparkingFreeRate5 : " + parkingFreeRate5);

                    Toast.makeText(BestChoiceActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
