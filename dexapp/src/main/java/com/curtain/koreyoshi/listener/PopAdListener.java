package com.curtain.koreyoshi.listener;

import com.curtain.koreyoshi.bean.AdData;

/**
 * Created by lijichuan on 15/10/13.
 */
public abstract class PopAdListener {
    public PopAdListener() {
    }

    public void onAdClosed() {
    }

    public void onAdFailedToLoad(int errorCode) {
    }

    public void onAdLeftApplication() {
    }

    public void onAdOpened() {

    }

    public void onAdLoaded(AdData adData) {

    }
}
