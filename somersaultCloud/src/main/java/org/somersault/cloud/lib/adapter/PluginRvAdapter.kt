package org.somersault.cloud.lib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.somersault.cloud.lib.databinding.ItemPluginBinding
import org.somersault.cloud.lib.holder.BaseBindingViewHolder
import org.somersault.cloud.lib.interf.IFunctionPlugin

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 8:20
 * 描    述：
 * 修订历史：
 * ================================================
 */
class PluginRvAdapter constructor() : RecyclerView.Adapter<BaseBindingViewHolder<ItemPluginBinding>>() {

    private var plugins: List<IFunctionPlugin>? = null

    constructor(plugins: List<IFunctionPlugin>) : this() {
        this.plugins = plugins
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemPluginBinding> {
        val inflate = ItemPluginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseBindingViewHolder<ItemPluginBinding>(inflate)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ItemPluginBinding>, position: Int) {
        var plugin = plugins?.get(position)
        holder.mBinding.ivIcon.setImageResource(plugin!!.getIconResId())
        holder.mBinding.tvTitle.text = plugin!!.getTitle()
    }

    override fun getItemCount(): Int {
        return if(plugins == null) 0 else plugins!!.size
    }
}