package com.example.realgodjj.parking_system;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;

public class PayingActivity extends AppCompatActivity {

    private EditText e_parkFee;
    private Button finishPaying;
    private String parkFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);
        e_parkFee = (EditText) findViewById(R.id.paying_parkFee_edit_text);
        finishPaying = (Button) findViewById(R.id.paying_finish_paying_button);
        Bundle bundle = this.getIntent().getExtras();
        parkFee = bundle.getString("parkFee");
        BigDecimal b = new BigDecimal(parkFee);
        double fee = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        e_parkFee.setText(fee + "元");
        e_parkFee.setEnabled(false);
        finishPaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayingActivity.this, R.string.finish_paying, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
        }
        return super.dispatchKeyEvent(event);
    }
}
