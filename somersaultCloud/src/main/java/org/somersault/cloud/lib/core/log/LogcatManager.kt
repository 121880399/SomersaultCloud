package org.somersault.cloud.lib.core.log

import android.widget.Toast
import org.somersault.cloud.lib.utils.ApplicationUtils
import org.somersault.cloud.lib.utils.Logger
import org.somersault.cloud.lib.utils.SCThreadManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/4/30 9:58
 * 描    述：日志管理类，用来获取输出的LOG日志
 * 修订历史：
 * ================================================
 */
object LogcatManager {

    @Volatile private var LOG_SWITCH = true

    /**
    * 是否已经开启日志输出，不允许重复开启
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 8:24
    */
    @Volatile private var STARTED = false

    /**
    * 是否关闭LOG功能，需要暂停
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 12:10
    */
    @Volatile private var isNeedStop = false

    /**
    * 备份Log信息
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 22:50
    */
    private val LOG_BACKUP : ArrayList<LogcatInfo> = ArrayList()

    val IGNORE_LIST = arrayListOf("--------- beginning of crash","--------- beginning of main","--------- beginning of system")

    var mCallBack : CallBack ? = null


    /**
    * 用于监听线程的状态
     * 预留
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/18 17:47
    */
    var mThreadStatusCallback  : ThreadStatus ? = null

    /**
    * Log Tag 提示，给业务层进行预埋
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/27 8:11
    */
    var mLogTagTip : Map<String,String> ? = null

    /**
    * 切换捕获日志的状态
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 12:06
    */
    @Synchronized
    fun switchStatus(){
        if(LOG_SWITCH){
            pause()
        }else{
            resume()
        }
    }

    /**
    * 开始捕获日志
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 22:58
    */
    @Synchronized
    fun start(callback:CallBack){
        mCallBack = callback
        if(STARTED){
            return
        }
        STARTED = true
        LOG_SWITCH = true
        SCThreadManager.runOnReadLogThread(mRunnable)
    }

    /**
    * 暂停捕获日志
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 22:55
    */
    @Synchronized
    fun pause(){
        LOG_SWITCH = false
    }

    /**
    * 恢复日志捕获
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 8:06
    */
    @Synchronized
    fun resume(){
        LOG_SWITCH = true
        if(mCallBack != null && !LOG_BACKUP.isEmpty()){
            LOG_BACKUP.filterNotNull().forEach {
                mCallBack!!.onReceiveLog(it)
            }
        }
        LOG_BACKUP.clear()
    }

    /**
    * 停止捕获
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 8:28
    */
    @Synchronized
    fun destory(){
        STARTED = false
        LOG_SWITCH = false
        mCallBack = null
        isNeedStop = true
    }

    /**
    * 获取当前日志打印开关状态
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/14 18:04
    */
    fun getLogSwitchStatus():Boolean{
        return LOG_SWITCH
    }

    
    
    val mRunnable = Runnable {
        run{
            var reader : BufferedReader? = null
            try {
                var process = ProcessBuilder("logcat","-v","threadtime").start()
                reader = BufferedReader(InputStreamReader(process.inputStream))
                while (true){
                    var line = reader.readLine()
                    if(line.isNullOrEmpty()){
                        continue
                    }
                    if(isNeedStop){
                        Logger.d(LogcatManager::class.java.name,"Thread had stoped!")
                        if(mThreadStatusCallback!=null){
                            mThreadStatusCallback?.onThreadStop()
                        }
                        break
                    }
                    if(IGNORE_LIST.contains(line)){
                        continue
                    }
                    try{
                        var info: LogcatInfo? = LogcatInfo.getLocatInfo(line!!) ?: continue
                        if(!LOG_SWITCH){
                            LOG_BACKUP.add(info!!)
                            continue
                        }

                        if(mCallBack!=null){
                            //只打印当前进程的
                            if(info!!.pid != android.os.Process.myPid()){
                                continue
                            }
                            SCThreadManager.runOnUIThread { mCallBack!!.onReceiveLog(info!!) }
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
                pause()
            }catch (e:IOException){
                e.printStackTrace()
                pause()
            }finally {
                //执行到这里说明，任务执行完毕，要将isNeedStop标志位恢复false
                isNeedStop = false
                if(reader!=null){
                    try{
                        reader.close()
                    }catch (e : IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

     interface CallBack{
        /**
        * 接收到日志后回调
        * 作者: ZhouZhengyi
        * 创建时间: 2022/4/30 21:15
        */
        fun onReceiveLog(info:LogcatInfo)
    }

    interface  ThreadStatus{

        /**
        * 当线程停止时回调
        * 作者: ZhouZhengyi
        * 创建时间: 2022/5/18 17:46
        */
        fun onThreadStop()
    }
}