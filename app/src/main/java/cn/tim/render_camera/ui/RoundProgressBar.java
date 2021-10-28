package cn.tim.render_camera.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import cn.tim.render_camera.R;
import cn.tim.render_camera.widget.CameraView;


public class RoundProgressBar extends View {
    private static final String TAG = "RoundProgressBar";
    private final int mRadius;
    private final int mColor;
    private final int mLineWidth;
    private final int mTextSize;
    private int mProgress;

    private Paint mPaint;
    private Timer mTimer;

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBar_radius, dp2px(30));
        mColor = ta.getColor(R.styleable.RoundProgressBar_color, 0xffff0000);
        mLineWidth = (int) ta.getDimension(R.styleable.RoundProgressBar_line_width, dp2px(3));
        mTextSize = (int) ta.getDimension(R.styleable.RoundProgressBar_android_textSize, dp2px(36));
        mProgress = ta.getInt(R.styleable.RoundProgressBar_android_progress, 30);

        ta.recycle();
        initPaint();
    }

    private float dp2px(int dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int mProgressCount = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: ACTION_DOWN");
                mCameraView.startRecord();
                // 按下启动计时器开始更新录制进度
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mProgressCount > 1000) {
                            mTimer.cancel();
                        }
                        mProgressCount += 1;
                        RoundProgressBar.this.setProgress(mProgressCount /5);
                    }
                }, 0, 30);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: ACTION_UP");
                mCameraView.stopRecord();
                mProgressCount = 0;
                mTimer.cancel();
                performClick();
                recordListener.stop();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private RecordListener recordListener;
    public void setRecordListener(RecordListener listener) {
        this.recordListener = listener;
    }
    public interface RecordListener {
        void stop();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int needWidth = measureWidth() + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(needWidth, widthSize);
            } else {
                width = needWidth;
            }
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }else {
            int needHeight = measureHeight() + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(needHeight, heightSize);
            } else { //MeasureSpec.UNSPECIFIED
                height = needHeight;
            }
        }
        setMeasuredDimension(width, height);
    }

    private int measureHeight() {
        return mRadius * 2;
    }

    private int measureWidth() {
        return mRadius * 2;
    }


    private final Rect bound = new Rect();
    private RectF mRectF;
    private RectF getRectF() {
        if(mRectF == null) {
            mRectF = new RectF(0, 0, getWidth() - getPaddingLeft() * 2, getHeight() - getPaddingLeft() * 2);
        }
        return mRectF;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth * 1.0f / 4);

        mPaint.setStrokeWidth(mLineWidth);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        float angle = mProgress * 1.0f / 100 * 360;
        canvas.drawArc(getRectF(), 0, angle, false, mPaint);
        canvas.restore();

        //String text = "已拍摄" + mProgress + "%";
        String text = "按住拍";
        mPaint.setStrokeWidth(0);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);
        int y = getHeight() / 2;
        mPaint.getTextBounds(text, 0, text.length(), bound);
        int textHeight = bound.height();
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, 0, text.length(), (float) getWidth() / 2, y + (float) textHeight / 2, mPaint);

        mPaint.setStrokeWidth(0);
    }


    private static final String INSTANCE = "instance";
    private static final String KEY_PROGRESS = "key_progress";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROGRESS, mProgress);
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Parcelable parcelable = bundle.getParcelable(INSTANCE);
            super.onRestoreInstanceState(parcelable);
            mProgress = bundle.getInt(KEY_PROGRESS);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private CameraView mCameraView;
    public void setCameraView(CameraView cameraView) {
        this.mCameraView = cameraView;
    }
}