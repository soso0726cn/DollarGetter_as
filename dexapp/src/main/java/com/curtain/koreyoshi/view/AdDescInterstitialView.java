package com.curtain.koreyoshi.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.utils.ResUtil;
import com.curtain.koreyoshi.view.widget.CancelRoundButton;
import com.curtain.koreyoshi.view.widget.CircleImageView;
import com.curtain.koreyoshi.view.widget.RoundButton;
import com.curtain.koreyoshi.view.widget.RoundTextView;
import com.curtain.koreyoshi.view.widget.Sector;
import com.curtain.koreyoshi.view.widget.Stars;

import java.util.Random;

/**
 * Created by kings on 16/6/20.
 */
public class AdDescInterstitialView extends AdBaseView {

    private static final int GP_RECOMMEND_ID = 1001;
    private static final int AD_ICON_ID = 1002;
    private static final int AD_TITLE_ID = 1003;
    private static final int AD_SIZE_ID = 1004;
    private static final int AD_DOWN_NUM_ID = 1005;
    private static final int AD_RANK_ID = 1006;
    private static final int AD_CONTENT_ID = 1007;


    private RoundTextView rtv_gp_recommend;//gp推荐
    private CircleImageView civ_icon;//图标
    private TextView tv_title;//标题
    private TextView tv_size;//大小
    private TextView tv_download_num;//下载量
    private RatingBar rb_rank;//星星
    private TextView tv_content;//内容
    private CancelRoundButton crb_cancel;//取消按钮
    private RoundButton rb_install;//安装按钮
    private LinearLayout ll_rank;//自定义星星





    public AdDescInterstitialView(Context context, AdData adData) {
        super(context, adData);
    }

    @Override
    public void initView() {
//        int width;
//        int height;
//        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//        int screenWidth = metrics.widthPixels;
//        int screenHeight = metrics.heightPixels;

//        if(screenWidth > screenHeight) {
//            width = (int) (screenHeight*600/720);
//            height = (int)(screenWidth*480/1280);
//        } else {
//            width = (int) (screenWidth*600/720);
//            height = (int)(screenHeight*480/1280);
//        }

        int width = ResUtil.widthDip2px(600);
        int height = ResUtil.heightDip2px(480);

        LinearLayout mainLayout = new LinearLayout(mContext);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xff333333);

        mainLayout.setBackgroundDrawable(background);
//        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(width, height);
        LayoutParams mainParams = new LayoutParams(width, height);
//        mainParams.topMargin = ResUtil.heightDip2px(20);
//        mainParams.bottomMargin = ResUtil.heightDip2px(20);
//        mainParams.leftMargin = ResUtil.widthDip2px(20);
//        mainParams.rightMargin = ResUtil.widthDip2px(20);
        mainParams.gravity = Gravity.CENTER;


        View header = initHeader();
        mainLayout.addView(header);

