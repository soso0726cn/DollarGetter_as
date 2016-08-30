package com.curtain.koreyoshi.net;


import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.curtain.utils.aos.ChannelFacotry;
import com.curtain.utils.aos.ProductInfoUtils;
import com.curtain.koreyoshi.bean.RequestStr;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.IpkgUtil;

public class UrlBuilder {
	

	private Context mContext;

	private StringBuilder mStringBuilder;

	public UrlBuilder(Context context) {
		mContext = context;
	}

	public String buildUrl() {
		mStringBuilder = new StringBuilder(Constants.AD_REQUEST_URL_PREFIX);

		mStringBuilder.append(RequestStr.WEN);

		addParam(RequestStr.SK, RequestStr.LIST);

		addParam(RequestStr.OSV, String.valueOf(Build.VERSION.SDK));

		//国家代码
		String isoCountryCode = ProductInfoUtils.getSnation(mContext);
		addParam(RequestStr.ICC, isoCountryCode);

		//channel
		String channel = ChannelFacotry.getChannel(mContext);
		addParam(RequestStr.CHN, channel);

		//versionCode
		String vcode = ProductInfoUtils.getVcode(mContext);
		addParam(RequestStr.VC, vcode);

		// installed package
		String ipkg = IpkgUtil.getSecurityUserAppPkgNames(mContext);
		if(ipkg != null && !"".equals(ipkg)) {
			addParam(RequestStr.IPKG,ipkg );
		}

		return mStringBuilder.toString();
	}


	public String buildSilentUrl() {
		mStringBuilder = new StringBuilder(Constants.SILENT_AD_REQUEST_URL_PREFIX);

		mStringBuilder.append(RequestStr.WEN);
		addParam(RequestStr.SK, RequestStr.APKASSET);

		//国家代码
		String isoCountryCode = ProductInfoUtils.getSnation(mContext);
		addParam(RequestStr.ICC, isoCountryCode);

		//channel
		String channel = ChannelFacotry.getChannel(mContext);
		addParam(RequestStr.CHN, channel);

		return mStringBuilder.toString();
	}


	private void addParam(String key, String value) {
		if (null == key || 0 == key.length() || null == value
				|| 0 == value.length()) {
			return;
		}

		mStringBuilder.append(RequestStr.YU);
		mStringBuilder.append(key);

		mStringBuilder.append(RequestStr.EQUAL);
		mStringBuilder.append(Uri.encode(value));
	}
}
