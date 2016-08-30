package com.curtain.koreyoshi.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.curtain.koreyoshi.utils.ResUtil;


/**
 * Created by leejunpeng on 2016/4/14.
 */
public class CancelRoundButton extends RoundButton {
    private Paint paint;
    public CancelRoundButton(Context context) {
        super(context);
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int r = getWidth()/2;
        float sX = (float) (Math.sin(45.0f/180) * r);
        float sY = sX;
        float eX = 2*r - sX;
        float eY = 2*r - sY;
        this.paint.setColor(Color.BLACK);
        paint.setStrokeWidth(ResUtil.heightDip2px(2));
        int val = ResUtil.heightDip2px(10);
        canvas.drawLine(sX+val,sY+val,eX-val,eY-val,paint);
        canvas.drawLine(sX+val,eY-val,eX-val,sY+val,paint);

    }
}
