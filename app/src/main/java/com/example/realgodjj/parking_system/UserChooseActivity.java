package com.example.realgodjj.parking_system;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.realgodjj.parking_system.client.MyApp;

import java.util.Calendar;


public class UserChooseActivity extends AppCompatActivity {

    private TextView userName;
    private EditText e_parkingFreeRate, e_distance, e_parkFee, e_lightNum;
    private EditText e_startTime_hour, e_startTime_minute, e_endTime_hour, e_endTime_minute, e_total_time;
    private Button bestEstimate;

    private String s_parkingFreeRate, s_distance, s_parkFee, s_lightNum;
    private double totalHour = 0, totalMinute = 0;
    private double dayTime, nightTime;//以小时记录的日间时间和夜间时间
    private int currentHour, currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userName = (TextView) findViewById(R.id.user_choose_username_text_view);
        e_parkingFreeRate = (EditText) findViewById(R.id.user_choose_parking_free_rate_edit_text);
        e_distance = (EditText) findViewById(R.id.user_choose_distance_edit_text);
        e_parkFee = (EditText) findViewById(R.id.user_choose_parkFee_edit_text);
        e_lightNum = (EditText) findViewById(R.id.user_choose_lightNum_edit_text);
        e_startTime_hour = (EditText) findViewById(R.id.user_choose_start_time_hour_edit_text);
        e_startTime_minute = (EditText) findViewById(R.id.user_choose_start_time_minute_edit_text);
        e_endTime_hour = (EditText) findViewById(R.id.user_choose_end_time_hour_edit_text);
        e_endTime_minute = (EditText) findViewById(R.id.user_choose_end_time_minute_edit_text);
        e_total_time = (EditText) findViewById(R.id.user_choose_total_time_edit_text);
        bestEstimate = (Button) findViewById(R.id.user_choose_best_estimate_button);

        userName.setText(MyApp.getUserName());//get userName who is logging

        Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        e_startTime_hour.setText(String.valueOf(currentHour));
        e_startTime_minute.setText(String.valueOf(currentMinute));

        bestEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_parkingFreeRate = e_parkingFreeRate.getText().toString();
                s_distance = e_distance.getText().toString();
                s_parkFee = e_parkFee.getText().toString();
                s_lightNum = e_lightNum.getText().toString();
                if (TextUtils.isEmpty(s_parkingFreeRate) || TextUtils.isEmpty(s_distance) || TextUtils.isEmpty(s_parkFee)
                        || TextUtils.isEmpty(s_lightNum) || TextUtils.isEmpty(e_startTime_hour.getText().toString())
                        || TextUtils.isEmpty(e_startTime_minute.getText().toString()) || TextUtils.isEmpty(e_endTime_hour.getText().toString())
                        || TextUtils.isEmpty(e_endTime_minute.getText().toString())) {
                    Toast.makeText(UserChooseActivity.this, R.string.info_empty, Toast.LENGTH_SHORT).show();
                } else if(Integer.parseInt(s_parkingFreeRate) + Integer.parseInt(s_distance) + Integer.parseInt(s_parkFee) + Integer.parseInt(s_lightNum) != 10) {
                    Toast.makeText(UserChooseActivity.this, R.string.input_number_error, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(UserChooseActivity.this, BestChoiceActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("parkingFreeRate_rate", s_parkingFreeRate);
                    bundle.putString("distance_rate", s_distance);
                    bundle.putString("parkFee_rate", s_parkFee);
                    bundle.putString("lightNum_rate", s_lightNum);
                    bundle.putDouble("nightTime", nightTime);
                    bundle.putDouble("dayTime", dayTime);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        e_parkingFreeRate.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 1;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_parkingFreeRate.getSelectionStart();
                editEnd = e_parkingFreeRate.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
            }
        });

