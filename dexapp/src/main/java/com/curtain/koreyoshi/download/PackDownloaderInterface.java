package com.curtain.koreyoshi.download;


import com.curtain.koreyoshi.bean.AdData;

/**
 * Created by kings on 16/6/16.
 */
public interface PackDownloaderInterface {

    void startDownload(final AdData adData);

    void startSilentDownload(final AdData adData);

}
