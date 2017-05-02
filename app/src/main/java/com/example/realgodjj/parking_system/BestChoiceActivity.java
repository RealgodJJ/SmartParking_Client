package com.example.realgodjj.parking_system;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkInfoClient;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class BestChoiceActivity extends AppCompatActivity {

    private int parkingFreeRate_rate, distance_rate, parkFee_rate, lightNum_rate;
    private double nightTime, dayTime;
    private static final String parkUid2 = "eb485de9e77e8e7451e5c556";
    private static final String parkUId4 = "59bcc8c15c2ae2edc03644ae";
    private static final String parkUId5 = "529cb484e57dffd0f6eafcad";
    private String getParkInfo2, getParkInfo4, getParkInfo5;
    private static final int GETPARKINFO_ERROR = 1;
    private static final int GETPARKINFO_SUCCESS = 2;
    private double totalSpaces2, totalAvailable2, nightPrice2, dayPrice2;
    private double totalSpaces4, totalAvailable4, nightPrice4, dayPrice4;
    private double totalSpaces5, totalAvailable5, nightPrice5, dayPrice5;
    private double ParkingFreeRate2, parkFee2, distance2;
    private double ParkingFreeRate4, parkFee4, distance4;
    private double ParkingFreeRate5, parkFee5, distance5;
    private int lightNum;

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
        System.out.println("parkingFreeRate_rate : " + parkingFreeRate_rate +
        "distance_rate : " + distance_rate + "parkFee_rate : " + parkFee_rate
        + "lightNum_rate : " + lightNum_rate);
        nightTime = bundle.getDouble("nightTime");
        dayTime = bundle.getDouble("dayTime");
        //TODO : move them into the handler


        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //通过uid获取所有的日间夜间停车费
                    getParkInfo2 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkUid2);
                    getParkInfo4 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkUId4);
                    getParkInfo5 = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkUId5);
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

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GETPARKINFO_ERROR:
                    Toast.makeText(BestChoiceActivity.this, R.string.get_park_info_error, Toast.LENGTH_SHORT).show();
                    break;
                case GETPARKINFO_SUCCESS:
                    String[] strArray2 = getParkInfo2.split("!");
                    String[] strArray4 = getParkInfo4.split("!");
                    String[] strArray5 = getParkInfo5.split("!");
                    totalSpaces2 = Integer.parseInt(strArray2[0]);
                    totalAvailable2 = Integer.parseInt(strArray2[1]);
                    dayPrice2 = Double.parseDouble(strArray2[2]);
                    nightPrice2 = Double.parseDouble(strArray2[3]);
                    parkFee2 = nightPrice2 * nightTime + dayPrice2 * dayTime;
                    BigDecimal b = new BigDecimal(parkFee2);
                    parkFee2 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    System.out.println("\n\n\nnightTime : " + nightTime + "dayTime : " + dayTime + "parkFee : " + parkFee2);
                    Toast.makeText(BestChoiceActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
