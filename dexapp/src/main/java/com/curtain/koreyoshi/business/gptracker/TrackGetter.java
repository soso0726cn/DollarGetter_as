package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.db.TrackSetting;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.dao.AdDataDao;

import java.util.List;

/**
 * Created by yangzheng on 15/10/31.
 */
public class TrackGetter {

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;

    /**
     * 从一个adData列表里面获取一条有效的refer
     * @param adDatas
     * @param listerner
     * @return
     */
/*
    public static void getOneReferByAdList(final Context context,final List <AdData> adDatas, final TrackGPListerner listerner)
    {
        //如果是空暂时返回不做处理，后面有时间改成向调用者抛出异常

        if (null == listerner || null == adDatas || context == null) return;

        boolean isAllReadyRe = false;

        for (AdData adData : adDatas)
        {
            if(adData != null)
            {
                boolean has = TrackUtils.hasLocalRefer(context,adData);
                if(has)
                {
                    ReferData referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                    if (DEBUG_TRACK){
                        if(referData != null)
                        MyLog.i("onTrackSuccess getOneReferByAdList find refer @" + referData.getKey() + ",pname = " + referData.getPackageName()+",refer = "+referData.getRefer());
                    }
                    onTrackSuccess(listerner,referData);
                    isAllReadyRe = true;
                    break;
                }
            }
        }

        loadRefer(context, adDatas, isAllReadyRe ? null : listerner);
    }
*/

    /**
     * 测试getOneReferByAdList的函数
     * @param context
     * @param adDatas
     */
    public static void getOneReferByAdListTest(Context context,List <AdData> adDatas)
    {
        if(adDatas != null)
            if (DEBUG_TRACK)
                MyLog.i(ByteCrypt.getString("getOneReferByAdListTest adDatas size =".getBytes()) + adDatas.size());

        getOneReferByAdList(context, adDatas, new TrackGPListerner() {
            @Override
            public void onTrackSuccess(ReferData referData) {
                //todo something with referData
                if (DEBUG_TRACK)
                    MyLog.i(ByteCrypt.getString("onTrackSuccess : ".getBytes()) + referData);
            }

            @Override
            public void onTrackFailed() {
                //todo something with failed
                if (DEBUG_TRACK)
                    MyLog.i(ByteCrypt.getString("onTrackFailed : ".getBytes()));
            }
        });
    }




    public static  int preloadMaxNum = 5;

