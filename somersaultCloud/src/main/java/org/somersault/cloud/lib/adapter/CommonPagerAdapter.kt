package org.somersault.cloud.lib.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import org.somersault.cloud.lib.interf.IPageView

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/19 17:43
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CommonPagerAdapter constructor(): PagerAdapter() {

    var mPagesView : List<IPageView>?  = null

    constructor(pages : List<IPageView>):this(){
        mPagesView = pages
    }

    override fun getCount(): Int =  mPagesView?.size ?: 0

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mPagesView!![position].onCreateView(container))
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mPagesView!![position].onCreateView(container))
        return mPagesView!![position].onCreateView(container)
    }
}