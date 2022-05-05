package org.somersault.cloud.lib.core.log

import java.util.regex.Pattern

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/4/30 10:12
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LogcatInfo private constructor(){


    /**
    * 时间
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:24
    */
    var time : String? = null

    /**
    * 等级
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:25
    */
    var level : String? = null

    /**
    * 标签
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:25
    */
    var tag : String? = null

    /**
    * 日志内容
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:25
    */
    var content : String ? = null

    /**
    * 进程ID
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:25
    */
    var pid : Int ? = null

    /**
    * 线程ID
    * 作者: ZhouZhengyi
    * 创建时间: 2022/4/30 12:47
    */
    var tid : Int ? = null

    companion object{

        val SPACE = " "

        val LINE_SPACE = "\n"+ SPACE

        private val PATTERN : Pattern = Pattern.compile("([0-9^-]+-[0-9^ ]+\\s[0-9^:]+:[0-9^:]+\\.[0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s([VDIWEF])\\s([^\\s]*)\\s*:\\s(.*)")

        fun getLocatInfo(line :String):LogcatInfo?{
            var matcher = PATTERN.matcher(line)
            //判断日志是否符合标准
            if(!matcher.find()){
                return null
            }

            var info = LogcatInfo()
            info.time = matcher.group(1)
            info.pid = matcher.group(2).toInt()
            info.tid = matcher.group(3).toInt()
            info.level = matcher.group(4)
            info.tag = matcher.group(5)
            info.content = matcher.group(6)
            return info
        }
    }

    /**
    * 追加日志
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/1 10:12
    */
    fun appendLog(log:String){
        content = (if(content!!.startsWith(LINE_SPACE))  "" else LINE_SPACE) + content + LINE_SPACE + log
    }

    override fun toString(): String {
        return String.format("%s$SPACE%d-%d$SPACE%s$SPACE%s",time,pid,tid,tag,content)
    }
}