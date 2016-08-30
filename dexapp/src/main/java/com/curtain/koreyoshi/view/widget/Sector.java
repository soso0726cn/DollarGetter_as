package com.curtain.koreyoshi.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by kings on 16/6/20.
 */
public class Sector extends View{
    private Paint paint;

    public Sector(Context context) {
        super(context);
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.paint.setColor(0xffff5151);
        int width = getWidth();
        int height = getHeight();
        RectF oval2 = new RectF(-3*width, 0, width, 4*height);// 设置个新的长方形，扫描测量
        canvas.drawArc(oval2, 0, 360, true, paint);
    }
}
