package com.curtain.koreyoshi.view.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.curtain.koreyoshi.utils.ResUtil;


/**
 * Created by kings on 16/6/20.
 */
public class RoundTextView extends TextView{

    private Context context;
    private int normorColor = 0xffff3d5d;
    private int pressedColor = 0xffff3d5d;
    private int cornerRadius = 50;

    public RoundTextView(Context context) {
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
