package org.zzy.slow_method

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.ss.android.ugc.bytex.common.CommonPlugin
import com.ss.android.ugc.bytex.common.visitor.ClassVisitorChain
import org.gradle.api.Project
import org.zzy.slow_method.visitor.SlowMethodClassVisitor

/**
* 慢函数插件入口
 * 使用ByteX必须创建的类
* 作者: ZhouZhengyi
* 创建时间: 2021/10/3 10:20
*/
class SlowMethodPlugin : CommonPlugin<SlowMethodExtension, SlowMethodContext>() {

    override fun onApply(project: Project) {
        super.onApply(project)
        when{
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature") ->{
                //不是Relase环境才进行以下操作
                if(!isReleaseTask(project)){
                    project.getAndroid<AppExtension>().let{androidExt->
                        extension.somersaultcloud_plugin_switch = project.getProperty("SOMERSAULTCLOUD_PLUGIN_SWITCH",true)
                        extension.slow_method_switch = project.getProperty("SLOW_METHOD_SWITCH",true)
                    }
                }
            }
        }
    }

    override fun getContext(project: Project?, android: AppExtension?, extension: SlowMethodExtension?): SlowMethodContext {
        return SlowMethodContext(project,android,extension)
    }

    /**
     * transform工程中所有的class
     * @param relativePath Class的相对路径
     * @param chain 用于注册自定义ClassVisitor
     * @return 返回true 这个Class文件会正常输出 返回false 这个class文件会被删除
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/23 9:17
     */
    override fun transform(relativePath:String, chain: ClassVisitorChain): Boolean {
        //这里注册我们的ClassVisitor
        chain.connect(SlowMethodClassVisitor(extension))
        return super.transform(relativePath, chain)
    }



    private fun isReleaseTask(project: Project):Boolean{
        return project.gradle.startParameter.taskNames.any{
            it.contains("release") || it.contains("Release")
        }
    }
}

inline fun <reified T : BaseExtension> Project.getAndroid():T = extensions.getByName("android") as T

fun <T> Project.getProperty(name:String,defaultValue:T):T{
    val value = findProperty(name) ?:  return defaultValue
    return when(defaultValue){
        is Boolean -> if(value is Boolean) value as T else value.toString().toBoolean() as T
        is Byte -> if(value is Byte) value as T else value.toString().toByte() as T
        is Short -> if(value is Short) value as T else value.toString().toShort() as T
        is Int -> if(value is Int) value as T else value.toString().toInt() as T
        is Float -> if(value is Float) value as T else value.toString().toFloat() as T
        is Long -> if(value is Long) value as T else value.toString().toLong() as T
        is Double -> if(value is Double) value as T else value.toString().toDouble() as T
        is String -> if(value is String) value as T else value.toString() as T
        else -> value as T
    }
}