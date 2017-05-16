package com.example.realgodjj.parking_system;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.ParkInfoClient;
import com.example.realgodjj.parking_system.client.UpdateParkClient;
import com.example.realgodjj.parking_system.simulation.Park;

public class AdminParkInfoActivity extends AppCompatActivity {

    private EditText e_parkingLotName, e_parkingLotAddress, e_destination, e_totalSpaces, e_totalAvailable, e_parkPrice, e_parkNightPrice;
    private Button changeParkInfo, saveParkInfo;
    private String parkingLotUid, s_parkingLotName, s_parkingLotAddress, s_destination, s_totalSpaces, s_totalAvailable, s_parkPrice, s_parkNightPrice;
    private String isSuccess = "";
    private static final int GETPARKINFO_SUCCESS = 1;
    private static final int GETPARKINFO_ERROR = 2;
    private static final int UPDATEPARKINFO_SUCCESS = 3;
    private static final int UPDATEPARKINFO_ERROR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_park_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        e_parkingLotName = (EditText) findViewById(R.id.admin_park_info_parking_lot_name_edit_text);
        e_parkingLotAddress = (EditText) findViewById(R.id.admin_park_info_parking_lot_address_edit_text);
        e_destination = (EditText) findViewById(R.id.admin_park_info_destination_edit_text);
        e_totalSpaces = (EditText) findViewById(R.id.admin_park_info_totalSpaces_edit_text);
        e_totalAvailable = (EditText) findViewById(R.id.admin_park_info_totalAvailable_edit_text);
        e_parkPrice = (EditText) findViewById(R.id.admin_park_info_park_price_edit_text);
        e_parkNightPrice = (EditText) findViewById(R.id.admin_park_info_park_night_price_edit_text);
        changeParkInfo = (Button) findViewById(R.id.admin_park_info_change_park_info_button);
        saveParkInfo = (Button) findViewById(R.id.admin_park_info_save_park_info_button);

        if(MyApp.isIntent()) {
            Bundle bundle = this.getIntent().getExtras();
            parkingLotUid = bundle.getString("parkingLotUid");
            s_parkingLotName = bundle.getString("parkingLotName");
            s_parkingLotAddress = bundle.getString("parkingLotAddress");
            s_destination = bundle.getString("destination");

//            System.out.println(parkingLotUid + "\n" + s_parkingLotName + "\n" + s_parkingLotAddress + "\n" + s_destination);
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

        changeParkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_totalSpaces.setEnabled(true);
                e_totalAvailable.setEnabled(true);
                e_parkPrice.setEnabled(true);
                e_parkNightPrice.setEnabled(true);
                s_totalSpaces = e_totalSpaces.getText().toString();
                s_totalAvailable = e_totalAvailable.getText().toString();
                s_parkPrice = e_parkPrice.getText().toString();
                s_parkNightPrice = e_parkNightPrice.getText().toString();
            }
        });

        saveParkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread update_post_thread;
                update_post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Park park = new Park();
                            park.setParkUid(parkingLotUid);
                            park.setTotalSpaces(Integer.parseInt(e_totalSpaces.getText().toString()));
                            park.setTotalAvailable(Integer.parseInt(e_totalAvailable.getText().toString()));
                            park.setParkPrice(Double.parseDouble(e_parkPrice.getText().toString()));
                            park.setParkNightPrice(Double.parseDouble(e_parkNightPrice.getText().toString()));
                            //修改数据库用户信息
                            isSuccess = UpdateParkClient.updatePark(MyApp.getIpAddress(), park);
                            if (isSuccess == null) {
                                Message message = new Message();
                                message.what = UPDATEPARKINFO_ERROR;
                                handler.sendMessage(message);
                            } else {
                                Message message = new Message();
                                message.what = UPDATEPARKINFO_SUCCESS;
                                handler.sendMessage(message);
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                update_post_thread.start();
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GETPARKINFO_ERROR:
                    Toast.makeText(AdminParkInfoActivity.this, R.string.get_park_info_error, Toast.LENGTH_SHORT).show();
                    break;

                case GETPARKINFO_SUCCESS:
                    String[] strArray = isSuccess.split("!");
                    e_totalSpaces.setText(strArray[0]);
                    e_totalAvailable.setText(strArray[1]);
                    e_parkPrice.setText(strArray[2]);
                    e_parkNightPrice.setText(strArray[3]);
                    Toast.makeText(AdminParkInfoActivity.this, R.string.get_park_info_success, Toast.LENGTH_SHORT).show();
                    break;

                case UPDATEPARKINFO_ERROR:
                    Toast.makeText(AdminParkInfoActivity.this, R.string.update_park_info_error, Toast.LENGTH_SHORT).show();
                    break;

                case UPDATEPARKINFO_SUCCESS:
                    e_totalSpaces.setEnabled(false);
                    e_totalAvailable.setEnabled(false);
                    e_parkPrice.setEnabled(false);
                    e_parkNightPrice.setEnabled(false);
                    Toast.makeText(AdminParkInfoActivity.this, R.string.update_park_info_success, Toast.LENGTH_SHORT).show();
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
