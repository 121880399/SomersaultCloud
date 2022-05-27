package org.somersault.cloud.lib.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.GridLayoutManager
import org.somersault.cloud.lib.adapter.PluginRvAdapter
import org.somersault.cloud.lib.databinding.ScLayoutFunctionBinding
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.interf.IPageView
import org.somersault.cloud.lib.SomersaultCloud

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 7:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
class AppInfoPageView : IPageView {

    private var mBinding : ScLayoutFunctionBinding? = null

    private var mPlugins : ArrayList<IFunctionPlugin>? = null

    private var mAdapter : PluginRvAdapter? = null

    override fun onCreateView(container: ViewGroup): View {
        if(mBinding == null){
            mBinding = ScLayoutFunctionBinding.inflate(LayoutInflater.from(container.context),container,false)
            onViewCreate(mBinding!!.root)
        }
        return mBinding!!.root
    }

    private fun onViewCreate(view : View){
        mPlugins = SomersaultCloud.getAppInfoPlugins()
        mBinding!!.tvTitle.text = "App信息"
        if(mPlugins == null || mPlugins!!.isEmpty()){
            // TODO: 2021/6/20 显示空页面
            return
        }
        mBinding!!.rvFunction.layoutManager = GridLayoutManager(view.context,3)
        mBinding!!.rvFunction.setHasFixedSize(true)
        mAdapter = PluginRvAdapter(mPlugins!!)
        mAdapter!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            if(mPlugins!=null && mPlugins!!.size > 0){
                mPlugins!![position].onClick()
            }
        })
        mBinding!!.rvFunction.adapter = mAdapter
    }
}