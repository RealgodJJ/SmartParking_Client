package com.example.realgodjj.parking_system;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.example.realgodjj.parking_system.client.MyApp;

public class SearchDestinationActivity extends Activity {

    private EditText inputDestination;
    private Button searchDestination;
    private String s_destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_search_destination);
        inputDestination = (EditText) findViewById(R.id.search_et_input);
        searchDestination = (Button) findViewById(R.id.search_btn_back);

        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_destination = inputDestination.getText().toString().trim();
                if(!s_destination.equals("")) {
                    MyApp.setSearchDestination(true);
                    Intent intent = new Intent(SearchDestinationActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("s_destination", s_destination);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            moveTaskToBack(true);
        }
        return super.dispatchKeyEvent(event);
    }
}
