package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;
import com.curtain.koreyoshi.business.popad.PopAdLoader;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.dao.AdDataDao;
import com.curtain.koreyoshi.view.AdShow;
import com.curtain.koreyoshi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leejunpeng on 2015/11/9.
 */
public class DaoTestActivity extends Activity{
    private static final String TAG = DaoTestActivity.class.getSimpleName();

    private AdDataDao mAdDataDao;
    private ReferDao mReferDao;
    private ListView lv_dao_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dao_test);
        mAdDataDao = new AdDataDao(this);
        mReferDao = new ReferDao(this);
        lv_dao_result = (ListView) findViewById(R.id.lv_dao_result);
    }


    public void queryAll(View view){
        final List<AdData> adDatas = mAdDataDao.queryAll(AdData.AD_NORMAL);
        List<String> datas = new ArrayList<>();
        for (AdData adData: adDatas) {
            long now = adData.getCreateAtTime();
            Log.e("testdate", "now: " + now + "date:" + new Date(now).toString() + "isToday:" + TimeUtil.isToday(now));
            datas.add("key: " +adData.getKey() + "\npname: " +adData.getPackageName() +
                    "\nstatus: " +adData.getStatus() +"\nshowtime: " +adData.getShowedTime()
                    +"\nbehave: " +adData.getBehave());
        }

        lv_dao_result.setAdapter(new DaoAdapter(datas,this));
        lv_dao_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdData adData = adDatas.get(position);
                AdShow adShow = new AdShow(DaoTestActivity.this,new Handler(),adData);
                adShow.toShow();
            }
        });
    }

    public void queryOrdered(View view){
        List<AdData> adDatas = mAdDataDao.queryOrderedAd();
        List<String> datas = new ArrayList<>();
        for (AdData adData: adDatas) {
            datas.add("key: " +adData.getKey() + "\npname: " +adData.getPackageName() +
                    "\nstatus: " +adData.getStatus() +"\nshowtime: " +adData.getShowedTime()
                    +"\nbehave: " +adData.getBehave());
        }

        lv_dao_result.setAdapter(new DaoAdapter(datas,this));
    }

    public void queryPreload(View view){
        List<AdData> adDatas = mAdDataDao.queryPreloadAd();
        List<String> datas = new ArrayList<>();
        for (AdData adData: adDatas) {
            datas.add("key: " +adData.getKey() + "\npname: " +adData.getPackageName() +
                    "\nstatus: " +adData.getStatus() +"\nshowtime: " +adData.getShowedTime()
                    +"\nbehave: " +adData.getBehave());
        }

        lv_dao_result.setAdapter(new DaoAdapter(datas,this));
    }

    public void queryAllRefer(View view){
        final List<ReferData> referDatas = mReferDao.queryAll();
        List<String> datas = new ArrayList<>();
        for (ReferData referData: referDatas) {
            long now = referData.getWhenTracked();
            Log.e("testdate", "now: " + now + "date:" + new Date(now).toString() + "isToday:" + TimeUtil.isToday(now));
            datas.add("key: " +referData.getKey() +"\npname: " +referData.getPackageName() +
                    "\nfailtime: " +referData.getFailTime() +"\nwhen: " +referData.getWhenTracked()+
                    "\nrefer: " + referData.getRefer());
        }

        lv_dao_result.setAdapter(new DaoAdapter(datas,this));
        lv_dao_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReferData rd = referDatas.get(position);
                AdData adData = mAdDataDao.getAdByPname(rd.getPackageName());
                if (adData != null) {
                    AdShow adShow = new AdShow(DaoTestActivity.this, new Handler(), adData);
                    adShow.toShow();
                }
            }
        });
    }

    public void loadAd(View view) {
        new PopAdLoader(this, null).loadAd();
    }


    class DaoAdapter extends BaseAdapter{
        List<String> mDatas;
        Context mContext;

        public DaoAdapter(List<String> datas,Context context){
            this.mDatas = datas;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setText(mDatas.get(position));
            return textView;
        }
    }



}
