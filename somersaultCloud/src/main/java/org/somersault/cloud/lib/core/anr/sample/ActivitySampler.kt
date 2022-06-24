package org.somersault.cloud.lib.core.anr.sample

import android.text.TextUtils
import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import org.somersault.cloud.lib.manager.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/24 9:09
 * 描    述：Activity采集器
 * 修订历史：
 * ================================================
 */
class ActivitySampler :IANRSampleListener{

    private var mSampleResultListener: ISampleResultListener? = null

    override fun doSample(msgId: String, anrTime: Long) {
        if(mSampleResultListener!=null) {
            if(TextUtils.isEmpty(ActivityManager.instance.getStackInfo())){
                mSampleResultListener!!.onSampleError("Activity stack is empty")
            }else {
                mSampleResultListener!!.onSampleSuccess(msgId,ActivityManager.instance.getStackInfo(),anrTime)
            }

        }
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        mSampleResultListener = resultListener
    }
}