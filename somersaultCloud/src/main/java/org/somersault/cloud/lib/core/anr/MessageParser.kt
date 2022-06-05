package org.somersault.cloud.lib.core.anr

import android.text.TextUtils
import org.somersault.cloud.lib.core.anr.bean.MonitorMessage
import java.util.regex.Pattern

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/5 11:33
 * 描    述：消息解析器，主要将Looper中Printer打印的日志
 * 转换为MonitorMessage对象
 * 修订历史：
 * ================================================
 */
class MessageParser {

    companion object {

         const val FRAME_HANDLER  = "android.view.Choreographer\$FrameHandler"

         const val FRAME_INVOKE  = "android.view.Choreographer\$FrameDisplayEventReceiver"

         const val ACTIVITY_THREAD_HANDLER  = "android.app.ActivityThread\$H"

        /**
         * 用来匹配handler的名称
         * 作者:ZhouZhengyi
         * 创建时间: 2022/6/5 12:01
         */
        private val HANDLER_NAME_PATTERN = "(?<=\\()\\S+(?=\\))"

        /**
         * 用来匹配Handler的内存地址
         * 作者:ZhouZhengyi
         * 创建时间: 2022/6/5 12:06
         */
        private val HANDLER_ADDRESS_PATTERN = "(?<=\\{)[^}]*(?=\\})"

        /**
         * 解析 logging.println(">>>>> Dispatching to " + msg.target + " " +
         * msg.callback + ": " + msg.what);
         * 示例：
         * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3332d43} org.somersault.cloud.MainActivity$2@34550: 0
         * 作者:ZhouZhengyi
         * 创建时间: 2022/6/5 11:35
         */
        fun parsePrinterStart(log: String): MonitorMessage {
            try {
                //去掉空格
                var content = log.trim()
                //通过:分割字符串
                var splitContent = content.split(":")
                if (splitContent.size != 2) {
                    throw IllegalArgumentException("log format error")
                }
                //拿到what值
                val what = splitContent[1].trim().toInt()
                //将第0部分的字符串再次进行分割,以{3332d43}进行分割
                splitContent = splitContent[0].split("\\{.*\\}")
                if (splitContent.size != 2) {
                    throw IllegalArgumentException("log format error")
                }
                //org.somersault.cloud.MainActivity$2@34550
                val callbackName = splitContent[1].trim()
                //通过正则(?<=\()\S+(?=\)) 获取Handler名字
                var pattern = Pattern.compile(HANDLER_NAME_PATTERN)
                var match = pattern.matcher(splitContent[0].trim())
                var handlerName = ""
                if (match.find()) {
                    var handlerName = match.group()
                }
                //通过正则(?<=\{)[^}]*(?=\}) 获取Handler的内存地址
                pattern = Pattern.compile(HANDLER_ADDRESS_PATTERN)
                match = pattern.matcher(content)
                var handlerAddress = ""
                if (match.find()) {
                    var handlerAddress = match.group()
                }
                return MonitorMessage(handlerName, handlerAddress, callbackName, what)
            } catch (e: Exception) {
                e.printStackTrace()
                return MonitorMessage("", "", "", 0)
            }
        }

        fun isDoFrame(msg : MonitorMessage) : Boolean {
            return msg != null && TextUtils.equals(FRAME_HANDLER,msg.handlerName) && msg.callBackName.contains(FRAME_INVOKE)
        }

        fun isActivityThreadMsg(msg:MonitorMessage) : Boolean {
            return msg != null && TextUtils.equals(ACTIVITY_THREAD_HANDLER,msg.handlerName)
        }


    }


}