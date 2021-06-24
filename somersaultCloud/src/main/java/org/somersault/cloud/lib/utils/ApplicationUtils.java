package org.somersault.cloud.lib.utils;

import android.app.Application;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2020/12/8 18:13
 * 描    述：保存Application实例
 * 修订历史：
 * ================================================
 */
public class ApplicationUtils {

    private Application mApplication;

    private ApplicationUtils(){}

    static class Holder{
        protected static ApplicationUtils INSTANCE = new ApplicationUtils();
    }

    public static ApplicationUtils getInstance(){
        return Holder.INSTANCE;
    }

    public Application getApplication(){
        return mApplication;
    }

    public void setApplication(Application application){
        mApplication = application;
    }

}
