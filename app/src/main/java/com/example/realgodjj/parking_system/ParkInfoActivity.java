package com.example.realgodjj.parking_system;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkInfoClient;

public class ParkInfoActivity extends AppCompatActivity {

    private ImageView parking_lot;
    private EditText e_parkingLotName, e_parkingLotAddress, e_destination, e_totalSpaces, e_totalAvailable, e_parkPrice, e_parkNightPrice;
    private String parkingLotUid, s_parkingLotName, s_parkingLotAddress, s_parkingLotLatitude, s_parkingLotLongitude, s_destination;
    private String isSuccess = "";
    private static final int GETPARKINFO_SUCCESS = 1;
    private static final int GETPARKINFO_ERROR = 2;

    //TODO 计算两点之间的距离

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        parking_lot = (ImageView) findViewById(R.id.parking_lot_image);
        e_parkingLotName = (EditText) findViewById(R.id.park_info_parking_lot_name_edit_text);
        e_parkingLotAddress = (EditText) findViewById(R.id.park_info_parking_lot_address_edit_text);
        e_destination = (EditText) findViewById(R.id.park_info_destination_edit_text);
        e_totalSpaces = (EditText) findViewById(R.id.park_info_totalSpaces_edit_text);
        e_totalAvailable = (EditText) findViewById(R.id.park_info_totalAvailable_edit_text);
        e_parkPrice = (EditText) findViewById(R.id.park_info_park_price_edit_text);
        e_parkNightPrice = (EditText) findViewById(R.id.park_info_park_night_price_edit_text);
        parking_lot.setImageResource(R.drawable.parking_lot);

        if(MyApp.isIntent()) {
            Bundle bundle = this.getIntent().getExtras();
            parkingLotUid = bundle.getString("parkingLotUid");
            s_parkingLotName = bundle.getString("parkingLotName");
            s_parkingLotAddress = bundle.getString("parkingLotAddress");
            s_parkingLotLatitude = bundle.getString("parkingLotLatitude");
            s_parkingLotLongitude = bundle.getString("parkingLotLongitude");
            s_destination = bundle.getString("destination");

            System.out.println(parkingLotUid + "\n" + s_parkingLotName + "\n" + s_parkingLotAddress + "\n" + s_destination);
            e_parkingLotName.setText(s_parkingLotName);
            e_parkingLotAddress.setText(s_parkingLotAddress);
            e_destination.setText(s_destination);
            e_parkingLotName.setEnabled(false);
            e_parkingLotAddress.setEnabled(false);
            e_destination.setEnabled(false);
            e_totalSpaces.setEnabled(false);
            e_totalAvailable.setEnabled(false);
            e_parkPrice.setEnabled(false);
            e_parkNightPrice.setEnabled(false);
        }

        // 获取用户信息
        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    isSuccess = ParkInfoClient.getByParkUid(MyApp.getIpAddress(), parkingLotUid);
                    if (isSuccess == null) {
                        Message message = new Message();
                        message.what = GETPARKINFO_ERROR;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = GETPARKINFO_SUCCESS;
                        handler.sendMessage(message);
                    }
                }catch(IllegalArgumentException e){
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
                    Toast.makeText(ParkInfoActivity.this, R.string.get_park_info_error, Toast.LENGTH_SHORT).show();
                    break;
                case GETPARKINFO_SUCCESS:
                    String[] strArray = isSuccess.split("!");
                    e_totalSpaces.setText(strArray[0]);
                    e_totalAvailable.setText(strArray[1]);
                    e_parkPrice.setText(strArray[2]);
                    e_parkNightPrice.setText(strArray[3]);
                    Toast.makeText(ParkInfoActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
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