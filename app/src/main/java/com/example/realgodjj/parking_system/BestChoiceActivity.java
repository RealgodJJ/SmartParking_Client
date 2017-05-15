package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkInfoClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BestChoiceActivity extends AppCompatActivity {

    private EditText e_parkingLotId, e_parkingFreeRate, e_distance, e_parkFee;
    private int parkingFreeRate_rate, distance_rate, parkFee_rate;
    private double parkingOccupyRatePercentage, distancePercentage, parkFeePercentage;
    private double nightTime, dayTime;
    private String getParkInfo2, getParkInfo4, getParkInfo5;
    private static final int GETPARKINFO_ERROR = 1;
    private static final int GETPARKINFO_SUCCESS = 2;
    private String parkingLotUid[] = new String[3];
    private int parkingLotId[] = new int[3];
    private double parkingLotLatitude[] = new double[3];
    private double parkingLotLongitude[] = new double[3];
    private double endLatitude, endLongitude;
    private LatLng latLng2, latLng4, latLng5, latLngEnd;
    private double totalSpaces2, totalAvailable2, nightPrice2, dayPrice2;
    private double totalSpaces4, totalAvailable4, nightPrice4, dayPrice4;
    private double totalSpaces5, totalAvailable5, nightPrice5, dayPrice5;
    private double parkingOccupyRate2, parkFee2, distance2;
    private double parkingOccupyRate4, parkFee4, distance4;
    private double parkingOccupyRate5, parkFee5, distance5;
    private double parkingOccupyRateFactor2, parkFeeFactor2, distanceFactor2;
    private double parkingOccupyRateFactor4, parkFeeFactor4, distanceFactor4;
    private double parkingOccupyRateFactor5, parkFeeFactor5, distanceFactor5;
    private double averageParkingFreeRate, averageParkFee, averageDistance;
    private double overView2, overView4, overView5;
    private double targetOverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_choice);
        e_parkingLotId = (EditText) findViewById(R.id.best_choice_parking_name_edit_text);
        e_parkingFreeRate = (EditText) findViewById(R.id.best_choice_parking_free_rate_edit_text);
        e_distance = (EditText) findViewById(R.id.best_choice_distance_edit_text);
        e_parkFee = (EditText) findViewById(R.id.best_choice_parkFee_edit_text);
        e_parkingLotId.setEnabled(false);
        e_parkingFreeRate.setEnabled(false);
        e_distance.setEnabled(false);
        e_parkFee.setEnabled(false);

        //Intent get 停车的夜间和日间时间
        Bundle bundle = this.getIntent().getExtras();
        parkingFreeRate_rate = Integer.parseInt(bundle.getString("parkingFreeRate_rate"));
        distance_rate = Integer.parseInt(bundle.getString("distance_rate"));
        parkFee_rate = Integer.parseInt(bundle.getString("parkFee_rate"));
        endLatitude = bundle.getDouble("endLatitude");
        endLongitude = bundle.getDouble("endLongitude");
        //Intent get 数组(Uid, Latitude, Longitude)
        Intent intent = this.getIntent();
        parkingLotUid = intent.getStringArrayExtra("parkingLotUid");
        parkingLotId = intent.getIntArrayExtra("parkingLotId");
        parkingLotLatitude = intent.getDoubleArrayExtra("parkingLotLatitude");
        parkingLotLongitude = intent.getDoubleArrayExtra("parkingLotLongitude");
        nightTime = bundle.getDouble("nightTime");
        dayTime = bundle.getDouble("dayTime");

        System.out.println("parkingFreeRate_rate : " + parkingFreeRate_rate +
                "distance_rate : " + distance_rate + "parkFee_rate : " + parkFee_rate);

        System.out.println("\nparkingLotUid : " + parkingLotUid[0] + "::::" + parkingLotUid[1] + "::::" + parkingLotUid[2]);

        System.out.println("\nparkingLotId : " + parkingLotId[0] + "::::" + parkingLotId[1] + "::::" + parkingLotId[2]);

        System.out.println("\nparkingLotLatitude : " + parkingLotLatitude[0] + "::::" + parkingLotLatitude[1] + "::::" + parkingLotLatitude[2]);

        System.out.println("\nparkingLotLongitude : " + parkingLotLongitude[0] + "::::" + parkingLotLongitude[1] + "::::" + parkingLotLongitude[2]);

        //判断车位空闲率排名
        switch (parkingFreeRate_rate) {
            case 1:
                parkingOccupyRatePercentage = 0.5;
                break;
            case 2:
                parkingOccupyRatePercentage = 0.3;
                break;
            case 3:
                parkingOccupyRatePercentage = 0.2;
                break;
        }

        switch (distance_rate) {
            case 1:
                distancePercentage = 0.5;
                break;
            case 2:
                distancePercentage = 0.3;
                break;
            case 3:
                distancePercentage = 0.2;
                break;
        }

        switch (parkFee_rate) {
            case 1:
                parkFeePercentage = 0.5;
                break;
            case 2:
                parkFeePercentage = 0.3;
                break;
            case 3:
                parkFeePercentage = 0.2;
                break;
        }

        System.out.println("\nparkingOccupyRatePercentage : " + parkingOccupyRatePercentage +
                "\ndistancePercentage : " + distancePercentage + "\nparkFeePercentage : " + parkFeePercentage);

        latLng2 = new LatLng(parkingLotLatitude[0], parkingLotLongitude[0]);
        latLng4 = new LatLng(parkingLotLatitude[1], parkingLotLongitude[1]);
        latLng5 = new LatLng(parkingLotLatitude[2], parkingLotLongitude[2]);
        latLngEnd = new LatLng(endLatitude, endLongitude);

        distance2 = DistanceUtil.getDistance(latLng2, latLngEnd);
        distance4 = DistanceUtil.getDistance(latLng4, latLngEnd);
        distance5 = DistanceUtil.getDistance(latLng5, latLngEnd);

        System.out.println("\ndistance : " + distance2 + "::::" + distance4 + "::::" + distance5);

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
                    chooseBestParkingLot();
