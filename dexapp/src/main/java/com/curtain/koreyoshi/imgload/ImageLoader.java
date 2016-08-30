package com.curtain.koreyoshi.imgload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

	private Context mContext;
	private static Handler mHandler = null;
	private MemoryCache memoryCache = new MemoryCache();
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	// 线程池
	private ExecutorService executorService;
	public static ImageLoader getInstance(Context context){
		synchronized (ImageLoader.class) {
			if (imageLoader == null) {
				imageLoader = new ImageLoader(context);
			}
		}
		return imageLoader;
	}

	//默认图片
	int stub_bmp;

	private static ImageLoader imageLoader = null;

	private ImageLoader(Context context) {
		this.mContext = context;
		executorService = Executors.newFixedThreadPool(5);
		//默认图片： 设置为android机器人
//		stub_bmp = ResUtil.getRes(mContext, "ks_ic_launcher", "mipmap", mContext.getPackageName());
		stub_bmp = android.R.drawable.sym_def_app_icon;
	}

	//最主要的方法： 显示图片时调用，从内存和本地缓存获取，不再进行网络请求请求
	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		if (TextUtils.isEmpty(url)){
			imageView.setImageResource(stub_bmp);
//			imageView.setImageBitmap(stub_bmp);
		}
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null && !bitmap.isRecycled()){
			imageView.setImageBitmap(bitmap);
		}else{
    		// 先从文件缓存中查找是否有
			File f = getPushPicFile(url);
    		if (f != null && f.exists()){
    			Bitmap bmp = decodeFile(f);
				//显示SD卡中的图片缓存
        		if(bmp != null)
        			imageView.setImageBitmap(bmp);
    		}else{
				//线程加载网络图片   TODO: 为加快显示速度，不再从网络实时请求
    			//queuePhoto(url, imageView);
    			imageView.setImageResource(stub_bmp);
//				imageView.setImageBitmap(stub_bmp);
        	}
		}
	}

	public File getPushPicFile(String url) {
		if (TextUtils.isEmpty(url)){
			return null;
		}
		String filename = String.valueOf(url.hashCode());
		File f = new File(Constants.IMAGE_DOWNLOAD_DIR + filename);
		return f;
	}

	public void queuePhoto(String url, ImageView imageView) {
		downloadPhoto(url, imageView, null);
	}
	
	public void downloadPhoto(String url, ImageView imageView, LoaderCallBack callBack) { 
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p,callBack));
	}

	public Bitmap getBitmapFromLocal(String url){
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		}
		File f = getPushPicFile(url);
		if (f == null){
			return null;
		}

		if(!f.getParentFile().exists()){
			boolean rtn = f.getParentFile().mkdirs();
		}
		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}
		return null;
	}


	private Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		bitmap = memoryCache.get(url);
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		}
		File f = getPushPicFile(url);
		if (f == null){
			return null;
		}
		if(!f.getParentFile().exists()){
			boolean rtn = f.getParentFile().mkdirs();
		}
		// 先从文件缓存中查找是否有
		if (f != null && f.exists()){
			bitmap = decodeFile(f);
		}
		if (bitmap != null){
			return bitmap;
		}
		// 最后从指定的url中下载图片
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			if(!CopyStream(is, os) && f.exists())
				f.delete();
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			return null;
		}
	}
	
	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	public static Bitmap decodeFile(File f) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			// decode image size
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
//			final int REQUIRED_SIZE = 200;
//			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			// decode with inSampleSize
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = 1;
			fis = new FileInputStream(f);
			bitmap = BitmapFactory.decodeStream(fis, null, o);
		} catch (FileNotFoundException e) {
		}
		finally {
			if (fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		
		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		LoaderCallBack callBack;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		PhotosLoader(PhotoToLoad photoToLoad,LoaderCallBack callBack) {
			this.photoToLoad = photoToLoad;
			this.callBack = callBack;
		}

		@Override
		public void run() {
			Bitmap bmp = getBitmap(photoToLoad.url);
			MyLog.e("ljjp", photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if(photoToLoad.imageView != null){
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				// 更新的操作放在UI线程中
//				Activity a = (Activity) photoToLoad.imageView.getContext();
//				a.runOnUiThread(bd);
				new Handler(photoToLoad.imageView.getContext().getMainLooper()).post(bd);
			}else{
				if (callBack != null) {
					if (bmp == null) {
						callBack.loaderFailure();
					}else {
						callBack.loaderSuccess(bmp);
					}
				}
//				MyLog.i("jyh1", "~~~~photoToLoad.imageView = null~~~~");
//				mHandler.obtainMessage(Constants.GET_LOCKSCREEN_IMAGE, photoToLoad.url).sendToTarget();
				
			}
		}
	}
	
	public interface LoaderCallBack{
		public void loaderSuccess(Bitmap bitmap);
		public void loaderFailure();
	}
	
	public static void setHandler(Handler handler)
	{
		mHandler = handler;
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
	
		}
	}

	public void clearCache() {
		memoryCache.clear();
	}

	public static boolean CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					return true;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 回收ImageView占用的图像内存;
	 * @param view
	 */
	public static void recycleImageView(View view){
		if(view==null) return;
		if(view instanceof ImageView){
			Drawable drawable=((ImageView) view).getDrawable();
			if(drawable instanceof BitmapDrawable){
				Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
				if (bmp != null && !bmp.isRecycled()){
					((ImageView) view).setImageBitmap(null);
					bmp.recycle();
					bmp=null;
				}
			}
		}
	}

}
