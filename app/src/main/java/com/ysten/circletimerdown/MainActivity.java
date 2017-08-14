package com.ysten.circletimerdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ysten.circletimerdown.view.CircleTimerView;

public class MainActivity extends AppCompatActivity implements CircleTimerView.OnCountDownFinish{
        private CircleTimerView mCircleTimerView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(); 

    }

    private void initView() {


        mCircleTimerView = (CircleTimerView) findViewById(R.id.circle_timer);
        mCircleTimerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCircleTimerView.cancelAnim();
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
                finish();
            }
        });
        mCircleTimerView.setOnCountDownFinish(this);
        mCircleTimerView.start();
    }

    @Override
    public void onFinish() {
        startActivity(new Intent(MainActivity.this,SecondActivity.class));
        finish();
    }
}
