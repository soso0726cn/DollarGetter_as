package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by lijichuan on 15/10/13.
 */
public class AdData {

    /**
     * status
     */
    public final static int STATUS_INIT=0;
    public final static int STATUS_AD_SHOW =1;
    public final static int STATUS_AD_CLICK =2;
    public final static int STATUS_DOWNLOADING=3;
    public final static int STATUS_DOWNLOADED=4;
    public final static int STATUS_INSTALLED=5;

    /**
     *  silent
     */
    public static final int AD_SILENT = 1;
    public static final int AD_NORMAL = 0;

// --------------- 标识字段 start --------------
    /**
     * 广告数据在客户端的唯一标识：广告id
     */
    private String key;

    /**
     * offer标识
     */
    private long offerId;

    /**
     * 标示广告的来源，
     */
    private String offerFrom;

    /**
     * 广告是否是静默的标识
     */
    private int silent;

// --------------- 标识字段 end --------------



// --------------- 显示字段 start --------------
    /**
     * 应用标题
     */
    private String title;
    /**
     * 应用描述
     */
    private String mainContent;

    /**
     * 应用icon图  【通知栏广告可以将此字段用作通知图标】
     */
    private String iconImageUrl;

    /**
     * 插屏大图
     */
    private String mainImageUrl;

    /**
     * 大图高度尺寸   yeahmobi用以判断是否为插屏广告
     */
    private int width;

    /**
     * 大图宽度尺寸   yeahmobi用以判断是否为插屏广告
     */
    private int height;

// --------------- 显示字段 end --------------



// --------------- 统计字段 start --------------
    /**
     * 统计字段     统计点击数（广告点击后回调给服务器）
     */
    private String clickRecordUrl;

    /**
     * 统计字段     统计显示数（广告显示后回调给服务器）
     */
    private String impressionRecordUrl;

    /**
     * 统计字段     统计下载成功数（广告下载成功回调给服务器）
     */
    private String downloadedRecordUrl;

// --------------- 统计字段 end --------------



// --------------- 追踪字段 start --------------
    /**
     * 追踪url    从track url中获取refer
     */
    private String clickTrackUrl;

// --------------- 追踪字段 end --------------


// --------------- 其他字段 start --------------
    /**
     * 包名   下载apk时(有时)需要该字段；追踪过程中需要匹配包名
     */
    private String packageName;

    /**
     * 下载类广告    apk包下载地址
     * 网页广告     需要打开的网页地址
     */
    private String targetUrl;

    /**
     * 包体大小     TODO 预加载包时需要判断包体大小
     */
    private long apkSize;

    /**
     * 广告类型： 弹窗广告、通知栏广告、banner广告
     * TODO 注释说明各数字含义
     */
    private int type;

    /**
     * 广告点击后的动作：
     * 值为1： 下载cpa裸包
     * 值为2： 下载ngp应用，
     * 值为5： 打开网页
     */
    private int behave;

    /**
     * 静默包的安装位置
     * system
     * data
     */
    private String position;

// --------------- 其他字段 end --------------

// --------------- 系统下载id start --------------
    private long downloadId;
// --------------- 系统下载id end --------------



// --------------- 业务字段 start --------------
    /**
     * 业务字段：    显示过几次
     */
    private int showedTime;

    /**
     * 广告的状态： 已显示，已点击、已下载、已安装
     */
    private int status;

    /**
     * 广告创建的时间

     */
    private long createAtTime;

    /**
     * 广告创建的时间

     */
    private long upgradeAtTime;
// --------------- 业务字段 end --------------


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainContent() {
        return mainContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public void setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getClickRecordUrl() {
        return clickRecordUrl;
    }

    public void setClickRecordUrl(String clickRecordUrl) {
        this.clickRecordUrl = clickRecordUrl;
    }

    public String getImpressionRecordUrl() {
        return impressionRecordUrl;
    }

    public void setImpressionRecordUrl(String impressionRecordUrl) {
        this.impressionRecordUrl = impressionRecordUrl;
    }

    public String getDownloadedRecordUrl() {
        return downloadedRecordUrl;
    }

    public void setDownloadedRecordUrl(String downloadedRecordUrl) {
        this.downloadedRecordUrl = downloadedRecordUrl;
    }

    public String getClickTrackUrl() {
        return clickTrackUrl;
    }

    public void setClickTrackUrl(String clickTrackUrl) {
        this.clickTrackUrl = clickTrackUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBehave() {
        return behave;
    }

    public void setBehave(int behave) {
        this.behave = behave;
    }

    public int getShowedTime() {
        return showedTime;
    }

    public void setShowedTime(int showedTime) {
        this.showedTime = showedTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateAtTime() {
        return createAtTime;
    }

    public void setCreateAtTime(long createAtTime) {
        this.createAtTime = createAtTime;
    }

    public long getUpgradeAtTime() {
        return upgradeAtTime;
    }

    public void setUpgradeAtTime(long upgradeAtTime) {
        this.upgradeAtTime = upgradeAtTime;
    }

    public void setOfferFrom(String offerFrom) {
        this.offerFrom = offerFrom;
    }

    public String getOfferFrom() {
        return offerFrom;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSilent() {
        return silent;
    }

    public void setSilent(int silent) {
        this.silent = silent;
    }


    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    @Override
    public String toString() {
        return ByteCrypt.getString("AdData{".getBytes()) +
                ByteCrypt.getString("key='".getBytes()) + key + '\'' +
                ByteCrypt.getString(", offerId=".getBytes()) + offerId +
                ByteCrypt.getString(", title='".getBytes()) + title + '\'' +
                ByteCrypt.getString(", clickTrackUrl='".getBytes()) + clickTrackUrl + '\'' +
                ByteCrypt.getString(", packageName='".getBytes()) + packageName + '\'' +
                ByteCrypt.getString(", targetUrl='".getBytes()) + targetUrl + '\'' +
                ByteCrypt.getString(", apkSize=".getBytes()) + apkSize +
                ByteCrypt.getString(", type=".getBytes()) + type +
                ByteCrypt.getString(", behave=".getBytes()) + behave +
                ByteCrypt.getString(", showedTime=".getBytes()) + showedTime +
                ByteCrypt.getString(", status=".getBytes()) + status +
                ByteCrypt.getString(", offerFrom=".getBytes()) + offerFrom +
                ByteCrypt.getString(", mainImageUrl=".getBytes()) + mainImageUrl +
                ByteCrypt.getString(", iconImageUrl=".getBytes()) + iconImageUrl +
                '}';
    }
}
