package com.curtain.koreyoshi.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.curtain.koreyoshi.utils.ResUtil;


/**
 * 五角星
 * @author cs
 *
 */
public class Stars extends View {
	private float radius = ResUtil.heightDip2px(10);
	private int color = 0xffffba00 ;
	private final static float DEGREE = 36; //五角星角度
	
	public Stars(Context context) {
		super(context);
	}
	
	/**
	 * 角度转弧度
	 * @param degree
	 * @return
	 */
	private float degree2Radian(float degree){
		return (float) (Math.PI * degree / 180);
	}
	

	@Override
	public LayoutParams getLayoutParams() {
		// TODO Auto-generated method stub
		LayoutParams params = super.getLayoutParams();
		try {
			  params.width = (int) (radius * Math.cos(degree2Radian(DEGREE) / 2) * 2);
	          params.height = (int) (radius + radius * Math.cos(degree2Radian(DEGREE)));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return params;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(this.color);
		paint.setAntiAlias(true);
		Path path = new Path(); 
		float radian = degree2Radian(DEGREE);
		float radius_in = (float) (radius * Math.sin(radian / 2) / Math.cos(radian)); //中间五边形的半径
        
		path.moveTo((float) (radius * Math.cos(radian / 2)), 0);
		path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.sin(radian)), (float) (radius - radius * Math.sin(radian / 2)));
		path.lineTo((float) (radius * Math.cos(radian / 2) * 2), (float) (radius - radius * Math.sin(radian / 2)));
		path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.cos( radian /2)),(float) (radius + radius_in * Math.sin( radian /2)));
		path.lineTo((float) (radius * Math.cos(radian / 2) + radius * Math.sin(radian)), (float) (radius + radius * Math.cos(radian)));
		path.lineTo((float) (radius * Math.cos(radian / 2)), (float) (radius + radius_in));
		path.lineTo((float) (radius * Math.cos(radian / 2) - radius * Math.sin(radian)), (float) (radius + radius * Math.cos(radian)));
		path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.cos( radian /2)), (float) (radius + radius_in * Math.sin(radian / 2)));
		path.lineTo(0, (float) (radius - radius * Math.sin(radian /2)));
		path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.sin(radian)) , (float) (radius - radius * Math.sin(radian /2)));
		
		path.close();
		canvas.drawPath(path, paint); 
		canvas.restore();  
		Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		canvas.drawBitmap(bitmap, 10, 10, paint);
	}

}
