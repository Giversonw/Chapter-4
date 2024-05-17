package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
//import java.util.logging.Handler;
import com.bytedance.clockapplication.MainActivity;

import java.util.logging.LogRecord;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private float PANEL_RADIUS = 200.0f;// 表盘半径

    private float HOUR_POINTER_LENGTH;// 指针长度
    private float MINUTE_POINTER_LENGTH;
    private float SECOND_POINTER_LENGTH;
    private float UNIT_DEGREE = (float) (6 * Math.PI / 180);// 一个小格的度数

    private int mWidth, mCenterX, mCenterY, mRadius;

    private int degreesColor;

    private Paint mNeedlePaint;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
//    class MyHandler extends Handler
//    {
//        private WeakReference<View> view;
//        public MyHandler(WeakReference<View> view)
//        {
//            this.view=view;
//        }
//        @Override public void handleMessage(Message msg)
//        {
//            if (msg.what==0x123){
////                view.get().draw(canvas);
////                drawNeedles(canvas);
//                postInvalidate();
//            }
//            super.handleMessage(msg);
//        }
//    }
//    private Handler handler=new MyHandler(new WeakReference(this));

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNeedlePaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        PANEL_RADIUS = mRadius;
        HOUR_POINTER_LENGTH = PANEL_RADIUS - 400;
        MINUTE_POINTER_LENGTH = PANEL_RADIUS - 250;
        SECOND_POINTER_LENGTH = PANEL_RADIUS - 150;

        drawDegrees(canvas);
        drawHoursValues(canvas);
        drawNeedles(canvas);

        // todo 每一秒刷新一次，让指针动起来
        //也就是每秒运行一下 drawNeedles
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
////                postInvalidate();
//                handler.sendEmptyMessage(0x123);
//            }
//        },0,1000);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 1000);
    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(degreesColor); // 使用在类中定义的度数颜色变量
//        paint.setTextSize(PANEL_RADIUS / 7.5f); // 使用PANEL_RADIUS来计算字体大小，可以根据需要调整比例
//        paint.setTextAlign(Paint.Align.CENTER);
//
//        for (int i = 1; i <= 12; i++) {
//            // 根据钟表的比例调整文字的半径位置
//            float distance = mRadius - (mRadius / 6);
//            // 计算时针数字的位置
//            double angle = Math.PI / 6 * (i - 3);
//            int x = (int) (mCenterX + distance * Math.cos(angle));
//            int y = (int) (mCenterY + distance * Math.sin(angle));
//
//            // 对于一些数字不处于中线上，需要微调确保它们在视觉上更加居中
//            if (i == 6 || i == 12) {
//                y += paint.getTextSize() / 3;
//            }
//
//            canvas.drawText(String.valueOf(i), x, y, paint);
//        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(degreesColor);
        paint.setTextSize(mRadius / 7.5f);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent; // 计算文字的真实高度
        float textOffset = textHeight / 2 - fontMetrics.descent; // 计算文本相对于基线的偏移量

        for(int i = 1; i <= 12; i++) {
            double angle = Math.PI / 6 * (i - 3);
            float x = (float) (mCenterX + (mRadius - 80) * Math.cos(angle));
            float y = (float) (mCenterY + (mRadius - 80) * Math.sin(angle));

            y += textOffset; // 使用字体度量调整y坐标的位置
            canvas.drawText(String.valueOf(i), x, y, paint);
        }

    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        int nowHours = now.getHours();
        int nowMinutes = now.getMinutes();
        int nowSeconds = now.getSeconds();
        // 画秒针
        drawPointer(canvas, 2, nowSeconds);
        // 画分针
        // todo 画分针
        drawPointer(canvas, 1, 5*nowMinutes+nowSeconds/12);
        // 画时针
        int part = nowMinutes / 12;
        drawPointer(canvas, 0, 5 * nowHours + part);


    }


    private void drawPointer(Canvas canvas, int pointerType, int value) {

        float degree;
        float[] pointerHeadXY = new float[2];

        mNeedlePaint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        switch (pointerType) {
            case 0:
                degree = value * UNIT_DEGREE;
                mNeedlePaint.setColor(Color.WHITE);
                pointerHeadXY = getPointerHeadXY(HOUR_POINTER_LENGTH, degree);
                break;
            case 1:
                // todo 画分针，设置分针的颜色
                degree = value * UNIT_DEGREE;
                mNeedlePaint.setColor(Color.BLUE);
                pointerHeadXY = getPointerHeadXY(MINUTE_POINTER_LENGTH, degree);
                break;
            case 2:
                degree = value * UNIT_DEGREE;
                mNeedlePaint.setColor(Color.GREEN);
                pointerHeadXY = getPointerHeadXY(SECOND_POINTER_LENGTH, degree);
                break;
        }


        canvas.drawLine(mCenterX, mCenterY, pointerHeadXY[0], pointerHeadXY[1], mNeedlePaint);
    }

    private float[] getPointerHeadXY(float pointerLength, float degree) {
        float[] xy = new float[2];
        xy[0] = (float) (mCenterX + pointerLength * Math.sin(degree));
        xy[1] = (float) (mCenterY - pointerLength * Math.cos(degree));
        return xy;
    }


}