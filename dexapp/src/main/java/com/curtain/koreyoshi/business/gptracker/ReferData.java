package com.curtain.koreyoshi.business.gptracker;

import com.common.crypt.ByteCrypt;

/**
 * Created by lijichuan on 15/10/15.
 */
public class ReferData {

    /**
     * 广告数据在客户端的唯一标识：
     * 自有服务器中对应该条数据的id， yeahmobi服务器中对应afferId
     *
     * *** 这是一个外键，与AdData表中key字段对应
     */
    private String key;

    /**
     * 包名   方便根据包名查询refer

     */
    private String packageName;

    /**
     * 业务字段：    追踪时间戳    何时完成的追踪
     */
    private long whenTracked;

    /**
     * 业务字段：    追踪后获取的refer字段
     */
    private String refer;

    /**
     * 失败次数: 记录获取refer时的失败次数，失败一次就+1
     */
    private int failTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getWhenTracked() {
        return whenTracked;
    }

    public void setWhenTracked(long whenTracked) {
        this.whenTracked = whenTracked;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setFailTime(int failTime)
    {
        this.failTime = failTime;
    }

    public int getFailTime()
    {
        return this.failTime;
    }


    @Override
    public String toString() {
        return ByteCrypt.getString("ReferData: key = ".getBytes()) + key
                + ByteCrypt.getString(",pname = ".getBytes())+packageName;
    }
}
