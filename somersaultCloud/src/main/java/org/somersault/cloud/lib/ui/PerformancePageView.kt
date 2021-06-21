package org.somersault.cloud.lib.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import org.somersault.cloud.lib.databinding.LayoutFunctionBinding
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.interf.IPageView
import org.zzy.somersault.cloud.lib.SomersaultCloud

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 7:30
 * 描    述：
 * 修订历史：
 * ================================================
 */
class PerformancePageView : IPageView{

    private var mBinding : LayoutFunctionBinding? = null

    private var mPlugins : ArrayList<IFunctionPlugin>? = null

    override fun onCreateView(container: ViewGroup): View {
        if(mBinding == null){
            mBinding = LayoutFunctionBinding.inflate(LayoutInflater.from(container.context),container,false)
            onViewCreate(mBinding!!.root)
        }
        return mBinding!!.root
    }

    fun onViewCreate(view : View){
        mPlugins = SomersaultCloud.instance.getPerformancePlugins()
        if(mPlugins == null || mPlugins!!.isEmpty()){
            // TODO: 2021/6/20 显示空页面
            return
        }
        mBinding!!.rvFunction.layoutManager = GridLayoutManager(view.context,3)
        mBinding!!.rvFunction.setHasFixedSize(true)
    }
}