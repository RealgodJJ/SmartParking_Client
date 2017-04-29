package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.ChangePasswordClient;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.client.UserInfoClient;
import com.example.realgodjj.parking_system.simulation.User;


public class ChangePasswordActivity extends AppCompatActivity {

    private EditText e_userName, e_old_password, e_new_password, e_new_password_again;
    private Button changePassword;
    private String userName, old_password, new_password, new_password_again;
    private String isSuccess1 = "", isSuccess2 = "";
    private static final int GETUSERINFO_ERROR = 1;
    private static final int GETUSERINFO_SUCCESS = 2;
    private static final int CHANGEPASSWORD_ERROR = 3;
    private static final int CHANGEPASSWORD_SUCCESS = 4;
    private String userId, server_old_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        e_userName = (EditText) findViewById(R.id.change_password_account_edit_text);
        e_old_password = (EditText) findViewById(R.id.change_password_old_pwd_edit_text);
        e_new_password = (EditText) findViewById(R.id.change_password_new_pwd_edit_text);
        e_new_password_again = (EditText) findViewById(R.id.change_password_new_pwd_again_edit_text);
        changePassword = (Button) findViewById(R.id.change_password_change_pwd_button);
//        e_old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        e_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        e_new_password_again.setTransformationMethod(PasswordTransformationMethod.getInstance());

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = e_userName.getText().toString().trim();
                old_password = e_old_password.getText().toString().trim();
                new_password = e_new_password.getText().toString().trim();
                new_password_again = e_new_password_again.getText().toString().trim();

                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(old_password) || TextUtils.isEmpty(new_password) || TextUtils.isEmpty(new_password_again)) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.info_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!new_password.equals(new_password_again)) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.pwd_not_the_same, Toast.LENGTH_SHORT).show();
                } else if(old_password.equals(new_password)) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.new_can_not_same_as_old, Toast.LENGTH_SHORT).show();
                } else {
                    Thread post_thread;
                    post_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                User user = new User();
                                user.setUserName(userName);
                                isSuccess1 = UserInfoClient.getByUserName(MyApp.getIpAddress(), userName);
                                String[] strArray = isSuccess1.split("#");
                                userId = strArray[4];
                                server_old_password = strArray[5];
//                                System.out.println("mima mima mima : +++++++++++++++++++++" + server_old_password + "+++++++++++++++ : mima mima mima ");
                                if (userId.equals("error")) {
                                    Message message = new Message();
                                    message.what = GETUSERINFO_ERROR;
                                    handler.sendMessage(message);
                                } else if (!old_password.equals(server_old_password)) {
                                    Message message = new Message();
                                    message.what = CHANGEPASSWORD_ERROR;
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
                }
            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GETUSERINFO_ERROR:
                    Toast.makeText(ChangePasswordActivity.this, R.string.no_exist_user, Toast.LENGTH_SHORT).show();
                    break;

                case GETUSERINFO_SUCCESS:
                    System.out.println("get from server : " + userId + "\nget from server : " + server_old_password);
//                    Toast.makeText(ChangePasswordActivity.this, R.string.get_info_success, Toast.LENGTH_SHORT).show();
                    //修改数据库中的密码
                    Thread post_thread;
                    post_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(old_password.equals(new_password)) {
                                    Toast.makeText(ChangePasswordActivity.this, R.string.new_can_not_same_as_old, Toast.LENGTH_SHORT).show();
                                } else {
                                    User user = new User();
                                    user.setUserId(Integer.parseInt(userId));
                                    user.setPassword(new_password);
                                    isSuccess2 = ChangePasswordClient.changePassword(MyApp.getIpAddress(), user);
                                    Message message1 = new Message();
                                    message1.what = CHANGEPASSWORD_SUCCESS;
                                    handler.sendMessage(message1);
                                }
                            }catch(IllegalArgumentException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    post_thread.start();
                    break;

                case CHANGEPASSWORD_ERROR:
                    Toast.makeText(ChangePasswordActivity.this, R.string.old_password_error, Toast.LENGTH_SHORT).show();
                    break;

                case CHANGEPASSWORD_SUCCESS:
                    MyApp.setResetPassword(true);
                    Toast.makeText(ChangePasswordActivity.this, R.string.change_password_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", userName);
                    bundle.putString("new_password", new_password);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };

}
