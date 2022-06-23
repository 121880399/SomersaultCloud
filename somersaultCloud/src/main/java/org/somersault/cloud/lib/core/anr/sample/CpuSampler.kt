package org.somersault.cloud.lib.core.anr.sample

import android.os.Build
import android.text.TextUtils
import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import java.io.BufferedReader
import java.io.FileInputStream
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
class CpuSampler : IANRSampleListener {

    private var mSampleResultListener: ISampleResultListener? = null

    override fun doSample(msgId: String, anrTime: Long) {
        getCpuData(msgId, anrTime)
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        mSampleResultListener = resultListener
    }

    /**
     * 获取系统整体状态
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/22 8:10
     */
    private fun getCpuData(msgId: String, anrTime: Long) {
        //android O以上，不能访问proc/stat，所以只能调用top命令来获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCpuDataAboveOreo(msgId, anrTime)
        } else {
            getCpuDataBelowOreo(msgId, anrTime)
        }
    }

    /**
     * Android O以上，不能访问proc/stat，所以只能调用top命令来获取
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/22 9:40
     */
    private fun getCpuDataAboveOreo(msgId: String, anrTime: Long) {
        var process: Process? = null
        try {
            //得到当前进程号
            val pid = android.os.Process.myPid()
            process = Runtime.getRuntime().exec("top -n 1 -p $pid")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line = ""
            val result = StringBuilder()
            //记录CPU在第几列
            var cpuIndex = -1
            while ((reader.readLine().also { line = it }) != null) {
                line = line.trim()
                if (TextUtils.isEmpty(line)) {
                    continue
                }

                if (line.contains("cpu")
                    && line.contains("user")
                    && line.contains("sys")
                ) {
                    result.append(line)
                }

                var tempIndex = getCPUIndex(line)
                if (tempIndex != -1) {
                    //拿到CPU在第几列
                    cpuIndex = tempIndex
                    continue
                }
                //如果是pid开头，说明是当前进程的数据
                if (line.startsWith(pid.toString())) {
                    if (cpuIndex == -1) {
                        //CPU在第几列没有找到,说明可能没有CPU的数据
                        continue
                    }
                    var param = line.split("\\s+")
                    if (param.size < cpuIndex + 1) {
                        //说明当前进程的数据可能不完成，没有CPU的数据
                        continue
                    }
                    var cpu = param[cpuIndex]
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"))
                    }
                    result.append(" $cpu%:app")
                }
            }
            if (mSampleResultListener != null) {
                mSampleResultListener!!.onSampleSuccess(msgId, result.toString(), anrTime)
            }
        } catch (e: Exception) {
            if (mSampleResultListener != null) {
                mSampleResultListener!!.onSampleError(e.toString())
            }
        } finally {
            process?.destroy()
        }
    }

    private fun getCPUIndex(line: String): Int {
        if (line.contains("CPU")) {
            val titles = line.split("\\s+")
            for (index in titles.indices) {
                if (titles[index].contains("CPU")) {
                    return index
                }
            }
        }
        return -1
    }


    /**
     * android O以下使用proc/stat中的数据来获取整体CPU的状态
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/23 8:36
     */
    private fun getCpuDataBelowOreo(msgId: String, anrTime: Long) {
        var cpuReader: BufferedReader? = null
        var pidReader: BufferedReader? = null
        try {
            cpuReader = BufferedReader(InputStreamReader(FileInputStream("/proc/stat")), 1024)
            var cpuRate = cpuReader.readLine()
            if (cpuRate == null) {
                cpuRate = ""
            }
            //得到当前进程号
            val pid = android.os.Process.myPid()
            pidReader = BufferedReader(InputStreamReader(FileInputStream("/proc/$pid/stat")), 1024)
            var pidRate = pidReader.readLine()
            if (pidRate == null) {
                pidRate = ""
            }
            val result = parse(cpuRate,pidRate)
            if(mSampleResultListener != null){
                mSampleResultListener!!.onSampleSuccess(msgId,result,anrTime)
            }
        }catch (e:Exception) {
            e.printStackTrace()
            if(mSampleResultListener != null) {
                mSampleResultListener!!.onSampleError(e.toString())
            }
        }finally {
            cpuReader?.close()
            pidReader?.close()
        }
    }

    /**
     * 不同内核版本/proc/stat文件格式不大一致。/proc/stat文件中第一行为总的cpu使用情况。
     *各个版本都有的4个字段: user、nice、system、idle
     *2.5.41版本新增字段：iowait
     *2.6.0-test4新增字段：irq、softirq
     * 2.6.11新增字段：stealstolen ：which is the time spent in other operating systems when running in a virtualized environment
     *2.6.24新增字段：guest:which is the time spent running a virtual  CPU  for  guest operating systems under the control of the Linux kernel
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/23 9:31
     */
    private fun parse(cpuRate: String, pidRate: String): String {
       val cpuInfoParam = cpuRate.split("\\s+")
        val result = StringBuilder()
        if(cpuInfoParam.size < 9){
            return ""
        }
        //得到CPU核数
        val cpuCount =Runtime.getRuntime().availableProcessors()

        // TODO: 这里需要测试数据的准确性，看是否取错了值
        //从2开始是因为CPU后面有一个空格
        // 从系统启动开始累计到当前时刻，处于用户态的运行时间，不包含 nice值为负进程。
        val cpuUser = cpuInfoParam[2].toLong()
        // 从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间
        val nice = cpuInfoParam[3].toLong()
        //从系统启动开始累计到当前时刻，处于核心态的运行时间
        val cpuSystem = cpuInfoParam[4].toLong()
        //从系统启动开始累计到当前时刻，除IO等待时间以外的其它等待时间iowait
        val idle = cpuInfoParam[5].toLong()
        //从系统启动开始累计到当前时刻，IO等待时间
        val ioWait = cpuInfoParam[6].toLong()
        //从系统启动开始累计到当前时刻，硬中断时间
        val irq = cpuInfoParam[7].toLong()
        //从系统启动开始累计到当前时刻，软中断时间
        val softIrq = cpuInfoParam[8].toLong()
        val total = cpuUser + nice + cpuSystem + idle + ioWait + irq + softIrq

        result.append("cpu:").append((total-idle)/total*100).append("% ")
            .append("user:").append(cpuUser/total*100).append("% ")
            .append("system:").append(cpuSystem/total*100).append("% ")
            .append("ioWait:").append(ioWait/total*100).append("% ")
            .append("irq:").append(irq/total*100).append("% ")
            .append("softIrq:").append(softIrq/total*100).append("% ")

        val pidInfoParam = pidRate.split("\\s+")
        if(pidInfoParam.size < 17){
            return result.toString()
        }
        //该任务在用户态运行的时间，单位为jiffies
        val utime = pidInfoParam[13].toLong()
        //该任务在核心态运行的时间，单位为jiffies
        val stime = pidInfoParam[14].toLong()
        //所有已死线程在用户态运行的时间，单位为jiffies
        val cutime = pidInfoParam[15].toLong()
        //所有已死在核心态运行的时间，单位为jiffies
        val cstime = pidInfoParam[16].toLong()

        val appCpuTime = utime + stime + cutime + cstime

        result.append("appCpu:").append(appCpuTime/total*100*cpuCount).append("% ")

        return result.toString()
    }
}