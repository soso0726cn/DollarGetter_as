package com.curtain.koreyoshi.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.utils.ResUtil;
import com.curtain.koreyoshi.view.widget.CancelRoundButton;


/**
 * Created by leejunpeng on 2015/10/22.
 */
public class AdFlyingBoardView extends AdBaseView{
    private static final String TAG = AdFlyingBoardView.class.getSimpleName();

    private ImageView iv_ad_pic;
    private CancelRoundButton btn_cancel;

    public AdFlyingBoardView(Context context,AdData adData) {
        super(context, adData);
    }

    @Override
    public void initView() {

        int width;
        int height;

        if(screenWidth > screenHeight) {
            width = (int) (screenHeight*0.8);
            height = (int) (width *(mAdData.getHeight()*1.0 / mAdData.getWidth()));
        } else {
            width = (int) (screenWidth*0.8);
            height = (int) (width *(mAdData.getHeight()*1.0 / mAdData.getWidth()));
        }

        LinearLayout mainLayout = new LinearLayout(mContext);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xffffffff);
        background.setCornerRadius(ResUtil.heightDip2px(12));
        mainLayout.setBackgroundDrawable(background);
        LayoutParams mainParams = new LayoutParams(width, height);
        mainParams.topMargin = ResUtil.heightDip2px(20);
        mainParams.bottomMargin = ResUtil.heightDip2px(20);
        mainParams.leftMargin = ResUtil.widthDip2px(20);
        mainParams.rightMargin = ResUtil.widthDip2px(20);
        mainParams.gravity = Gravity.CENTER;

        RelativeLayout content = new RelativeLayout(mContext);
        LayoutParams contentParams = new LayoutParams(width, height);


        iv_ad_pic = new ImageView(mContext);
        iv_ad_pic.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams picParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        picParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


        btn_cancel = new CancelRoundButton(mContext);
        btn_cancel.setCornerRadius(50);
        btn_cancel.setNormorColor(0xffd5d5d5);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(41), ResUtil.heightDip2px(41));
        closeParams.rightMargin = ResUtil.heightDip2px(14);
        closeParams.topMargin = ResUtil.heightDip2px(14);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        content.addView(iv_ad_pic, picParams);
        content.addView(btn_cancel, closeParams);

        mainLayout.addView(content, contentParams);
        this.addView(mainLayout, mainParams);
    }

    @Override
    public void initData() {
        mImageLoader.DisplayImage(mAdData.getMainImageUrl(),iv_ad_pic,true);

        iv_ad_pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_ad_pic.setImageBitmap(null);
                ImageLoader.recycleImageView(iv_ad_pic);
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdClicked(mAdData);
                }
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_ad_pic.setImageBitmap(null);
                ImageLoader.recycleImageView(iv_ad_pic);
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdRemoved(mAdData);
                }
            }
        });
    }
}
