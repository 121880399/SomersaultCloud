package org.zzy.slow_method

import com.android.build.gradle.AppExtension
import com.ss.android.ugc.bytex.common.CommonPlugin
import com.ss.android.ugc.bytex.common.visitor.ClassVisitorChain
import org.gradle.api.Project
import org.zzy.slow_method.visitor.SlowMethodClassVisitor

/**
* 慢函数插件入口
* 作者: ZhouZhengyi
* 创建时间: 2021/10/3 10:20
*/
class SlowMethodPlugin : CommonPlugin<SlowMethodExtension, SlowMethodContext> {

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

    override fun transform(relativePath: java.lang.String, chain: ClassVisitorChain): Boolean {
        chain.connect(SlowMethodClassVisitor(extension))
        return super.transform(relativePath, chain)
    }

    private fun isReleaseTask(project: Project):Boolean{
        return project.gradle.startParameter.taskNames.any{
            it.contains("release") || it.contains("Release")
        }
    }
}