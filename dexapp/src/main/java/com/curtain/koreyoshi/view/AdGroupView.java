package com.curtain.koreyoshi.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.utils.ResUtil;
import com.curtain.koreyoshi.view.widget.CancelRoundButton;
import com.curtain.koreyoshi.view.widget.RoundButton;


/**
 * Created by leejunpeng on 2015/10/22.
 */
public class AdGroupView extends AdBaseView {
    private static final String TAG = AdGroupView.class.getSimpleName();

    private static final int AD_ICON_ID = 1001;
    private static final int AD_TITLE_ID = 1002;
    private static final int AD_RAND_ID = 1003;
    private static final int AD_CONTENT_ID = 1004;


    private ImageView iv_ad_icon;
    private TextView tv_ad_title;
    private RatingBar rb_ad_rank;
    private TextView tv_ad_content;
    private RoundButton btn_install_now;
    private CancelRoundButton btn_cancel;

    public AdGroupView(Context context,AdData adData) {
        super(context, adData);

    }

    @Override
    public void initView() {

        int width;

        if(screenWidth > screenHeight) {
            width = (int) (screenHeight*0.8);
        } else {
            width = (int) (screenWidth*0.8);
        }

        LinearLayout mainLayout = new LinearLayout(mContext);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xffffffff);
        background.setCornerRadius(ResUtil.heightDip2px(12));

        mainLayout.setBackgroundDrawable(background);
        LayoutParams mainParams = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
        mainParams.topMargin = ResUtil.heightDip2px(20);
        mainParams.bottomMargin = ResUtil.heightDip2px(20);
        mainParams.leftMargin = ResUtil.widthDip2px(20);
        mainParams.rightMargin = ResUtil.widthDip2px(20);
        mainParams.gravity = Gravity.CENTER;

        //初始化上半部分
        View header = initHeader();
        mainLayout.addView(header);

        //分割线
        View line = new View(mContext);
        line.setBackgroundColor(0xffaaaaaa);
        LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ResUtil.dip2px(mContext, 1));
        lineParams.topMargin = ResUtil.heightDip2px(24);
        lineParams.bottomMargin = ResUtil.heightDip2px(24);
        lineParams.rightMargin = ResUtil.widthDip2px(20);
        lineParams.leftMargin = ResUtil.widthDip2px(20);
        line.setLayoutParams(lineParams);
        mainLayout.addView(line);

        //广告说明内容区
        tv_ad_content = new TextView(mContext);
        tv_ad_content.setTextColor(0xffaaaaaa);
        tv_ad_content.setMinHeight(ResUtil.heightDip2px(300));
        tv_ad_content.setMaxHeight(ResUtil.heightDip2px(300));
        tv_ad_content.setMovementMethod(new ScrollingMovementMethod());
        LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contentParams.gravity = Gravity.CENTER_HORIZONTAL;
        contentParams.leftMargin = ResUtil.widthDip2px(20);
        contentParams.rightMargin = ResUtil.widthDip2px(20);
        contentParams.topMargin = ResUtil.heightDip2px(20);
        contentParams.bottomMargin = ResUtil.heightDip2px(20);
        mainLayout.addView(tv_ad_content, contentParams);


        //安装按钮
        btn_install_now = new RoundButton(mContext);
        btn_install_now.setNormorColor(0xffff0000);
        btn_install_now.setPressedColor(0xffff6600);
        btn_install_now.setTextColor(0xffffffff);
        btn_install_now.setText(ByteCrypt.getString("Install Now".getBytes()));
        btn_install_now.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(44));
        LayoutParams btnInstallParams = new LayoutParams(ResUtil.widthDip2px(500), ResUtil.heightDip2px(92));
        btnInstallParams.topMargin = ResUtil.heightDip2px(40);
        btnInstallParams.bottomMargin = ResUtil.heightDip2px(40);
        btnInstallParams.gravity = Gravity.CENTER;

        mainLayout.addView(btn_install_now, btnInstallParams);

        this.addView(mainLayout, mainParams);

    }

    private View initHeader(){
        RelativeLayout header = new RelativeLayout(mContext);
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.bottomMargin = ResUtil.heightDip2px(24);
        headerParams.topMargin = ResUtil.heightDip2px(16);
        headerParams.rightMargin = ResUtil.widthDip2px(16);
        headerParams.leftMargin = ResUtil.widthDip2px(16);
        header.setLayoutParams(headerParams);

        //广告的ICON
        iv_ad_icon = new ImageView(mContext);
        iv_ad_icon.setId(AD_ICON_ID);
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(140), ResUtil.heightDip2px(140));
        header.addView(iv_ad_icon, iconParams);

        //广告标题
        tv_ad_title = new TextView(mContext);
        tv_ad_title.setTextColor(0xff000000);
        tv_ad_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(32));
        tv_ad_title.setSingleLine(true);
        tv_ad_title.setId(AD_TITLE_ID);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.RIGHT_OF, AD_ICON_ID);
        titleParams.leftMargin = ResUtil.widthDip2px(20);
        titleParams.topMargin = ResUtil.heightDip2px(20);
        header.addView(tv_ad_title, titleParams);

        //星星
        rb_ad_rank = new RatingBar(mContext, null, android.R.attr.ratingBarStyleSmall);
        rb_ad_rank.setNumStars(5);
        rb_ad_rank.setRating(4.5f);
        rb_ad_rank.setEnabled(false);
        rb_ad_rank.setFocusable(false);
        rb_ad_rank.setFocusableInTouchMode(false);
        RelativeLayout.LayoutParams rankParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rankParams.addRule(RelativeLayout.RIGHT_OF, AD_ICON_ID);
        rankParams.addRule(RelativeLayout.BELOW, AD_TITLE_ID);
        rankParams.leftMargin = ResUtil.widthDip2px(20);
        rankParams.bottomMargin = ResUtil.heightDip2px(20);
        rankParams.topMargin = ResUtil.heightDip2px(20);
//        rb_ad_rank.setLayoutParams(rankParams);
        header.addView(rb_ad_rank, rankParams);

        //close按钮
        btn_cancel = new CancelRoundButton(mContext);
        btn_cancel.setCornerRadius(50);
        btn_cancel.setNormorColor(0xffc7c7c7);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(30), ResUtil.heightDip2px(30));
        closeParams.rightMargin = ResUtil.heightDip2px(13);
        closeParams.topMargin = ResUtil.heightDip2px(13);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        header.addView(btn_cancel, closeParams);

        return header;
    }

    @Override
    public void initData() {
        mImageLoader.DisplayImage(mAdData.getIconImageUrl(), iv_ad_icon, true);
        tv_ad_title.setText(mAdData.getTitle());
        rb_ad_rank.setRating(4.5f);
        tv_ad_content.setText(Html.fromHtml(mAdData.getMainContent()));
        btn_install_now.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_ad_icon.setImageBitmap(null);
                ImageLoader.recycleImageView(iv_ad_icon);
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdClicked(mAdData);
                }
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_ad_icon.setImageBitmap(null);
                ImageLoader.recycleImageView(iv_ad_icon);
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdRemoved(mAdData);
                }
            }
        });
    }
}
