package com.hqxi.waterwaveview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.hqxi.waterwaveview.view.WaterWaveView;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 可以直接在xml中设置
//        init(); // java代码添加演示
    }

    private void init() {
        mLinearLayout = findViewById(R.id.view_group);
        WaterWaveView waterWaveView = new WaterWaveView.Bulider(this)
//                // 设置属性，不设置使用默认值
//                .setWaveIntervalSize(50F)
//                .setWaveStartWidth(30F)
//                .setWaveEndWidth(1F)
//                .setFillWaveSourceShapeRadius(50F)
//                .setWaveColor(Color.YELLOW)
//                .setWaveStirstep(2F)
//                .setFillAllView(true)
                .create();
        addView(waterWaveView);
    }

    private void addView(WaterWaveView waterWaveView) {
//        // 直接添加
//        mLinearLayout.addView(waterWaveView);
        // 设置大小及位置
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(dip2px(this, 300), dip2px(this, 400));
        layoutParams.setMargins(0, 50, 0, 0);
        mLinearLayout.addView(waterWaveView, layoutParams);
    }

    private int dip2px(Context context, float dpValue) {
        // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
        // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);

    }
}
