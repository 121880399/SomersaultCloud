package org.somersault.cloud.lib.utils

import android.os.Environment

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/23 8:20
 * 描    述：
 * 修订历史：
 * ================================================
 */
object FileUtils {

    /**
     * 判断外部存储是否可写
     * 作者: ZhouZhengyi
     * 创建时间: 2022/5/23 8:22
     */
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            return true
        }
        return false
    }

    /**
    * 判断外部存储当前是否可读
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/23 8:25
    */
    fun isExternalStorageReadable():Boolean{
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state
            || Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            return true
        }
        return false
    }

}