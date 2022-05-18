package org.somersault.cloud.lib.core.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.base.BaseActivity
import org.somersault.cloud.lib.databinding.ScActivityLogcatBinding

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/3 11:28
 * 描    述：日志打印Activity，比悬浮窗显示更多的功能
 * 修订历史：
 * ================================================
 */
class LogcatActivity : BaseActivity() {

    companion object{
        fun start(context: Context){
            var intent = Intent(context,LogcatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private var mBinding : ScActivityLogcatBinding ? = null

    private var mLogcatAdapter : LogcatAdapter ?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ScActivityLogcatBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding!!.root)
        initData()
    }

    private fun initData(){
        mLogcatAdapter = LogcatAdapter(this,LogDataManager.getAllLogData(),LogDataManager.getShowLogData())
        mBinding!!.rvLog.layoutManager = LinearLayoutManager(this)
        mBinding!!.rvLog.adapter = mLogcatAdapter
        mBinding!!.ivLogSwitch.setOnClickListener {
            //切换回悬浮窗
        }
        mBinding!!.ivClose.setOnClickListener {
            //退出日志功能,先停止日志采集，再清空数据
            LogcatManager.destory()
            LogDataManager.reset()
            finish()
        }
        var logLevelAdapter = ArrayAdapter.createFromResource(this,
            R.array.logcat_level, R.layout.sc_item_logcat_level)
        mBinding!!.spinnerLevel.adapter = logLevelAdapter
        mBinding!!.spinnerLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var level = resources!!.getStringArray(R.array.logcat_level)?.get(position)
                //通过选择的等级来过滤日志显示
                LogDataManager.setLogLevel(level!!)
                mLogcatAdapter!!.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
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
            mLogcatAdapter!!.notifyDataSetChanged()
        }
        //分享功能
        //导出日志功能
    }

}