package com.curtain.koreyoshi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;

public class ResUtil {
	public static int getRes(Context context, String name, String defType, String defPackage){
		return context.getResources().getIdentifier(name, defType, defPackage);
	}

	/**
	 * 将dip转化为px
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px转化成dip
	 */
	public static int px2dip(Context context, float pxValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);
	}

	public static int heightDip2px(float pxValue){
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		int screenHeight = metrics.heightPixels;
		int screenWidth = metrics.widthPixels;
		int result = 0;
		if (screenWidth > screenHeight){
			result = (int)(pxValue * screenWidth/1280 + 0.5f);
		}else {
			result = (int)(pxValue * screenHeight/1280 + 0.5f);
		}
		return result;
	}

	public static int widthDip2px(float pxValue){
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;
		int result = 0;
		if (screenWidth > screenHeight){
			result = (int)(pxValue * screenHeight/720 + 0.5f);
		}else {
			result = (int)(pxValue * screenWidth/720 + 0.5f);
		}
		return result;
	}


	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
//	public static int px2sp(Context context, float pxValue) {
//		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//		return (int) (pxValue / fontScale + 0.5f);
//	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
//	public static int sp2px(Context context, float spValue) {
//		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//		return (int) (spValue * fontScale + 0.5f);
//	}



	public static StateListDrawable newSelector(Context context, Drawable normal, Drawable pressed, Drawable focused,
												Drawable unable) {
		StateListDrawable bg = new StateListDrawable();
//		Drawable normal = idNormal == -1 ? null : ;
//		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
//		Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
//		Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		// View.WINDOW_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}

}
