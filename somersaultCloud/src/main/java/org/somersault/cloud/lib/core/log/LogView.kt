package org.somersault.cloud.lib.core.log

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.BaseFloatView
import org.somersault.cloud.lib.databinding.ScViewLogBinding
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.widget.CustomLayoutParams

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/4/30 8:21
 * 描    述：Log日志悬浮框
 * 修订历史：
 * ================================================
 */
class LogView :BaseFloatView(){

    private var mBinding : ScViewLogBinding ? = null

    private var mAdapter : LogcatAdapter ? = null

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context): View {
        mBinding = ScViewLogBinding.inflate(LayoutInflater.from(context))
        return mBinding!!.root
    }

    override fun onViewCreated(floatView: View) {
        mAdapter = LogcatAdapter(floatView.context)
        var logLevelAdapter = ArrayAdapter.createFromResource(floatView.context,
        R.array.logcat_level,R.layout.sc_item_logcat_level)
        mBinding!!.spinnerLevel!!.adapter = logLevelAdapter
        mBinding!!.spinnerLevel!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var level = floatView?.context?.resources?.getStringArray(R.array.logcat_level)?.get(position)
                //通过选择的等级来过滤日志显示
                mAdapter!!.setLogLevel(level!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
        mBinding!!.ivClose!!.setOnClickListener{
            //关闭悬浮窗，需要关闭日志功能，清空数据
            LogcatManager.destory()
            FloatViewManager.instance.detach(this.mTag)
        }
        mBinding!!.ivExtension!!.setOnClickListener {
            //切换到Activity显示
            LogcatActivity.start(floatView.context)
        }
        mBinding!!.ivLogSwitch!!.setOnClickListener {
            //暂停或开始捕获
            LogcatManager.switchStatus()
        }
        //开始捕获日志
        LogcatManager.start(object :LogcatManager.CallBack{
            override fun onReceiveLog(info: LogcatInfo) {
                //只打印当前进程的
                if(info.pid != android.os.Process.myPid()){
                    return
                }

            }

        })
    }


    override fun onStop() {

    }

    override fun initFloatViewLayoutParams(params: CustomLayoutParams) {

    }
}