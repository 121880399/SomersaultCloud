package org.somersault.cloud.lib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import org.somersault.cloud.lib.databinding.ScItemPluginBinding
import org.somersault.cloud.lib.holder.BaseBindingViewHolder
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.widget.sheet.listener.SingleClickListener

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 8:20
 * 描    述：
 * 修订历史：
 * ================================================
 */
class PluginRvAdapter constructor() : RecyclerView.Adapter<BaseBindingViewHolder<ScItemPluginBinding>>() {

    private var plugins: List<IFunctionPlugin>? = null

    private var mOnItemClickListener : AdapterView.OnItemClickListener? = null

    constructor(plugins: List<IFunctionPlugin>) : this() {
        this.plugins = plugins
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ScItemPluginBinding> {
        val inflate = ScItemPluginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseBindingViewHolder<ScItemPluginBinding>(inflate)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder<ScItemPluginBinding>, position: Int) {
        holder.mBinding.root.setOnClickListener(mClickListener)
        holder.mBinding.root.tag = position
        var plugin = plugins?.get(position)
        holder.mBinding.ivIcon.setImageResource(plugin!!.getIconResId())
        holder.mBinding.tvTitle.text = plugin!!.getTitle()
    }

    override fun getItemCount(): Int {
        return if(plugins == null) 0 else plugins!!.size
    }

    fun setOnItemClickListener(onItemClickListener:AdapterView.OnItemClickListener){
        this.mOnItemClickListener = onItemClickListener
    }

    private val mClickListener = SingleClickListener(View.OnClickListener {
        val position = it.getTag()
        if(mOnItemClickListener!=null){
            mOnItemClickListener!!.onItemClick(null,it, position as Int, position as Long)
        }
    })
}