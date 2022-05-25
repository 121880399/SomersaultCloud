package org.somersault.cloud.lib.core.log

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.base.BaseActivity
import org.somersault.cloud.lib.databinding.ScActivityLogcatBinding
import org.somersault.cloud.lib.plugin.LogPlugin
import org.somersault.cloud.lib.utils.DateUtils
import org.somersault.cloud.lib.utils.FileUtils
import org.somersault.cloud.lib.utils.SCThreadManager
import java.io.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList

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

    /**
    * 记录搜素过的关键字
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/20 8:40
    */
    private val mSearchKeywords = ArrayList<String>()

    private var mBinding : ScActivityLogcatBinding ? = null

    private var mLogcatAdapter : LogcatAdapter ?  = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ScActivityLogcatBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding!!.root)
        initData()
    }

    private fun initData(){
        mLogcatAdapter = LogcatAdapter(this,LogDataManager.getShowLogData())
        mBinding!!.rvLog.layoutManager = LinearLayoutManager(this)
        mBinding!!.rvLog.adapter = mLogcatAdapter
        mBinding!!.ivLogSwitch.setOnClickListener {
            //切换回悬浮窗
        }
        mBinding!!.ivClose.setOnClickListener {
            //退出日志功能,先停止日志采集，再清空数据
            LogcatManager.destory()
            LogDataManager.reset()
            LogPlugin.isOpen = false
            finish()
        }
        var logLevelAdapter = ArrayAdapter.createFromResource(this,
            R.array.logcat_level, R.layout.sc_item_logcat_level)
        mBinding!!.spinnerLevel.adapter = logLevelAdapter
        mBinding!!.spinnerLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var level = LogcatInfo.getLevel(position)
                //通过选择的等级来过滤日志显示
                LogDataManager.setLogLevel(level!!)
                mLogcatAdapter!!.setData(LogDataManager.getShowLogData())
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
        mBinding!!.logFilter!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               mBinding!!.logFilter!!.removeCallbacks(mSearchRunnable)
               mBinding!!.logFilter!!.postDelayed(mSearchRunnable,300)

               //移除的主要目的是用户连续输入，下次执行该方法的时候，可能还没有执行该任务，所以将之前的任务移除掉
               mBinding!!.logFilter!!.removeCallbacks(mRecordKeywordRunnable)
               //因为没有搜索按钮可以点击，只能一段时间记录一次用户输入的数据
               mBinding!!.logFilter!!.postDelayed(mRecordKeywordRunnable,3000)
            }
        })
        mBinding!!.ivLogSearch!!.setOnClickListener {
            var keyword = mBinding!!.logFilter!!.text.toString()
            if(keyword.isNotEmpty()){
                mBinding!!.logFilter!!.setText("")
            }else{
                showRecords()
            }
        }
        //分享功能
        mBinding!!.ivShareLog!!.setOnClickListener {
            this@LogcatActivity.lifecycleScope.launch{
                var file : File ? = null
                withContext(SCThreadManager.getSaveLogDispatcher()){
                    file = saveFile()
                }
                if(file == null){
                    Toast.makeText(this@LogcatActivity,R.string.sc_create_log_file_failed,Toast.LENGTH_SHORT).show()
                }else{
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    val uri = FileProvider.getUriForFile(applicationContext, "$packageName.logcat_fileprovider",file!!)
                    shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                    if(packageManager.queryIntentActivities(shareIntent,0).isEmpty()){
                        Toast.makeText(this@LogcatActivity,R.string.sc_not_support_on_this_device,Toast.LENGTH_SHORT).show()
                    }else{
                        startActivity(shareIntent)
                    }
                }
            }
        }
        //回到顶部
        mBinding!!.btnBackTop!!.setOnClickListener {
            if(mLogcatAdapter == null || mLogcatAdapter!!.itemCount == 0){
                return@setOnClickListener
            }
            mBinding!!.rvLog!!.scrollToPosition(0)
        }

        //滚至底部
        mBinding!!.btnScrollToBottom!!.setOnClickListener {
            if(mLogcatAdapter == null || mLogcatAdapter!!.itemCount == 0){
                return@setOnClickListener
            }
            mBinding!!.rvLog!!.scrollToPosition(mLogcatAdapter!!.itemCount-1)
        }
        //Item点击事件
        mLogcatAdapter!!.setOnItemClickListener(object : LogcatAdapter.OnItemClickListener {
            override fun onItemClick(info: LogcatInfo, position: Int) {

            }

        })
        //Item长按事件
        mLogcatAdapter!!.setOnItemLongClickListener(object : LogcatAdapter.OnItemLongClickListener {
            override fun onItemLongClick(info: LogcatInfo, position: Int) {
               val list = arrayOf("复制","分享")
                AlertDialog.Builder(this@LogcatActivity).setItems(list
                ) { dialog, which ->
                    when (which){
                        0 -> copyLog(position)
                        1 -> shareLog(position)
                    }
                }.show()
            }

        })
        //开始捕获日志
        LogcatManager.start(object :LogcatManager.CallBack{
            override fun onReceiveLog(info: LogcatInfo) {
                //只打印当前进程的
                if(info.pid != android.os.Process.myPid()){
                    return
                }
                //添加日志
                LogDataManager.addItem(info)
                mLogcatAdapter!!.notifyDataSetChanged()
            }
        })
    }


    private fun copyLog(position : Int){
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(manager != null){
            manager.setPrimaryClip(ClipData.newPlainText("log",mLogcatAdapter!!.getItem(position).originContent))
            Toast.makeText(this,R.string.sc_logcat_copy_success,Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,R.string.sc_logcat_copy_failed,Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareLog(positon : Int){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,mLogcatAdapter!!.getItem(positon).originContent)
        startActivity(intent)
    }

    private fun saveFile():File?{
        if(!FileUtils.isExternalStorageWritable()){
            return null
        }
        val externalFilesDir = getExternalFilesDir(null) ?: return null
        val allData = LogDataManager.getAllLogData() ?: return null
        val logFile = File(externalFilesDir,DateUtils.DATE_FORMAT.format(Date()) + ".txt")
        if(logFile.exists()){
            if(!logFile.delete()){
                return null
            }
        }
        var writer : BufferedWriter ? = null
        return try {
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(logFile)))
            allData.forEach {
                writer.write(it.originContent+"\n")
            }
            logFile
        }catch (e : IOException){
            e.printStackTrace()
            null
        }finally {
            writer?.close()
        }
    }

    private fun showRecords(){
        val list = mSearchKeywords.toTypedArray()
        AlertDialog.Builder(this).setItems(list
        ) { dialog, which ->
            mBinding!!.logFilter!!.setText(mSearchKeywords!![which])
            mBinding!!.logFilter!!.setSelection(mBinding!!.logFilter!!.text.toString().length)
        }.show()
    }

    /**
    * 搜索关键字任务
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/20 8:28
    */
    private val mSearchRunnable = Runnable {
        var keyword = mBinding!!.logFilter!!.text.toString()
        LogDataManager.setKeyWord(keyword)
        mLogcatAdapter!!.notifyDataSetChanged()
        mBinding!!.rvLog!!.layoutManager!!.scrollToPosition(mLogcatAdapter!!.itemCount-1)
        if(keyword.isNotEmpty()){
            //显示删除按钮
            mBinding!!.ivLogSearch!!.visibility = View.VISIBLE
            mBinding!!.ivLogSearch!!.setImageResource(R.mipmap.sc_ic_delete_record)
        }else{
            //如果存在记录，则显示记录按钮
            if(mSearchKeywords.isNotEmpty()){
                mBinding!!.ivLogSearch!!.visibility = View.VISIBLE
                mBinding!!.ivLogSearch!!.setImageResource(R.mipmap.sc_ic_record)
            }else{
                mBinding!!.ivLogSearch!!.visibility = View.GONE
            }
        }
    }

    /**
    * 记录关键字任务
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/20 9:03
    */
    private val mRecordKeywordRunnable = Runnable {
        var keyword = mBinding!!.logFilter!!.text.toString()
        if(keyword.isNullOrEmpty() || mSearchKeywords.contains(keyword)){
            return@Runnable
        }
        mSearchKeywords.add(keyword)
    }


}