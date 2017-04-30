package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.RegisterClient;
import com.example.realgodjj.parking_system.client.UserInfoClient;
import com.example.realgodjj.parking_system.simulation.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText e_userName, e_pwd, e_pwd_again, e_phoneNumber, e_email, e_plateNo;
    private Button finish;
    private String s_userName, s_pwd, s_pwd_again, s_phoneNumber, s_email, s_plateNo;
    private String isSuccess = "";
    private String server_userName, server_phoneNumber, server_email, server_plateNo;
    private static final int REGISTER_SUCCESS = 1;//register success
    private static final int REGISTER_ERROR = 2;//register fail
    private static final int REGISTER_INFO_SPACE = 3;
    private static final int USERNAME_CHECK = 4;
    private static final int USERNAME_REPEAT = 5;
    private static final int PHONE_NUMBER_CHECK = 6;
    private static final int PHONE_NUMBER_REPEAT = 7;
    private static final int PWD_CHECK = 8;
    private static final int PWD_SAME = 9;
    private static final int EMAIL_CHECK = 10;
    private static final int EMAIL_REPEAT = 11;
    private static final int PLATENO_CHECK = 12;
    private static final int PLATENO_REPEAT = 13;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        e_userName = (EditText) findViewById(R.id.register_account_edit_text);
        e_phoneNumber = (EditText) findViewById(R.id.register_phone_number_edit_text);
        e_pwd = (EditText) findViewById(R.id.register_password_edit_text);
        e_pwd_again = (EditText) findViewById(R.id.register_password_again_edit_text);
        e_email = (EditText) findViewById(R.id.register_email_edit_text);
        e_plateNo = (EditText) findViewById(R.id.register_plateNo_edit_text);
        finish = (Button) findViewById(R.id.register_finish_button);
        e_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        e_pwd_again.setTransformationMethod(PasswordTransformationMethod.getInstance());

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_userName = e_userName.getText().toString().trim();
                s_pwd = e_pwd.getText().toString().trim();
                s_pwd_again = e_pwd_again.getText().toString().trim();
                s_phoneNumber = e_phoneNumber.getText().toString().trim();
                s_email = e_email.getText().toString().trim();
                s_plateNo = e_plateNo.getText().toString().trim();

                Thread server_post_thread;
                server_post_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            isSuccess = UserInfoClient.getByUserName(MyApp.getIpAddress(), s_userName);
                            String[] strArray = isSuccess.split("#");
                            server_userName = strArray[0];
                            isSuccess = UserInfoClient.getByPhoneNumber(MyApp.getIpAddress(), s_phoneNumber);
                            server_phoneNumber = isSuccess;
                            isSuccess = UserInfoClient.getByEmail(MyApp.getIpAddress(), s_email);
                            server_email = isSuccess;
                            isSuccess = UserInfoClient.getByPlateNo(MyApp.getIpAddress(), s_plateNo);
                            server_plateNo = isSuccess;
                            System.out.println("get from server :\n" + server_userName + "\n" + server_phoneNumber + "\n" + server_email + "\n" + server_plateNo);

                            Thread handle_post_thread;
                            handle_post_thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        System.out.println("get from server :\n" + server_userName + "\n" + server_phoneNumber + "\n" + server_email + "\n" + server_plateNo);
                                        // 编译正则表达式
                                        String email_check = "/^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$/";
                                        String plateNo_check = "^[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$";
                                        Pattern pattern_email = Pattern.compile(email_check);
                                        Pattern pattern_plateNo = Pattern.compile(plateNo_check);
                                        Matcher matcher_email = pattern_email.matcher(s_email);
                                        Matcher matcher_plateNo = pattern_plateNo.matcher(s_plateNo);
                                        if (TextUtils.isEmpty(s_userName) || TextUtils.isEmpty(s_pwd) || TextUtils.isEmpty(s_pwd_again)
                                                || TextUtils.isEmpty(s_phoneNumber) || TextUtils.isEmpty(s_plateNo) || TextUtils.isEmpty(s_email)) {
                                            Message message = new Message();
                                            message.what = REGISTER_INFO_SPACE;
                                            handler.sendMessage(message);
                                            //return;
                                        } else if (s_userName.length() > 20) {
                                            Message message = new Message();
                                            message.what = USERNAME_CHECK;
                                            handler.sendMessage(message);
                                        } else if (!server_userName.equals("error")) {
                                            Message message = new Message();
                                            message.what = USERNAME_REPEAT;
                                            handler.sendMessage(message);
                                        } else if (s_phoneNumber.length() != 11) {
                                            Message message = new Message();
                                            message.what = PHONE_NUMBER_CHECK;
                                            handler.sendMessage(message);
                                        } else if (!server_phoneNumber.equals("error")) {
                                            Message message = new Message();
                                            message.what = PHONE_NUMBER_REPEAT;
                                            handler.sendMessage(message);
                                        } else if (s_pwd.length() < 6) {
                                            Message message = new Message();
                                            message.what = PWD_CHECK;
                                            handler.sendMessage(message);
                                        } else if (!s_pwd.equals(s_pwd_again)) {
                                            Message message = new Message();
                                            message.what = PWD_SAME;
                                            handler.sendMessage(message);
                                        } else if (matcher_email.matches()) {
                                            Message message = new Message();
                                            message.what = EMAIL_CHECK;
                                            handler.sendMessage(message);
                                        } else if (!server_email.equals("error")) {
                                            Message message = new Message();
                                            message.what = EMAIL_REPEAT;
                                            handler.sendMessage(message);
                                        } else if (!matcher_plateNo.matches()) {
                                            //TODO : 车牌号中文无法插入
                                            Message message = new Message();
                                            message.what = PLATENO_CHECK;
                                            handler.sendMessage(message);
                                        } else if (!server_plateNo.equals("error")) {
                                            Message message = new Message();
                                            message.what = PLATENO_REPEAT;
                                            handler.sendMessage(message);
                                        } else {
                                            Thread post_thread;
                                            post_thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        User user = new User();
                                                        user.setUserName(s_userName);
                                                        user.setPassword(s_pwd);
                                                        user.setPhoneNumber(s_phoneNumber);
                                                        user.setEmail(s_email);
                                                        user.setPlateNo(s_plateNo.substring(1));
                                                        isSuccess = RegisterClient.Register(MyApp.getIpAddress(), user);
                                                        if (isSuccess.equals("Register success!")) {
                                                            Message message = new Message();
                                                            message.what = REGISTER_SUCCESS;
                                                            handler.sendMessage(message);
                                                            MyApp.setRegister(true);
                                                            MyApp.setReceive(true);
                                                            //Send userName and password to the LoginActivity
                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("key_userName", s_userName);
                                                            bundle.putString("key_password", s_pwd);
                                                            intent.putExtras(bundle);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Message message = new Message();
                                                            message.what = REGISTER_ERROR;
                                                            handler.sendMessage(message);
                                                        }
                                                    } catch (IllegalArgumentException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            post_thread.start();
                                        }
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            handle_post_thread.start();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                server_post_thread.start();
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case REGISTER_ERROR:
                    Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_SUCCESS:
                    Toast.makeText(RegisterActivity.this, R.string.register_right, Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_INFO_SPACE:
                    Toast.makeText(RegisterActivity.this, R.string.space, Toast.LENGTH_SHORT).show();
                    break;
                case USERNAME_CHECK:
                    Toast.makeText(RegisterActivity.this, R.string.account_check, Toast.LENGTH_SHORT).show();
                    break;
                case USERNAME_REPEAT:
                    Toast.makeText(RegisterActivity.this, R.string.account_repeat, Toast.LENGTH_SHORT).show();
                    break;
                case PHONE_NUMBER_CHECK:
                    Toast.makeText(RegisterActivity.this, R.string.phone_number_check, Toast.LENGTH_SHORT).show();
                    break;
                case PHONE_NUMBER_REPEAT:
                    Toast.makeText(RegisterActivity.this, R.string.phone_number_repeat, Toast.LENGTH_SHORT).show();
                    break;
                case PWD_CHECK:
                    Toast.makeText(RegisterActivity.this, R.string.pwd_check, Toast.LENGTH_SHORT).show();
                    break;
                case PWD_SAME:
                    Toast.makeText(RegisterActivity.this, R.string.pwd_not_the_same, Toast.LENGTH_SHORT).show();
                    break;
                case EMAIL_CHECK:
                    Toast.makeText(RegisterActivity.this, R.string.email_check, Toast.LENGTH_SHORT).show();
                    break;
                case EMAIL_REPEAT:
                    Toast.makeText(RegisterActivity.this, R.string.email_repeat, Toast.LENGTH_SHORT).show();
                    break;
                case PLATENO_CHECK :
                    Toast.makeText(RegisterActivity.this, R.string.plateNo_check, Toast.LENGTH_SHORT).show();
                    break;
                case PLATENO_REPEAT:
                    Toast.makeText(RegisterActivity.this, R.string.plateNo_repeat, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    //点击返回事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //对返回键进行监听
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}
