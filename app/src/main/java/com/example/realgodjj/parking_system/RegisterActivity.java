package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.RegisterClient;
import com.example.realgodjj.parking_system.simulation.User;


public class RegisterActivity extends AppCompatActivity {

    private EditText e_account, e_pwd, e_pwd_again, e_phoneNumber, e_email, e_plateNo;
    private Button finish;
    private String s_account, s_pwd, s_pwd_again, s_phoneNumber, s_email, s_plateNo;
    private String isSuccess = "";
    private static final int REGISTER_SUCCESS = 1;//register success
    private static final int REGISTER_ERROR = 2;//register fail

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        e_account = (EditText) findViewById(R.id.register_account_edit_text);
        e_phoneNumber = (EditText) findViewById(R.id.register_phone_number_edit_text);
        e_pwd = (EditText) findViewById(R.id.register_password_edit_text);
        e_pwd_again = (EditText) findViewById(R.id.register_password_again_edit_text);
        e_email = (EditText) findViewById(R.id.register_email_edit_text);
        e_plateNo = (EditText) findViewById(R.id.register_account_edit_text);
        finish = (Button) findViewById(R.id.register_finish_button);
        e_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        e_pwd_again.setTransformationMethod(PasswordTransformationMethod.getInstance());

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_account = e_account.getText().toString().trim();
                s_pwd = e_pwd.getText().toString().trim();
                s_pwd_again = e_pwd_again.getText().toString().trim();
                s_phoneNumber = e_phoneNumber.getText().toString().trim();
                s_email = e_email.getText().toString().trim();
                s_plateNo = e_plateNo.getText().toString().trim();

                if (TextUtils.isEmpty(s_account) || TextUtils.isEmpty(s_pwd) || TextUtils.isEmpty(s_pwd_again)
                        || TextUtils.isEmpty(s_phoneNumber) || TextUtils.isEmpty(s_plateNo) || TextUtils.isEmpty(s_email)) {
                    Toast.makeText(RegisterActivity.this, R.string.space, Toast.LENGTH_SHORT).show();
                    //return;
                } else if (!s_pwd.equals(s_pwd_again)) {
                    Toast.makeText(RegisterActivity.this, R.string.pwd_not_the_same, Toast.LENGTH_SHORT).show();
                    //return;
                } else {
//                    UserData userData = new UserData();
//                    userData.setUserName(s_account);
//                    userData.setPassword(s_pwd);
//                    userData.setPhoneNumber(s_phone_number);
//                    userData.setEmail(s_email);
//                    userData.setPlateNo(s_plateNo);
//
//                    new UserDaoImpl().activity_register(userData);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean(SP_KEY_REGISTER, true);
//                    editor.commit();
                    Thread post_thread;
                    post_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                User user = new User();
                                user.setUserName(s_account);
                                user.setPassword(s_pwd);
                                user.setPhoneNumber(s_phoneNumber);
                                user.setEmail(s_email);
                                user.setPlateNo(s_plateNo);
                                isSuccess = RegisterClient.Register(MyApp.getIpAddress(), user);
                                if (isSuccess.equals("Register success!")) {
                                    Message message = new Message();
                                    message.what = REGISTER_SUCCESS;
                                    handler.sendMessage(message);
                                    MyApp.setRegister(true);
                                    MyApp.setReceive(true);
                                    //Send userName and password to the LoginActivity
                                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key_userName", s_account);
                                    bundle.putString("key_password", s_pwd);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Message message = new Message();
                                    message.what = REGISTER_ERROR;
                                    handler.sendMessage(message);
                                }
                            }catch(IllegalArgumentException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    post_thread.start();
                }
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case REGISTER_ERROR:
                    Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_SUCCESS:
                    Toast.makeText(RegisterActivity.this, R.string.register_right, Toast.LENGTH_SHORT).show();
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
