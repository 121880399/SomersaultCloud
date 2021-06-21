package org.somersault.cloud.lib.holder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 8:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
class BaseBindingViewHolder<T : ViewBinding>  constructor(val mBinding : T) : RecyclerView.ViewHolder(mBinding.root){
}