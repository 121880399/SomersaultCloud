package org.somersault.cloud.lib.core.log

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.BaseFloatView
import org.somersault.cloud.lib.databinding.ScViewLogBinding
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.plugin.LogPlugin
import org.somersault.cloud.lib.utils.ScreenUtils
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
        LogDataManager.init(floatView.context)
        if(LogcatManager.getLogSwitchStatus()){
            mBinding!!.ivLogSwitch!!.setImageResource(R.mipmap.sc_ic_stop_log)
        }else{
            mBinding!!.ivLogSwitch!!.setImageResource(R.mipmap.sc_ic_start_log)
        }
        mAdapter = LogcatAdapter(floatView.context,LogDataManager.getShowLogData())
        val linearLayoutManager = LinearLayoutManager(floatView.context)
        linearLayoutManager.stackFromEnd = true
        mBinding!!.rvLog.layoutManager = linearLayoutManager
        mBinding!!.rvLog.adapter = mAdapter
        //R.array.logcat_level 只用于在界面上显示，不要用于数据处理和判断，数据处理使用LogcatInfo中定义的等级
        var logLevelAdapter = ArrayAdapter.createFromResource(floatView.context,
        R.array.logcat_level,R.layout.sc_item_logcat_level)
        mBinding!!.spinnerLevel!!.adapter = logLevelAdapter
        mBinding!!.spinnerLevel!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var level = LogcatInfo.getLevel(position)
                //通过选择的等级来过滤日志显示
                LogDataManager.setLogLevel(level!!)
                mAdapter!!.setData(LogDataManager.getShowLogData())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mBinding!!.ivClose!!.setOnClickListener{
            //关闭悬浮窗，需要关闭日志功能，清空数据
            LogcatManager.destory()
            LogDataManager.reset()
            FloatViewManager.instance.detach(this.mTag)
            LogPlugin.isOpen = false
        }
        mBinding!!.ivExtension!!.setOnClickListener {
            //切换到Activity显示
            LogcatActivity.start(floatView.context)
            FloatViewManager.instance.detach(this.mTag)
        }
        mBinding!!.ivLogSwitch!!.setOnClickListener {
            //暂停或开始捕获
            LogcatManager.switchStatus()
            //修改图标显示
            if(LogcatManager.getLogSwitchStatus()){
                mBinding!!.ivLogSwitch!!.setImageResource(R.mipmap.sc_ic_stop_log)
            }else{
                mBinding!!.ivLogSwitch!!.setImageResource(R.mipmap.sc_ic_start_log)
            }
        }
        mBinding!!.ivClearLog!!.setOnClickListener {
            LogDataManager.clear()
            mAdapter!!.notifyDataSetChanged()
        }
        //开始捕获日志
        LogcatManager.start(object :LogcatManager.CallBack{
            override fun onReceiveLog(info: LogcatInfo) {
                //添加日志
                LogDataManager.addItem(info)
                mAdapter!!.setData(LogDataManager.getShowLogData())
                if (!mBinding!!.rvLog.canScrollVertically(1)) {
                    mBinding!!.rvLog.scrollToPosition(mAdapter!!.itemCount - 1)
                }
            }
        })
    }


    override fun onStop() {

    }

    /**
    * 初始化悬浮窗的宽高
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/24 9:05
    */
    override fun initFloatViewLayoutParams(params: CustomLayoutParams) {
        params.width = ScreenUtils.dp2px(300F).toInt()
        params.height = ScreenUtils.dp2px(400F).toInt()
        params.x = ScreenUtils.dp2px(30F).toInt()
        params.y = ScreenUtils.dp2px(20F).toInt()
    }
}