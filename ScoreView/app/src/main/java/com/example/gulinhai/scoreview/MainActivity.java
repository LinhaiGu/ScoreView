package com.example.gulinhai.scoreview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private RingRotateView rrv;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        rrv = (RingRotateView) findViewById(R.id.rrv);
    }

    private void initEvent() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rrv.setScore(90, 100);
                rrv.startAnimation(new DecelerateInterpolator());
            }
        });
        rrv.setOnScoreListener(new RingRotateView.OnScoreListener() {
            @Override
            public void finish() {
                Toast.makeText(MainActivity.this, "评测完毕", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
