package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;
import com.example.realgodjj.parking_system.client.LoginClient;
import com.example.realgodjj.parking_system.client.MyApp;
import com.example.realgodjj.parking_system.simulation.User;


public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_SUCCESS = 1;//login success
    private static final int LOGIN_ERROR = 2;//login fail
    private ImageView intelligent;
    private EditText e_userName, e_pwd;
    private TextView changePassword;
    private CheckBox showPassword, rememberPassword;
    private Button login, register;
    private String isSuccess = "";
    private String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        e_userName = (EditText) findViewById(R.id.account_edit_text);
        e_pwd = (EditText) findViewById(R.id.password_edit_text);
        changePassword = (TextView) findViewById(R.id.change_password_text_view);
        login = (Button) findViewById(R.id.login_button);
        register = (Button) findViewById(R.id.register_button);
        showPassword = (CheckBox) findViewById(R.id.show_password_check_box);
        rememberPassword = (CheckBox) findViewById(R.id.remember_password_check_box);
        intelligent = (ImageView) findViewById(R.id.intelligent_image);
        intelligent.setImageResource(R.drawable.intelligent);
        e_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

//        Bundle bundle = this.getIntent().getExtras();
//        String registerUserName = bundle.getString("key_userName");
//        String registerPassword = bundle.getString("key_password");
        //sharedPreferences = getSharedPreferences(MainActivity.SP_FILE_NAME, Context.MODE_PRIVATE);
        //userDao = new UserDaoImpl();

        /*
        login_sp = getSharedPreferences("userInfo", 0);
        String name = login_sp.getString("USER_NAME", "");
        String pwd = login_sp.getString("PASSWORD", "");
        boolean choseRemember = login_sp.getBoolean("mRmemberCheck", false);
        */

        /**
         * 记住密码
         */
        /*
        if (choseRemember) {
            e_userName.setText(name);
            e_pwd.setText(pwd);
            rememberPassword.setChecked(true);
        }*/

        /**
         * 显示密码
         */
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    e_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    e_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        //修改密码
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
                finish();
            }
        });

        /**
         * 密码字符内容的限制
         */
        e_pwd.setKeyListener(new DigitsKeyListener() {
            //存放类型
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            //接受字符串的类型
            @Override
            protected char[] getAcceptedChars() {
                char[] data = getStringData(R.string.login_only_can_input).toCharArray();
                return data;
            }
        });

        /**
         * 登录按钮事件
         */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = e_userName.getText().toString().trim();
                password = e_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Thread post_thread;
                    post_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                User user = new User();
                                user.setUserName(userName);
                                user.setPassword(password);
                                isSuccess = LoginClient.login(MyApp.getIpAddress(), user);
                                if (isSuccess.equals("Login success!")) {
                                    Message message = new Message();
                                    message.what = LOGIN_SUCCESS;
                                    handler.sendMessage(message);
                                    MyApp.setLogin(true);
                                    MyApp.setUserName(userName);
                                    finish();
                                } else {
                                    Message message = new Message();
                                    message.what = LOGIN_ERROR;
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

        /**
         * 注册事件
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyApp.isRegister() && MyApp.isReceive()) {
            Bundle bundle = this.getIntent().getExtras();
            String registerUserName = bundle.getString("key_userName");
            String registerPassword = bundle.getString("key_password");
            e_userName.setText(registerUserName);
            e_pwd.setText(registerPassword);
            MyApp.setRegister(false);
        } else if(MyApp.isResetPassword()) {
            Bundle bundle = this.getIntent().getExtras();
            String userName = bundle.getString("userName");
            String new_password = bundle.getString("new_password");
            e_userName.setText(userName);
            e_pwd.setText(new_password);
            MyApp.setResetPassword(false);
        }
    }

    /**
     * 得到字符串数组
     *
     * @param id
     * @return
     */
    public String getStringData(int id) {
        return getResources().getString(id);
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case LOGIN_ERROR:
                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, R.string.login_right, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