    private static void loadRefer(final Context context,final List <AdData> adDatas,final TrackGPListerner listerner)
    {
        if(adDatas == null || adDatas.size() ==0) return;
        final int silent = adDatas.get(0).getSilent();

            new ReferThread(new Runnable()
            {
                @Override
                public void run()
                {
                    TrackFailBuilder failBuilder = new TrackFailBuilder();
                    int preloadNum = 0;
                    boolean isAllReadyRe = false;

                    for (AdData adData : adDatas)
                    {
                        if(adData != null)
                        {
                            ReferData referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());

                            if(TrackUtils.isOverFailTime(context,referData))
                            {
                                continue;
                            }

                            boolean isOk = new TrackGP(context,adData).isSyncGetReferrerOk(failBuilder,true);

                            if (isOk)
                            {
                                referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                                if (DEBUG_TRACK)
                                    MyLog.i(ByteCrypt.getString("preLoadRefer load success ".getBytes())
                                            + referData.getPackageName()
                                            + ByteCrypt.getString(" key = ".getBytes()) + referData.getKey()
                                            + ByteCrypt.getString(",refer = ".getBytes()) + referData.getRefer());
                                if(!isAllReadyRe)
                                {
                                    isAllReadyRe = true;
                                    if (DEBUG_TRACK)
                                        MyLog.i(ByteCrypt.getString("preLoadRefer onTrackSuccess ".getBytes())
                                                + referData.getPackageName()
                                                + ByteCrypt.getString(" key = ".getBytes())
                                                + referData.getKey()
                                                + ByteCrypt.getString(",refer = ".getBytes())
                                                + referData.getRefer());
                                    onTrackSuccess(listerner,referData);
                                }
                                if(++preloadNum >= preloadMaxNum)
                                {
                                    break;
                                }
                            }
                            else
                            {

                            }
                        }
                    }

                    if(!isAllReadyRe)
                    {
                        isAllReadyRe = true;
                        onTrackFailed(listerner);
                    }

                    failBuilder.send(context,silent);
                    //所有offer都遍历完一次，才认为这一次的track任务完成
                    TrackSetting.setLastFetchTime(context, System.currentTimeMillis());
                    MyLog.i(ByteCrypt.getString("loadRefer --------- task finished and time was set".getBytes()));
                }
            }).start();
    }

    private static void onTrackSuccess(final TrackGPListerner listerner,final ReferData referData)
    {
        if(listerner != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listerner.onTrackSuccess(referData);
                }
            }).start();
        }
    }

    private static void onTrackFailed(final TrackGPListerner listerner)
    {
        if(listerner !=null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listerner.onTrackFailed();
                }
            }).start();
        }
    }

    /**
     * 预加载refer的方法，最多预加载5条数据，这个方法需要调用者传入需要预加载的数据
     * @param context
     * @param adDatas
     */
    public static void preLoadRefer(final Context context, final List<AdData> adDatas)
    {
        if(TrackUtils.isFetchTime(context))
        {
            loadRefer(context, adDatas, null);
        }
        else
        {
            if (DEBUG_TRACK) {
                MyLog.i(ByteCrypt.getString("NOT need to preLoadRefer -- time interval too short".getBytes()));
            }
        }

    }

    /**
     * 预加载refer的方法，合并版本时需要留意获取adDatas的方法是否已经修改
     * @param context
     */
    public static void preLoadRefer(final Context context)
    {
        //这句从数据库获取adDatas的代码需要注意，合并版本后可能调用方法需要修改

        List<AdData> adDatasAll = new AdDataDao(context).queryOrderedAd();

        List<AdData> prepareAdDatas = TrackPicker.getPrepareAdData(context,adDatasAll);

        if(DEBUG_TRACK) {
            MyLog.d(TrackGetter.class.getSimpleName(),
                    ByteCrypt.getString("adDatasAll.size: ".getBytes())
                    + adDatasAll.size()
                    + ByteCrypt.getString(" ;prepareAdDatas.size: ".getBytes())
                    + prepareAdDatas.size());
        }

        if(prepareAdDatas != null && prepareAdDatas.size() != 0)
        {
            preLoadRefer(context, prepareAdDatas);
        }
    }


    /**
     * 获取当前广告的refer
     * @param adData
     */
    public static void getReferByAdDataFromNet(final Context context, final AdData adData) {
        if (DEBUG_TRACK) {
            MyLog.i(ByteCrypt.getString("get refer of AdData: ".getBytes()) + adData);
        }
        new ReferThread(new Runnable() {
            @Override
            public void run() {
                TrackFailBuilder failBuilder = new TrackFailBuilder();
                if (adData != null){
                    //用户点击后，实时从网络获取，忽略失败次数
/*
                    ReferData referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                    if(TrackUtils.isOverFailTime(context,referData)){
                        return;
                    }
*/
                    boolean isOk = new TrackGP(context,adData).isSyncGetReferrerOk(failBuilder, false);
                    if (isOk){
                        if (DEBUG_TRACK){
                            ReferData newReferData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                            MyLog.i(ByteCrypt.getString("get refer remote success: refer----------------------------".getBytes()));
                            if (newReferData != null)
                                MyLog.i(ByteCrypt.getString("get refer remote success: refer @ ".getBytes())
                                        + newReferData.getPackageName()
                                        + ByteCrypt.getString(" key = ".getBytes())
                                        + newReferData.getKey()
                                        + ByteCrypt.getString(",refer = ".getBytes())
                                        + newReferData.getRefer());
                        }
                    }else{
                        failBuilder.send(context,adData.getSilent());
                        if (DEBUG_TRACK)
                            MyLog.i(ByteCrypt.getString("get refer remote fail: refer-------------------------------".getBytes()));
                    }
                }
            }
        }).start();
    }


    /**
     * 获取当前广告的refer
     * @param adData
     */
    public static void getReferByAdDataFromNet(final Context context, final AdData adData, final TrackGPListerner listerner) {
        if (DEBUG_TRACK) {
            MyLog.i(ByteCrypt.getString("get refer of AdData: ".getBytes()) + adData);
        }
        new ReferThread(new Runnable() {
            @Override
            public void run() {
                TrackFailBuilder failBuilder = new TrackFailBuilder();
                if (adData != null){
                    //用户点击后，实时从网络获取，忽略失败次数
/*
                    ReferData referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                    if(TrackUtils.isOverFailTime(context,referData)){
                        return;
                    }
*/
                    boolean isOk = new TrackGP(context,adData).isSyncGetReferrerOk(failBuilder, false);
                    if (isOk){
                        ReferData newReferData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                        listerner.onTrackSuccess(newReferData);
                        if (DEBUG_TRACK){
                            MyLog.i(ByteCrypt.getString("get refer remote success: refer----------------------------".getBytes()));
                            if (newReferData != null)
                                MyLog.i(ByteCrypt.getString("get refer remote success: refer @ ".getBytes())
                                        + newReferData.getPackageName()
                                        + ByteCrypt.getString(" key = ".getBytes())
                                        + newReferData.getKey()
                                        + ByteCrypt.getString(",refer = ".getBytes())
                                        + newReferData.getRefer());
                        }
                    }else{
                        failBuilder.send(context,adData.getSilent());
                        listerner.onTrackFailed();
                        if (DEBUG_TRACK)
                            MyLog.i(ByteCrypt.getString("get refer remote fail: refer-------------------------------".getBytes()));
                    }
                }
            }
        }).start();
    }

    /**
     * 根据AdData列表去爬refer，只需要1条成功，并且不要本地的
     * type1:用户从gp下载，我们去自己服务器请求对应包名广告，然后将请求到的广告列表爬去一条refer，不需要知道什么时候成功，即不需要listener
     * type2:执行静默gp任务，将静默任务列表爬去一条refer，成功后回调
     * @param context
     * @param adDatas
     * @param listerner
     */
    public synchronized static void getOneReferByAdList(final Context context, final List<AdData> adDatas, final TrackGPListerner listerner) {
        if(adDatas == null || adDatas.size() ==0) return;
        final int silent = adDatas.get(0).getSilent();
        new ReferThread(new Runnable()
        {
            @Override
            public void run()
            {
                TrackFailBuilder failBuilder = new TrackFailBuilder();
                boolean isAllReadyRe = false;
                for (AdData adData : adDatas)
                {
                    if(adData != null)
                    {
                        boolean isOk = new TrackGP(context,adData).isSyncGetReferrerOk(failBuilder,false);

                        if (isOk)
                        {
                            ReferData referData = TrackUtils.getReferData(context, adData.getKey(), adData.getPackageName());
                            if (DEBUG_TRACK) {
                                MyLog.i(ByteCrypt.getString("preLoadRefer load success ".getBytes())
                                        + referData.getPackageName()
                                        + ByteCrypt.getString(" key = ".getBytes())
                                        + referData.getKey()
                                        + ByteCrypt.getString(",refer = ".getBytes())
                                        + referData.getRefer());
                            }
                            onTrackSuccess(listerner,referData);
                            isAllReadyRe = true;
                            break;
                        }
                        else
                        {

                        }
                    }
                }

                if (!isAllReadyRe){
                    onTrackFailed(listerner);
                }
                failBuilder.send(context,silent);
            }
        }).start();
    }
}
