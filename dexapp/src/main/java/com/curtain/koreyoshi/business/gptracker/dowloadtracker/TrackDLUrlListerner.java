package com.curtain.koreyoshi.business.gptracker.dowloadtracker;

/**
 * Created by yangzheng on 15/11/10.
 */
public interface TrackDLUrlListerner {
    public void onTrackSuccess(String apkUrl);
    public void onTrackFailed();
}
