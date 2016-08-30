package com.curtain.koreyoshi.business.gptracker;

/**
 * Created by yangzheng on 15/10/29.
 */
public interface TrackGPListerner {
    public void onTrackSuccess(ReferData referData);
    public void onTrackFailed();
}
