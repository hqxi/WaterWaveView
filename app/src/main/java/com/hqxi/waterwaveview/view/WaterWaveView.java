package com.hqxi.waterwaveview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;

import com.hqxi.waterwaveview.R;
import com.hqxi.waterwaveview.view.entiy.Wave;

import java.util.ArrayList;

public class WaterWaveView extends View {
    private static final String TAG = "WaterWaveView";

    private final static int FPS = 1000 / 40;

    private float mWaveIntervalSize; // 波形之间的间距
    private float mWaveStartWidth; // 波形开始时的宽度，画笔的宽度	px
    private float mWaveEndWidth; // 波形结束时的宽度	px
    private float mWaveStirstep; // 波移动的速度
    private int mWaveColor; // 波形的颜色
    private float mFillWaveSourceShapeRadius; // 中心圆的半径
    private boolean mFillAllView = false;

    private ArrayList<Wave> mWaves;
    private Interpolator mInterpolator; // 颜色和波形宽度的插值器
    private Paint mWavePaint;
    private Paint mWaveCenterShapePaint;

    private float mViewCenterX; // 中心点的x坐标
    private float mViewCenterY; // 中心点的y坐标
    private float mMaxWaveAreaRadius;

    public WaterWaveView(Bulider bulider) {
        super(bulider.context);
        mWaveIntervalSize = bulider.waveIntervalSize;
        mWaveStartWidth = bulider.waveStartWidth;
        mWaveEndWidth = bulider.waveEndWidth;
        mWaveStirstep = bulider.waveStirstep;
        mWaveColor = bulider.waveColor;
        mFillWaveSourceShapeRadius = bulider.fillWaveSourceShapeRadius;
        mFillAllView = bulider.fillAllView;
        init();
    }

    public WaterWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init() {
        mWaves = new ArrayList<>();
        mInterpolator = new CycleInterpolator(0.5f);
        mWavePaint = new Paint();
        mWaveCenterShapePaint = new Paint();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WaterWaveView);
        mWaveIntervalSize = typedArray.getFloat(R.styleable.WaterWaveView_waveIntervalSize, 100F);
        mWaveStartWidth = typedArray.getFloat(R.styleable.WaterWaveView_waveStartWidth, 50F);
        mWaveEndWidth = typedArray.getFloat(R.styleable.WaterWaveView_waveEndWidth, 10F);
        mWaveStirstep = typedArray.getFloat(R.styleable.WaterWaveView_waveStirstep, 4F);
        mWaveColor = typedArray.getColor(R.styleable.WaterWaveView_waveColor, Color.GREEN);
        mFillWaveSourceShapeRadius = typedArray.getFloat(R.styleable.WaterWaveView_fillWaveSourceShapeRadius, 0F);
        mFillAllView = typedArray.getBoolean(R.styleable.WaterWaveView_fillAllView, false);
        typedArray.recycle();
    }

    public void setmInterpolator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        if (resolveSize(0, widthMeasureSpec) == 0) {
            width = getMeasuredWidth();
        } else {
            width = resolveSize(0, widthMeasureSpec);
        }
        if (resolveSize(0, heightMeasureSpec) == 0) {
            height = getMeasuredHeight();
        } else {
            height = resolveSize(0, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewCenterX = (float) (getWidth() / 2);
        mViewCenterY = (float) (getHeight() / 2);
        if (mFillAllView) {
            mMaxWaveAreaRadius = (float) Math.sqrt(mViewCenterX * mViewCenterX + mViewCenterY * mViewCenterY);
        } else {
            mMaxWaveAreaRadius = Math.min(mViewCenterX, mViewCenterY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        stir();
        if (mFillWaveSourceShapeRadius >= 0F) {
            mWaveCenterShapePaint.setColor(mWaveColor);
            mWaveCenterShapePaint.setAntiAlias(true);
            mWaveCenterShapePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mViewCenterX, mViewCenterY, mFillWaveSourceShapeRadius, mWaveCenterShapePaint);
        }
        for (Wave wave : mWaves) {
            mWavePaint.setAntiAlias(true);
            mWavePaint.setStyle(Paint.Style.STROKE);
            mWavePaint.setStrokeWidth(wave.getWidth());
            mWavePaint.setColor(wave.getColor());
            canvas.drawCircle(mViewCenterX, mViewCenterY, wave.getRadius(), mWavePaint);
        }
        postInvalidateDelayed(FPS);
    }

    private void stir() {
        Wave nearestWave = mWaves.isEmpty() ? null : mWaves.get(0);
        if (nearestWave == null || nearestWave.getRadius() >= mWaveIntervalSize) {
            Wave newWave = new Wave(mWaveStartWidth, mWaveColor);
            mWaves.add(0, newWave);
        }

        float waveWidthIncrease = mWaveEndWidth - mWaveStartWidth;
        for (Wave wave : mWaves) {
            float rp = wave.getRadius() / mMaxWaveAreaRadius;
            if (rp > 1F) {
                rp = 1F;
            }
            wave.setWidth(mWaveStartWidth + rp * waveWidthIncrease);
            wave.setRadius(wave.getRadius() + mWaveStirstep);
            float factor = mInterpolator.getInterpolation(rp);
            wave.setColor(mWaveColor & 0x00FFFFFF | ((int) (255 * factor) << 24));
        }

        Wave farthestWave = mWaves.get(mWaves.size() - 1);
        if (farthestWave.getRadius() >= mMaxWaveAreaRadius + farthestWave.getWidth() / 2) {
            mWaves.remove(farthestWave);
        }
    }

    public static class Bulider {
        private Context context;

        private float waveIntervalSize = 100F; // 波形之间的间距
        private float waveStartWidth = 50F; // 波形开始时的宽度，画笔的宽度	px
        private float waveEndWidth = 10F; // 波形结束时的宽度	px
        private float waveStirstep = 4F; // 波移动的速度
        private int waveColor = Color.GREEN; // 波形的颜色
        private float fillWaveSourceShapeRadius = 0; // 中心圆的半径
        private boolean fillAllView = false;

        public Bulider(Context context) {
            this.context = context;
        }

        public Bulider setWaveIntervalSize(float waveIntervalSize) {
            this.waveIntervalSize = waveIntervalSize;
            return this;
        }

        public Bulider setWaveStartWidth(float waveStartWidth) {
            this.waveStartWidth = waveStartWidth;
            return this;
        }

        public Bulider setWaveEndWidth(float waveEndWidth) {
            this.waveEndWidth = waveEndWidth;
            return this;
        }

        public Bulider setWaveStirstep(float waveStirstep) {
            this.waveStirstep = waveStirstep;
            return this;
        }

        public Bulider setWaveColor(int waveColor) {
            this.waveColor = waveColor;
            return this;
        }

        public Bulider setFillWaveSourceShapeRadius(float fillWaveSourceShapeRadius) {
            this.fillWaveSourceShapeRadius = fillWaveSourceShapeRadius;
            return this;
        }

        public Bulider setFillAllView(boolean fillAllView) {
            this.fillAllView = fillAllView;
            return this;
        }

        public WaterWaveView create() {
            return new WaterWaveView(this);
        }
    }
}
