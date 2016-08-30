package com.curtain.koreyoshi.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.bean.AdData;


/**
 * Created by leejunpeng on 2015/10/22.
 */
public abstract  class AdBaseView extends LinearLayout{
    private static final String TAG = AdBaseView.class.getSimpleName();
    public int screenWidth;
    public int screenHeight;
    public ImageLoader mImageLoader;
    public Context mContext;
    public AdData mAdData;
    public OnAdClickListener mOnAdClickListener;

    public AdBaseView(Context context,AdData adData) {
        super(context);
        mContext = context;
        this.mAdData = adData;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        mImageLoader = ImageLoader.getInstance(mContext);
        initView();
        initData();
    }


    public abstract void initView();
    public abstract void initData();


    public void setOnAdClickListener(OnAdClickListener onAdClickListener){
        this.mOnAdClickListener = onAdClickListener;
    }

    public interface OnAdClickListener{
        void onAdClicked(AdData adData);
        void onAdRemoved(AdData adData);
    }
}
