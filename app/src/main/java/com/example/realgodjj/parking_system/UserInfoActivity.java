package com.example.realgodjj.parking_system;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.UpdateUserClient;
import com.example.realgodjj.parking_system.client.UserInfoClient;
import com.example.realgodjj.parking_system.simulation.User;

public class UserInfoActivity extends AppCompatActivity {

    private ImageView head_picture;
    private EditText e_userName, e_phoneNumber, e_email, e_plateNo;
    private Button changeInfo, saveInfo;
    private String isSuccess1 = "", isSuccess2 = "";
    private String userName, userId;
    private static final int GETUSERINFO_SUCCESS = 1;
    private static final int GETUSERINFO_ERROR = 2;
    private static final int UPDATEUSERINFO_SUCCESS = 3;
    private static final int UPDATEUSERINFO_ERROR = 4;
    private static final int UPDATEUSERINFO_NULL = 5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        head_picture = (ImageView) findViewById(R.id.head_image);
        e_userName = (EditText) findViewById(R.id.user_info_account_edit_text);
        e_phoneNumber = (EditText) findViewById(R.id.user_info_phone_number_edit_text);
        e_email = (EditText) findViewById(R.id.user_info_email_edit_text);
        e_plateNo = (EditText) findViewById(R.id.user_info_plateNo_edit_text);
        changeInfo = (Button) findViewById(R.id.user_info_change_user_info_button);
        saveInfo = (Button) findViewById(R.id.user_info_save_user_info_button);
        head_picture.setImageResource(R.drawable.head);
        e_userName.setEnabled(false);
        e_phoneNumber.setEnabled(false);
        e_email.setEnabled(false);
        e_plateNo.setEnabled(false);

        //获取用户信息
        Thread post_thread;
        post_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    userName = MyApp.getUserName();//get userName who is logging
                    e_userName.setText(userName);
                    User user = new User();
                    user.setUserName(userName);
                    isSuccess1 = UserInfoClient.getByUserName(MyApp.getIpAddress(), userName);
                    if (isSuccess1 == null) {
                        Message message = new Message();
                        message.what = GETUSERINFO_ERROR;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = GETUSERINFO_SUCCESS;
                        handler.sendMessage(message);
                    }
                }catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        });
        post_thread.start();

        //修改用户信息
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e_userName.setEnabled(true);
                e_phoneNumber.setEnabled(true);
                e_email.setEnabled(true);
                e_plateNo.setEnabled(true);
            }
        });

        //保存用户信息
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread update_post_thread;
                update_post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(TextUtils.isEmpty(e_userName.getText().toString()) ||TextUtils.isEmpty(e_phoneNumber.getText().toString()) ||
                                    TextUtils.isEmpty(e_email.getText().toString()) || TextUtils.isEmpty(e_plateNo.getText().toString())) {
                                Message message = new Message();
                                message.what = UPDATEUSERINFO_NULL;
                                handler.sendMessage(message);
                            } else {
                                User user = new User();
                                user.setUserId(Integer.parseInt(userId));
                                user.setUserName(e_userName.getText().toString());
                                user.setPhoneNumber(e_phoneNumber.getText().toString());
                                user.setEmail(e_email.getText().toString());
                                user.setPlateNo(e_plateNo.getText().toString());
                                //修改数据库用户信息
                                isSuccess2 = UpdateUserClient.updateUser(MyApp.getIpAddress(), user);
                                if(isSuccess2 == null) {
                                    Message message = new Message();
                                    message.what = UPDATEUSERINFO_ERROR;
                                    handler.sendMessage(message);
                                } else {
                                    Message message = new Message();
                                    message.what = UPDATEUSERINFO_SUCCESS;
                                    handler.sendMessage(message);
                                }
                            }
                        }catch(IllegalArgumentException e){
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
                case GETUSERINFO_ERROR:
                    Toast.makeText(UserInfoActivity.this, R.string.get_info_error, Toast.LENGTH_SHORT).show();
                    break;

                case GETUSERINFO_SUCCESS:
                    String[] strArray = isSuccess1.split("#");
                    e_phoneNumber.setText(strArray[1]);
                    e_email.setText(strArray[2]);
                    e_plateNo.setText(strArray[3]);
                    userId = strArray[4];
                    System.out.println("get from server : " + userId);
                    Toast.makeText(UserInfoActivity.this, R.string.get_info_success, Toast.LENGTH_SHORT).show();
                    break;
                case UPDATEUSERINFO_NULL:
                    Toast.makeText(UserInfoActivity.this, R.string.update_user_info_null, Toast.LENGTH_SHORT).show();
                    break;

                case UPDATEUSERINFO_ERROR:
                    Toast.makeText(UserInfoActivity.this, R.string.update_user_info_error, Toast.LENGTH_SHORT).show();
                    break;

                case UPDATEUSERINFO_SUCCESS:
                    e_userName.setEnabled(false);
                    e_phoneNumber.setEnabled(false);
                    e_email.setEnabled(false);
                    e_plateNo.setEnabled(false);
                    MyApp.setUserName(e_userName.getText().toString());
                    Toast.makeText(UserInfoActivity.this, R.string.update_user_info_success, Toast.LENGTH_SHORT).show();
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
