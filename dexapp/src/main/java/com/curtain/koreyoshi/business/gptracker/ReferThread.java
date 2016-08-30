package com.curtain.koreyoshi.business.gptracker;

/**
 * Created by yangzheng on 15/11/2.
 */
public class ReferThread  extends  Thread
{

    public ReferThread(Runnable runnable)
    {
        super(runnable);
    }

    private String key;

    private String pName;

    public void setRequestReferKeyAndPanme(String key,String pName)
    {
        this.key = key;
        this.pName = pName;
    }

    public boolean isSameReferKey(String key)
    {
        if(key != null)
        {
            if(key.equals(this.key))
            {
                return true;
            }
        }
        return false;
    }


}
