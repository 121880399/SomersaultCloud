package org.somersault.cloud.lib.core.anr.sample

import android.os.Build
import android.text.TextUtils
import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 15:36
 * 描    述：CPU信息采集器
 * 修订历史：
 * ================================================
 */
class CpuSampler:IANRSampleListener {

    private var mSampleResultListener: ISampleResultListener? = null

    override fun doSample(msgId: String, anrTime: Long) {
        TODO("Not yet implemented")
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        mSampleResultListener = resultListener
    }

    /**
     * 获取系统整体状态
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/22 8:10
     */
    private fun getCpuData(msgId: String, anrTime: Long){
        //android O以上，不能访问proc/stat，所以只能调用top命令来获取
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getCpuDataAboveOreo(msgId, anrTime)
        }else{

        }
    }

    /**
     * Android O以上，不能访问proc/stat，所以只能调用top命令来获取
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/22 9:40
     */
    private fun getCpuDataAboveOreo(msgId: String, anrTime: Long){
        var process : Process? = null
        try{
            //得到当前进程号
            val pid = android.os.Process.myPid()
            process = Runtime.getRuntime().exec("top -n 1 -p $pid")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line = ""
            val result = StringBuilder()
            //记录CPU在第几列
            var cpuIndex = -1
            while ((reader.readLine().also { line = it }) != null){
                line = line.trim()
                if(TextUtils.isEmpty(line)){
                    continue
                }

                if(line.contains("cpu")
                    && line.contains("user")
                    && line.contains("sys")){
                    result.append(line)
                }

                var tempIndex = getCPUIndex(line)
                if(tempIndex != -1){
                    //拿到CPU在第几列
                    cpuIndex = tempIndex
                    continue
                }
                //如果是pid开头，说明是当前进程的数据
                if(line.startsWith(pid.toString())){
                    if(cpuIndex == -1){
                        //CPU在第几列没有找到,说明可能没有CPU的数据
                        continue
                    }
                    var param = line.split("\\s+")
                    if(param.size < cpuIndex+1 ){
                        //说明当前进程的数据可能不完成，没有CPU的数据
                        continue
                    }
                    var cpu = param[cpuIndex]
                    if(cpu.endsWith("%")){
                        cpu = cpu.substring(0,cpu.lastIndexOf("%"))
                    }
                    result.append(" $cpu%:app")
                }
            }
            if(mSampleResultListener!= null){
                mSampleResultListener!!.onSampleSuccess(msgId,result.toString(),anrTime)
            }
        }catch (e:Exception){
            if(mSampleResultListener!= null){
                mSampleResultListener!!.onSampleError(e.toString())
            }
        }finally {
            process?.destroy()
        }
    }

    private fun getCPUIndex(line:String):Int{
        if(line.contains("CPU")){
            val titles = line.split("\\s+")
            for(index in titles.indices){
                if(titles[index].contains("CPU")){
                    return index
                }
            }
        }
        return -1
    }

    private fun getSystemStatusBelowOreo() {

    }
}