package org.somersault.cloud.lib.core.log

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ExpandableListView
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.utils.ScreenUtils
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/1 8:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LogcatAdapter(context: Context) : RecyclerView.Adapter<LogcatAdapter.ViewHolder>() {


    /**
    * 所有的日志数据
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 10:00
    */
    private var mLogcatData : ArrayList<LogcatInfo> ? = null

    /**
    * 当前显示的数据
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 10:05
    */
    private var mShowData : ArrayList<LogcatInfo> ? = null

    /**
    * Item点击监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 11:12
    */
    var mItemClickListener : OnItemClickListener ? = null

    /**
    * Item长按监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 11:12
    */
    var mItemLongClickListener : OnItemLongClickListener ? = null
    /**
    * 存储滑动的X坐标
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 11:28
    */
    private val mScrollXSet : SparseIntArray = SparseIntArray()
    /**
    * 搜索关键字
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/2 8:35
    */
    private var mKeyWord = ""

    /**
    * 当前日志过滤等级
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 9:38
    */
    private var mLogLevel = ""

    /**
    * 报错代码正则表达式
     * 匹配：(CallEndMessageItemProvider.java:74)
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/2 9:03
    */
    private val ERROR_CODE_REGEX = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)")

    private var mContext : Context? = null

    init {
        mLogcatData = ArrayList()
        mShowData = mLogcatData
        mContext = context
    }

    companion object{
        /**
         * 日志最大限制
         * 作者: ZhouZhengyi
         * 创建时间: 2022/5/3 9:42
         */
        private val LOG_MAX_COUNT = 1000

        /**
         * 到达日志最大限制后删除的日志数量
         * 作者: ZhouZhengyi
         * 创建时间: 2022/5/3 9:42
         */
        private val LOG_REMOVE_COUNT = LOG_MAX_COUNT / 5
    }

    /**
    * 添加单条日志
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 9:32
    */
    fun addItem(info:LogcatInfo) {
        if (mShowData!!.isNotEmpty()) {
            var lastInfo = getItem(mShowData!!.size - 1) as LogcatInfo
            if (TextUtils.equals(info.level, lastInfo.level)
                && TextUtils.equals(info.tag, lastInfo.tag)
            ) {
                lastInfo.appendLog(info.content!!)
                notifyItemChanged(mShowData!!.size - 1)
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
                notifyDataSetChanged()
            } else {
                notifyItemInserted(mShowData!!.size - 1)
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
    * 设置关键字
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 10:12
    */
    fun setKeyWord(keyword:String){
        mKeyWord = keyword
        filterData()
        notifyDataSetChanged()
    }


    /**
    * 设置日志等级
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 10:35
    */
    fun setLogLevel(level: String){
        mLogLevel = level
        filterData()
        notifyDataSetChanged()
    }

    /**
    * 过滤数据
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/3 10:34
    */
    fun filterData(){
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


    fun getItem(position: Int): LogcatInfo {
        TODO("Not yet implemented")
    }



     inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnTouchListener,
        View.OnLongClickListener {
        private var mLogContentSv : HorizontalScrollView ? = null
        private var mContentTv : TextView? = null
        private var mDividerView : View ? = null

        init {
            mLogContentSv = itemView.findViewById(R.id.sv_log_content)
            mContentTv = itemView.findViewById(R.id.tv_content)
            mDividerView = itemView.findViewById(R.id.v_divider)

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

            mLogContentSv!!.setOnTouchListener(this)
        }

        override fun onClick(v: View?) {
            mItemClickListener?.onItemClick(getItem(layoutPosition),layoutPosition)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
           when(event!!.action){
               MotionEvent.ACTION_MOVE -> {
                   //修改动作为ACTION_CANCEL
                   event.action = MotionEvent.ACTION_CANCEL
                   //将事件回传给父控件
                   itemView.onTouchEvent(event)
                   //父控件响应后恢复事件原动作
                   event.action = MotionEvent.ACTION_MOVE
               }
               MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL ->{
                   var info = getItem(layoutPosition) as LogcatInfo
                   var scrollX = mLogContentSv!!.scrollX
                   mScrollXSet.put(info.hashCode(),scrollX)
                   itemView.onTouchEvent(event)
               }
               else->{
                   itemView.onTouchEvent(event)
               }
           }
            return false
        }

        override fun onLongClick(v: View?): Boolean {
            mItemLongClickListener?.onItemLongClick(getItem(layoutPosition),layoutPosition)
            return false
        }

         fun getColorResId(level : String):Int{
             when(level){
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[0] ->{
                    return R.color.sc_logcat_level_verbose_color
                }
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[1] ->{
                     return R.color.sc_logcat_level_debug_color
                 }
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[2] ->{
                     return R.color.sc_logcat_level_info_color
                 }
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[3] ->{
                     return R.color.sc_logcat_level_warn_color
                 }
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[4] ->{
                     return R.color.sc_logcat_level_error_color
                 }
                 mContentTv!!.context.resources.getStringArray(R.array.logcat_level)[5] ->{
                     return R.color.sc_logcat_level_assert_color
                 }
                 else ->{
                     return  R.color.sc_logcat_level_assert_color
                 }
             }
         }

         fun onBindView(info:LogcatInfo , position: Int){
             var content = ""
             if(ScreenUtils.isPortrait()){
                 var log = info.content
                 content = String.format("%s" + LogcatInfo.SPACE + "%d"+"-"+"%d"
                         + LogcatInfo.SPACE + "%s"
                 +(if(log!!.startsWith("\n")) LogcatInfo.SPACE else "\n")
                     + "%s",info.time,info.tag,log)
             }else{
                 content = info.toString()
             }
             var spannable = SpannableString(content)
             if(!mKeyWord.isNullOrEmpty()){
                 //有关键字的情况
                 var index = content.indexOf(mKeyWord)
                 if(index == -1){
                     //没找到关键字,内容和关键字全部转为小写再找
                     index = content.lowercase().indexOf(mKeyWord.lowercase())
                 }
                 //找到了
                 while (index > -1){
                     //高亮开始处为搜索到的位置索引
                     var start = index
                     //结束处为开始+关键词长度
                     var end = index + mKeyWord!!.length
                     spannable.setSpan(BackgroundColorSpan(Color.BLACK),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                     spannable.setSpan(ForegroundColorSpan(Color.WHITE),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                     //从上一次结尾处接着找
                     index = content.indexOf(mKeyWord,end)
                     //如果没找到，转换为小写再找
                     if(index == -1){
                         index = content.lowercase().indexOf(mKeyWord.lowercase(),end)
                     }
                 }
             }else{
                 //没有关键字的情况，并且有报错
                 var matcher = ERROR_CODE_REGEX.matcher(content)
                 if(spannable.isNotEmpty()){
                     while(matcher.find()){
                         //不包含左括号(
                         var start = matcher.start() + "(".length
                         //不包含右括号
                         var end = matcher.end() - ")".length
                         //设置前景颜色
                         spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF999999")),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                         //设置下划线
                         spannable.setSpan(UnderlineSpan(),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                     }
                 }
             }
             mContentTv!!.setText(spannable)
             var colorResId = getColorResId(info.level!!)
             var textColor = -1
             if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                 textColor = mContentTv!!.resources.getColor(colorResId,mContentTv!!.context.theme)
             }else{
                 textColor = mContentTv!!.resources.getColor(colorResId)
             }
             mContentTv!!.setTextColor(colorResId)
             mDividerView!!.visibility = if(position == 0) View.INVISIBLE else View.VISIBLE
             mLogContentSv!!.post(mScrollRunnable)
         }

         /**
         * 滚动任务
         * 作者: ZhouZhengyi
         * 创建时间: 2022/5/2 10:33
         */
         private var mScrollRunnable = Runnable {
            if(layoutPosition < 0 || layoutPosition >= itemCount ){
                return@Runnable
            }
            var info = getItem(layoutPosition)
            var scrollX = mScrollXSet.get(info.hashCode())
            if(mLogContentSv!!.scrollX == scrollX){
                return@Runnable
            }
             mLogContentSv!!.scrollTo(scrollX,0)
         }

         /**
         * ViewHolder 解绑时回调
         * 作者: ZhouZhengyi
         * 创建时间: 2022/5/2 10:36
         */
         fun onDetached(){
           mLogContentSv!!.removeCallbacks(mScrollRunnable)
         }

         /**
         * ViewHolder 回收时回调
         * 作者: ZhouZhengyi
         * 创建时间: 2022/5/2 10:37
         */
         fun onRecycled(){
           mLogContentSv.removeCallbacks(mScrollRunnable)
         }

    }


    /**
    * Item点击监听
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 11:08
    */
    public interface OnItemClickListener{
        fun onItemClick(info:LogcatInfo,position:Int)
    }

    /**
    * Item长按监听
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 11:09
    */
    public interface OnItemLongClickListener{
        fun onItemLongClick(info:LogcatInfo,position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}