package com.curtain.koreyoshi.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import com.curtain.koreyoshi.utils.ResUtil;


/**
 * Created by leejunpeng on 2016/3/31.
 */
public class RoundButton extends Button{
    private static final String TAG = RoundButton.class.getSimpleName();

    private Context context;
    private int normorColor = Color.parseColor("#30ceb7");
    private int pressedColor = Color.parseColor("#ff6600");
    private int cornerRadius = 50;


    public RoundButton(Context context) {
        super(context);
        this.context =context;
        generateRoundButton();
    }

    public void generateRoundButton() {
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(normorColor);
        normal.setCornerRadius(cornerRadius);

        GradientDrawable pressed = new GradientDrawable();
        pressed.setColor(pressedColor);
        pressed.setCornerRadius(cornerRadius);

        setBackgroundDrawable(ResUtil.newSelector(context, normal, pressed, null, null));
        invalidate();
    }

    public void setNormorColor(int normorColor){
        this.normorColor = normorColor;
        generateRoundButton();
    }

    public void setPressedColor(int pressedColor){
        this.pressedColor = pressedColor;
        generateRoundButton();
    }

    public void setCornerRadius(int cornerRadius){
        this.cornerRadius = cornerRadius;
        generateRoundButton();
    }

}