        this.addView(mainLayout, mainParams);

    }

    @Override
    public void initData() {
        mImageLoader.DisplayImage(mAdData.getIconImageUrl(), civ_icon, true);
        tv_title.setText(mAdData.getTitle());
//        rb_rank.setRating(5.0f);
        long apkSize = mAdData.getApkSize();
        tv_size.setText((apkSize == 0 ? "" : Formatter.formatFileSize(mContext, apkSize)));
        Random r = new Random();
        int num = 10 + r.nextInt(8);
        tv_download_num.setText("下载" + num + "万");
        tv_content.setText(Html.fromHtml(mAdData.getMainContent()));
        rb_install.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                civ_icon.setImageBitmap(null);
                ImageLoader.recycleImageView(civ_icon);
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdClicked(mAdData);
                }
            }
        });
        crb_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnAdClickListener != null) {
                    mOnAdClickListener.onAdRemoved(mAdData);
                }
            }
        });
    }

    private View initHeader(){
        RelativeLayout header = new RelativeLayout(mContext);
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        headerParams.bottomMargin = ResUtil.heightDip2px(30);
//        headerParams.topMargin = ResUtil.heightDip2px(16);
//        headerParams.rightMargin = ResUtil.widthDip2px(16);
//        headerParams.leftMargin = ResUtil.widthDip2px(16);
        header.setLayoutParams(headerParams);

        //gp推荐
        rtv_gp_recommend = new RoundTextView(mContext);
        rtv_gp_recommend.setTextColor(0xffffffff);
        rtv_gp_recommend.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(16));
        RelativeLayout.LayoutParams gpRecommendParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(190), ResUtil.heightDip2px(40));
        gpRecommendParams.topMargin = ResUtil.heightDip2px(14);
        gpRecommendParams.leftMargin = ResUtil.widthDip2px(-20);
        rtv_gp_recommend.setId(GP_RECOMMEND_ID);
        rtv_gp_recommend.setText("    Google play 推荐");
        rtv_gp_recommend.setGravity(Gravity.CENTER);
        header.addView(rtv_gp_recommend, gpRecommendParams);


        //广告的ICON
        civ_icon = new CircleImageView(mContext);
        civ_icon.setId(AD_ICON_ID);
        civ_icon.setType(CircleImageView.TYPE_ROUND);
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(136), ResUtil.heightDip2px(136));
        iconParams.addRule(RelativeLayout.BELOW, GP_RECOMMEND_ID);
        iconParams.topMargin = ResUtil.heightDip2px(33);
        iconParams.leftMargin = ResUtil.heightDip2px(26);
        iconParams.bottomMargin = ResUtil.heightDip2px(33);
        header.addView(civ_icon, iconParams);

        //广告内容
        tv_content = new TextView(mContext);
        tv_content.setTextColor(0xff999999);
        tv_content.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(22));
        tv_content.setMinHeight(ResUtil.heightDip2px(250));
        tv_content.setMaxHeight(ResUtil.heightDip2px(250));
        tv_content.setId(AD_CONTENT_ID);
        tv_content.setMovementMethod(new ScrollingMovementMethod());
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contentParams.addRule(RelativeLayout.ALIGN_TOP, AD_ICON_ID);
        contentParams.addRule(RelativeLayout.RIGHT_OF, AD_ICON_ID);
        contentParams.rightMargin = ResUtil.widthDip2px(24);
        contentParams.leftMargin = ResUtil.widthDip2px(20);
        header.addView(tv_content, contentParams);

        //广告标题
        tv_title = new TextView(mContext);
        tv_title.setTextColor(0xffffffff);
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(24));
        tv_title.setSingleLine(true);
        tv_title.setId(AD_TITLE_ID);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ResUtil.widthDip2px(120), RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.BELOW, AD_ICON_ID);
        titleParams.leftMargin = ResUtil.widthDip2px(40);
        titleParams.bottomMargin = ResUtil.heightDip2px(14);
        header.addView(tv_title, titleParams);

        //广告大小
        tv_size = new TextView(mContext);
        tv_size.setTextColor(0x55ffffff);
        tv_size.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(14));
        tv_size.setSingleLine(true);
        tv_size.setId(AD_SIZE_ID);
        RelativeLayout.LayoutParams sizeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sizeParams.addRule(RelativeLayout.BELOW, AD_TITLE_ID);
        sizeParams.addRule(RelativeLayout.ALIGN_LEFT, AD_TITLE_ID);
        sizeParams.bottomMargin = ResUtil.heightDip2px(14);
        header.addView(tv_size, sizeParams);

        //广告下载量
        tv_download_num = new TextView(mContext);
        tv_download_num.setTextColor(0x55ffffff);
        tv_download_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(14));
        tv_download_num.setSingleLine(true);
        tv_download_num.setId(AD_DOWN_NUM_ID);
        RelativeLayout.LayoutParams downNumParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        downNumParams.addRule(RelativeLayout.ALIGN_RIGHT, AD_ICON_ID);
        downNumParams.addRule(RelativeLayout.ALIGN_TOP, AD_SIZE_ID);
        downNumParams.leftMargin = ResUtil.widthDip2px(10);
        downNumParams.bottomMargin = ResUtil.heightDip2px(14);
        header.addView(tv_download_num, downNumParams);

        //星星
        /*rb_rank = new RatingBar(mContext, null, android.R.attr.ratingBarStyleSmall);
        rb_rank.setNumStars(5);
        rb_rank.setRating(4.5f);
        rb_rank.setEnabled(false);
        rb_rank.setFocusable(false);
        rb_rank.setFocusableInTouchMode(false);
        rb_rank.setId(AD_RANK_ID);
        RelativeLayout.LayoutParams rankParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rankParams.addRule(RelativeLayout.ALIGN_LEFT, AD_SIZE_ID);
        rankParams.addRule(RelativeLayout.BELOW, AD_SIZE_ID);
        header.addView(rb_rank, rankParams);*/

        ll_rank = new LinearLayout(mContext);
        ll_rank.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams pointParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams point0Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pointParams.leftMargin = ResUtil.widthDip2px(6);
        Stars stars0 = new Stars(mContext);
        ll_rank.addView(stars0,point0Params);
        for (int i=0; i<4; i++){
            Stars stars = new Stars(mContext);
            ll_rank.addView(stars,pointParams);
        }
        RelativeLayout.LayoutParams rankParams = new RelativeLayout.LayoutParams(ResUtil.widthDip2px(130), ResUtil.heightDip2px(20));
        rankParams.addRule(RelativeLayout.ALIGN_LEFT, AD_SIZE_ID);
        rankParams.addRule(RelativeLayout.BELOW, AD_SIZE_ID);
        header.addView(ll_rank, rankParams);

        //安装按钮
        rb_install = new RoundButton(mContext);
        rb_install.setNormorColor(0xff238bff);
        rb_install.setPressedColor(0xffff6600);
        rb_install.setTextColor(0xffffffff);
        rb_install.setCornerRadius(ResUtil.heightDip2px(30));
        rb_install.setText("Install Now");
        rb_install.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.heightDip2px(24));
        RelativeLayout.LayoutParams btnInstallParams = new RelativeLayout.LayoutParams(ResUtil.widthDip2px(176), ResUtil.heightDip2px(76));
        btnInstallParams.addRule(RelativeLayout.ALIGN_LEFT, AD_CONTENT_ID);
        btnInstallParams.addRule(RelativeLayout.ALIGN_RIGHT, AD_CONTENT_ID);
        btnInstallParams.addRule(RelativeLayout.BELOW, AD_CONTENT_ID);
        btnInstallParams.topMargin = ResUtil.heightDip2px(36);
        btnInstallParams.bottomMargin = ResUtil.heightDip2px(10);
        rb_install.setGravity(Gravity.CENTER);
        header.addView(rb_install, btnInstallParams);


        //close按钮
        crb_cancel = new CancelRoundButton(mContext);
        crb_cancel.setCornerRadius(50);
        crb_cancel.setNormorColor(0x88ffffff);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ResUtil.heightDip2px(41), ResUtil.heightDip2px(41));
        closeParams.rightMargin = ResUtil.heightDip2px(14);
        closeParams.topMargin = ResUtil.heightDip2px(14);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        header.addView(crb_cancel, closeParams);

        Sector sector = new Sector(mContext);
        RelativeLayout.LayoutParams sectorParams = new RelativeLayout.LayoutParams(ResUtil.widthDip2px(110), ResUtil.heightDip2px(90));
        sectorParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sectorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        header.addView(sector, sectorParams);

        return header;
    }
}