        e_distance.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 1;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_distance.getSelectionStart();
                editEnd = e_distance.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
            }
        });

        e_parkFee.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 1;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_parkFee.getSelectionStart();
                editEnd = e_parkFee.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
            }
        });

        e_lightNum.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 1;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_lightNum.getSelectionStart();
                editEnd = e_lightNum.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
            }
        });

        //开始时间小时监听事件
        e_startTime_hour.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_startTime_hour.getSelectionStart();
                editEnd = e_startTime_hour.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_hour.setText(temp);
                    e_startTime_hour.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        //开始时间分钟监听事件
        e_startTime_minute.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_startTime_minute.getSelectionStart();
                editEnd = e_startTime_minute.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_startTime_minute.setText(temp);
                    e_startTime_minute.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        //结束时间小时监听事件
        e_endTime_hour.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_endTime_hour.getSelectionStart();
                editEnd = e_endTime_hour.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_endTime_hour.setText(temp);
                    e_endTime_hour.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });

        //结束时间分钟监听事件
        e_endTime_minute.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本
            private int editStart;//光标开始位置
            private int editEnd;//光标结束位置
            private final int charMaxNum = 2;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = e_endTime_minute.getSelectionStart();
                editEnd = e_endTime_minute.getSelectionEnd();
                if (temp.length() > charMaxNum) {
                    Toast.makeText(getApplicationContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_LONG).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    e_endTime_minute.setText(temp);
                    e_endTime_minute.setSelection(tempSelection);
                }
                setTotalTime(e_startTime_hour.getText().toString(), e_startTime_minute.getText().toString(), e_endTime_hour.getText().toString(), e_endTime_minute.getText().toString());
            }
        });
    }

    private void setTotalTime(String startH, String startM, String endH, String endM) {
        if (!("".equals(startH) || "".equals(startM) || "".equals(endH) || "".equals(endM))) {
            if (Integer.parseInt(startH) >= 24) {
                e_total_time.setText(R.string.start_time_hour_error);
                return;
            }

            if (Integer.parseInt(startM) >= 60) {
                e_total_time.setText(R.string.start_time_minute_error);
                return;
            }

            if (Integer.parseInt(endH) >= 24) {
                e_total_time.setText(R.string.end_time_hour_error);
                return;
            }

            if (Integer.parseInt(endM) >= 60) {
                e_total_time.setText(R.string.end_time_minute_error);
                return;
            }

            if (Integer.parseInt(endM) >= Integer.parseInt(startM)) {
                totalMinute = Double.parseDouble(endM) - Double.parseDouble(startM);
                totalHour = Double.parseDouble(endH) - Double.parseDouble(startH);
            } else {
                totalMinute = Double.parseDouble(endM) + 60 - Double.parseDouble(startM);
                totalHour = Double.parseDouble(endH) - 1 - Double.parseDouble(startH);
            }
            if(totalHour < 0) {
                //以小时记录的日间时间和夜间时间
                if(Integer.parseInt(startH) >= 20 && Integer.parseInt(endH) >= 7) {
                    nightTime = 24.0 - Double.parseDouble(startH) - 1.0 + (60.0 - Double.parseDouble(startM)) / 60.0 + 7.0;
                    dayTime = totalHour + totalMinute / 60 + 24.0 - nightTime;
                } else if (Integer.parseInt(startH) >= 20 && Integer.parseInt(endH) < 7) {
                    nightTime = 24.0 - Double.parseDouble(startH) - 1.0 + (60.0 - Double.parseDouble(startM)) / 60.0 + Double.parseDouble(endH) + Double.parseDouble(endM) / 60;
                    dayTime = 0;
                } else if (Integer.parseInt(startH) < 20 && Integer.parseInt(endH) >= 7) {
                    nightTime = 4.0 +  7.0;
                    dayTime = totalHour + totalMinute / 60.0 + 24.0 - nightTime;
                } else {
                    nightTime = 4.0 + Double.parseDouble(endH) + Double.parseDouble(endM) / 60;
                    dayTime = totalHour + totalMinute / 60.0 + 24.0 - nightTime;
                }
                //显示停车总时间
                totalHour = totalHour + 24;
                if (totalHour == 0) {
                    e_total_time.setText(String.valueOf(totalMinute) + "分钟(隔夜)");
                } else if (totalMinute == 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时(隔夜)");
                } else if (totalHour != 0 && totalMinute != 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时" + String.valueOf(totalMinute) + "分钟(隔夜)");
                }
            } else {
                //以小时记录的日间时间和夜间时间
                if(Integer.parseInt(startH) < 7 && Integer.parseInt(endH) < 20) {
                    dayTime = Double.parseDouble(endH) - 7.0 + Double.parseDouble(endM) / 60;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                } else if (Integer.parseInt(startH) < 7 && Integer.parseInt(endH) >= 20) {
                    dayTime = 13.0;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                } else if (Integer.parseInt(startH) >= 7 && Integer.parseInt(endH) < 20) {
                    if (Integer.parseInt(endM) >= Integer.parseInt(startM)) {
                        totalMinute = Double.parseDouble(endM) - Double.parseDouble(startM);
                        totalHour = Double.parseDouble(endH) - Double.parseDouble(startH);
                    } else {
                        totalMinute = Double.parseDouble(endM) + 60 - Double.parseDouble(startM);
                        totalHour = Double.parseDouble(endH) - 1 - Double.parseDouble(startH);
                    }
                    dayTime = totalHour + totalMinute / 60;
                    nightTime = 0;
                } else {
                    dayTime = 20.0 - Double.parseDouble(startH) - 1.0 + (60 - Double.parseDouble(startM)) / 60;
                    nightTime = totalHour + totalMinute / 60 - dayTime;
                }
                //显示停车总时间
                if (totalHour == 0) {
                    e_total_time.setText(String.valueOf(totalMinute) + "分钟");
                } else if (totalMinute == 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时");
                } else if (totalHour != 0 && totalMinute != 0) {
                    e_total_time.setText(String.valueOf(totalHour) + "小时" + String.valueOf(totalMinute) + "分钟");
                }
            }
        } else {
            e_total_time.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