//                    Toast.makeText(BestChoiceActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void chooseBestParkingLot() {
        String[] strArray2 = getParkInfo2.split("!");
        totalSpaces2 = Integer.parseInt(strArray2[0]);
        totalAvailable2 = Integer.parseInt(strArray2[1]);
        dayPrice2 = Double.parseDouble(strArray2[2]);
        nightPrice2 = Double.parseDouble(strArray2[3]);
        parkFee2 = nightPrice2 * nightTime + dayPrice2 * dayTime;
        parkingOccupyRate2 = 1 - totalAvailable2 / totalSpaces2;

        String[] strArray4 = getParkInfo4.split("!");
        totalSpaces4 = Integer.parseInt(strArray4[0]);
        totalAvailable4 = Integer.parseInt(strArray4[1]);
        dayPrice4 = Double.parseDouble(strArray4[2]);
        nightPrice4 = Double.parseDouble(strArray4[3]);
        parkFee4 = nightPrice4 * nightTime + dayPrice4 * dayTime;

        parkingOccupyRate4 = 1 - totalAvailable4 / totalSpaces4;

        String[] strArray5 = getParkInfo5.split("!");
        totalSpaces5 = Integer.parseInt(strArray5[0]);
        totalAvailable5 = Integer.parseInt(strArray5[1]);
        dayPrice5 = Double.parseDouble(strArray5[2]);
        nightPrice5 = Double.parseDouble(strArray5[3]);
        parkFee5 = nightPrice5 * nightTime + dayPrice5 * dayTime;
        parkingOccupyRate5 = 1 - totalAvailable5 / totalSpaces5;

        averageParkingFreeRate = (parkingOccupyRate2 + parkingOccupyRate4 + parkingOccupyRate5) / 3;
        averageParkFee = (parkFee2 + parkFee4 + parkFee5) / 3;
        averageDistance = (distance2 + distance4 + distance5) / 3;
        //TODO
        parkingOccupyRateFactor2 = 100 / averageParkingFreeRate * (averageParkingFreeRate - parkingOccupyRate2);
        parkingOccupyRateFactor4 = 100 / averageParkingFreeRate * (averageParkingFreeRate - parkingOccupyRate4);
        parkingOccupyRateFactor5 = 100 / averageParkingFreeRate * (averageParkingFreeRate - parkingOccupyRate5);

        parkFeeFactor2 = 100 / averageParkFee * (averageParkFee - parkFee2);
        parkFeeFactor4 = 100 / averageParkFee * (averageParkFee - parkFee4);
        parkFeeFactor5 = 100 / averageParkFee * (averageParkFee - parkFee5);

        distanceFactor2 = 100 / averageDistance * (averageDistance - distance2);
        distanceFactor4 = 100 / averageDistance * (averageDistance - distance4);
        distanceFactor5 = 100 / averageDistance * (averageDistance - distance5);

        overView2 = parkingOccupyRatePercentage * parkingOccupyRateFactor2 + parkFeePercentage * parkFeeFactor2 + distancePercentage * distanceFactor2;
        overView4 = parkingOccupyRatePercentage * parkingOccupyRateFactor4 + parkFeePercentage * parkFeeFactor4 + distancePercentage * distanceFactor4;
        overView5 = parkingOccupyRatePercentage * parkingOccupyRateFactor5 + parkFeePercentage * parkFeeFactor5 + distancePercentage * distanceFactor5;

        System.out.println("\n\n\nparkingOccupyRate2 : " + parkingOccupyRate2 + "\nparkingOccupyRate4 : " +
                parkingOccupyRate4 + "\nparkingOccupyRate5 : " + parkingOccupyRate5);

        System.out.println("\n\n\nnightTime : " + nightTime + "\ndayTime : " + dayTime +
                "\nparkFee2 : " + parkFee2 + "\nparkFee4 : " + parkFee4 + "\nparkFee5 : " + parkFee5);

        System.out.println("\n\n\ndistance2 : " + distance2 + "\ndistance4 : " + distance4 + "\ndistance5 : " + distance5);

        System.out.println("\n\n\noverView2 : " + overView2 + "\noverView4 : " + overView4 + "\noverView5 : " + overView5);

        targetOverView = compareOverView(overView2, overView4, overView5);


        if (targetOverView == overView2) {
            e_parkingLotId.setText(String.valueOf(parkingLotId[0]) + "号停车场");

            parkingOccupyRate2 = (1- parkingOccupyRate2) * 100;
            BigDecimal parkingFreeRate = new BigDecimal(parkingOccupyRate2);
            parkingOccupyRate2 = parkingFreeRate.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//            DecimalFormat df = new DecimalFormat("0.000");
//            df.format(parkingOccupyRate2);
            e_parkingFreeRate.setText(String.valueOf(parkingOccupyRate2) + "%");

            e_distance.setText(String.valueOf((int)distance2) + "米");

            BigDecimal parkFee = new BigDecimal(parkFee2);
            parkFee2 = parkFee.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            e_parkFee.setText(String.valueOf(parkFee2) + "元");
        } else if (targetOverView == overView4) {
            e_parkingLotId.setText(String.valueOf(parkingLotId[1]) + "号停车场");

            parkingOccupyRate4 = (1- parkingOccupyRate4) * 100;
            BigDecimal parkingFreeRate = new BigDecimal(parkingOccupyRate4);
            parkingOccupyRate4 = parkingFreeRate.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//            DecimalFormat df = new DecimalFormat("0.000");
//            df.format(parkingOccupyRate4);
            e_parkingFreeRate.setText(String.valueOf(parkingOccupyRate4) + "%");

            e_distance.setText(String.valueOf((int)distance4) + "米");

            BigDecimal parkFee = new BigDecimal(parkFee4);
            parkFee4 = parkFee.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            e_parkFee.setText(String.valueOf(parkFee4) + "元");
        } else if (targetOverView == overView5) {
            e_parkingLotId.setText(String.valueOf(parkingLotId[2]) + "号停车场");

            parkingOccupyRate5 = (1- parkingOccupyRate5) * 100;
            BigDecimal parkingFreeRate = new BigDecimal(parkingOccupyRate5);
            parkingOccupyRate5 = parkingFreeRate.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            e_parkingFreeRate.setText(String.valueOf(parkingOccupyRate5) + "%");

            e_distance.setText(String.valueOf((int)distance5) + "米");

            BigDecimal parkFee = new BigDecimal(parkFee5);
            parkFee5 = parkFee.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            e_parkFee.setText(String.valueOf(parkFee5) + "元");
        }


//        maxOverView = Math.max(Math.max(overView2, overView4), overView5);
//        minOverView = Math.min(Math.max(overView2, overView4), overView5);
    }

    private double compareOverView(double a, double b, double c) {
        if (a > b) {
            if (c > a) {
                return c;
            } else if (c < b) {
                return a;
            } else {
                return a;
            }
        } else {
            if (c < a) {
                return b;
            } else if (c > b) {
                return c;
            } else {
                return b;
            }
        }
    }
}
