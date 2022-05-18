package org.somersault.cloud.lib.core.log

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import org.somersault.cloud.lib.R

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/17 16:10
 * 描    述：用来管理从Logcat输出的日志数据
 * 为了解决view跟Activity数据不统一的问题
 * 修订历史：
 * ================================================
 */
@SuppressLint("StaticFieldLeak")
object LogDataManager {

    /**
     * 日志最大限制
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 9:42
     */
    private const val LOG_MAX_COUNT = 1000

    /**
     * 到达日志最大限制后删除的日志数量
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 9:42
     */
    private const val LOG_REMOVE_COUNT = LOG_MAX_COUNT / 5

    /**
     * 所有的日志数据
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/1 10:00
     */
    private var mLogcatData : ArrayList<LogcatInfo>  =  ArrayList()

    /**
     * 当前显示的数据
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/1 10:05
     */
    private var mShowData : ArrayList<LogcatInfo>  =  ArrayList()

    /**
     * 标记是否正在进行清除操作，如果正在清除，则不添加数据
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/14 19:12
     */
    @Volatile private var mCleaning = false

    /**
     * 搜索关键字
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/2 8:35
     */
    @Volatile private var mKeyWord = ""

    /**
     * 当前日志过滤等级
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 9:38
     */
    private var mLogLevel = ""

    private var mContext :Context? = null

    fun init(context: Context){
        mContext = context.applicationContext
    }

    /**
     * 添加单条日志
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/1 9:32
     */
    @Synchronized
    fun addItem(info:LogcatInfo) {
        //正在清除则不添加数据
        if(mCleaning){
            return
        }
        if (mShowData!!.isNotEmpty()) {
            var lastInfo = mShowData!![mShowData!!.size - 1]
            if (TextUtils.equals(info.level, lastInfo.level)
                && TextUtils.equals(info.tag, lastInfo.tag)
            ) {
                lastInfo.appendLog(info.content!!)
                return
            }
        }

        //判断当前日志信息是否符合显示要求
        if (isConform(info)) {
            mShowData!!.add(info)

            //如果当前日志数量超过了最大限定
            if (mShowData!!.size > LOG_MAX_COUNT) {
                //删除前200条数据
                mShowData!!.removeAll(mShowData!!.subList(0, LOG_REMOVE_COUNT))
            }
        }
        //最开始mShowData与mLogcatData都指向同一个对象
        if (mShowData != mLogcatData) {
            //如果不是同一给对象，mLogcatData也应该添加
            mLogcatData!!.add(info)
            //如果所有日志数量超过最大数，删除前200条记录
            if(mLogcatData!!.size > LOG_MAX_COUNT){
                mLogcatData!!.removeAll(mLogcatData!!.subList(0, LOG_REMOVE_COUNT))
            }
        }
    }

    /**
     * 清空日志
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/14 18:40
     */
    @Synchronized
    fun clear(){
        //如果两个集合正在遍历，这时候清空数据会发生并发修改异常
        //如果两个集合增加添加元素，这时候清空，也会出现线程安全问题
        if(mShowData!=null && mLogcatData!=null){
            mCleaning = true
            mShowData!!.clear()
            mLogcatData!!.clear()
            mCleaning = false
        }
    }


    /**
     * 设置日志等级
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 10:35
     */
    fun setLogLevel(level: String){
        mLogLevel = level
        filterData()
    }

    /**
     * 判断是否符合日志显示的要求
     * 如果关键字为空或者内容中包含关键字
     * 并且
     * 日志等级是最低或者日志等级相等
     * 才符合要求
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 9:39
     */
    fun isConform(info:LogcatInfo):Boolean{
        return (TextUtils.isEmpty(mKeyWord) || info.content!!.contains(mKeyWord))
                &&
                (TextUtils.equals(mContext!!.resources.getStringArray(R.array.logcat_level)[0],mLogLevel)
                        || TextUtils.equals(info.level,mLogLevel))
    }


    /**
     * 设置关键字
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 10:12
     */
    fun setKeyWord(keyword:String){
        mKeyWord = keyword
        filterData()
    }

    /**
    * 得到输入的关键字
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/18 11:05
    */
    fun getKeyWord():String{
        return mKeyWord
    }

    /**
     * 过滤数据
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/3 10:34
     */
    @Synchronized
    fun filterData(){
        if(mCleaning){
            return
        }
        //如果没有任何过滤，包括关键字和日志等级
        if(TextUtils.isEmpty(mKeyWord)
            && TextUtils.equals(mContext!!.resources.getStringArray(R.array.logcat_level)[0],mLogLevel)){
            mShowData = mLogcatData
            return
        }

        if(mShowData == mLogcatData){
            //如果目前指向的是同一个对象,由于过滤需要重新创建一个对象
            //不能影响到所有日志数据
            mShowData = ArrayList()
        }
        mShowData!!.clear()

        //将符合要求的数据过滤出来，添加到显示数据中
        mLogcatData!!.forEach {
            if(isConform(it)){
                mShowData!!.add(it)
            }
        }
    }

    /**
    * 获取所有Log数据
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/18 11:24
    */
    fun getAllLogData(): ArrayList<LogcatInfo> {
        return mLogcatData
    }

    /**
    * 获取正在显示的Log数据
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/18 11:25
    */
    fun getShowLogData(): ArrayList<LogcatInfo> {
        return mShowData
    }
}