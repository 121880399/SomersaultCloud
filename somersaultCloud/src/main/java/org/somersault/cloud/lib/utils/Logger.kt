package org.somersault.cloud.lib.utils

import android.util.Log

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/1/9 10:17
 * 描    述：日志打印类，封转系统Log方法
 * 修订历史：
 * ================================================
 */
class Logger {
    companion object{
        fun i(tag:String,content:String){
            if (Debug.isDebug()){
               Log.i(tag,content)
            }
        }

        fun d(tag:String,content:String){
            if (Debug.isDebug()){
                Log.d(tag,content)
            }
        }

        fun w(tag:String,content:String){
            if (Debug.isDebug()){
                Log.w(tag,content)
            }
        }

        fun e(tag:String,content:String){
            if (Debug.isDebug()){
                Log.e(tag,content)
            }
        }
    }
}