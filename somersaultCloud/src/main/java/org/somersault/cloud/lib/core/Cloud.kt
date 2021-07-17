package org.somersault.cloud.lib.core

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.interf.ICloud
import org.somersault.cloud.lib.manager.PanelManager
import org.somersault.cloud.lib.utils.ScreenUtils
import org.somersault.cloud.lib.widget.floatview.CloudCoordinator
import org.somersault.cloud.lib.widget.floatview.CloudTrashLayout
import org.somersault.cloud.lib.widget.floatview.CloudView
import org.somersault.cloud.lib.manager.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/23 8:49
 * 描    述：筋斗云浮窗管理器
 * 修订历史：
 * ================================================
 */
class Cloud private constructor() : ICloud{

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = Cloud()
    }

    private var mCloudView : CloudView?  = null

    private var mTrashLayout : CloudTrashLayout? = null

    private var mCoordinatorLayout : CloudCoordinator? = null

    private var mPanelManager : PanelManager ? = null

    private var mContainer : FrameLayout ?  = null

    init {
        mPanelManager = PanelManager()
    }

    override fun add(context: Context): ICloud {
       initView(context)
        return this
    }

    override fun remove(): ICloud {
        if(ViewCompat.isAttachedToWindow(mCloudView!!) && mContainer!=null){
            mContainer!!.removeView(mCloudView)
        }
        mCloudView = null
        mTrashLayout = null
        mCoordinatorLayout = null
        return this
    }

    override fun attach(activity: Activity): ICloud {
        attach(ActivityManager.instance.getRootView(activity))
        return this
    }

    override fun attach(container: FrameLayout?): ICloud {
        if(container == null || mCloudView ==null){
            //这里有可能存在container不为空，mCloudView为空的情况
            //所以当container不为空的时候，就先记录
            mContainer = container
            return this
        }
        if(mCloudView?.parent == container){
            return this
        }
        removeView(mTrashLayout!!)
        removeView(mCloudView!!)

        mContainer = container

        addViewToWindow(mTrashLayout!!)
        addViewToWindow(mCloudView!!)
        return this
    }

    override fun detach(activity: Activity): ICloud {
        detach(ActivityManager.instance.getRootView(activity))
        return this
    }

    override fun detach(container: FrameLayout?): ICloud {
        if(mCloudView != null && container!=null && ViewCompat.isAttachedToWindow(mCloudView!!)){
            container.removeView(mCloudView)
            container.removeView(mTrashLayout)
        }
        if(mContainer == container){
            mContainer = null
        }
        return this
    }

    private fun initView(context:Context){
        if(mCloudView!=null){
            return
        }
        initTrash(context)
        initFloatView(context)
        addViewToWindow(mCloudView!!)
    }

    private fun initTrash(context: Context){
        mTrashLayout = CloudTrashLayout(context)
        mTrashLayout!!.visibility = View.GONE
        LayoutInflater.from(context).inflate(R.layout.sc_layout_trash,mTrashLayout,true)
        addViewToWindow(mTrashLayout!!)
        initCoordinator()
    }

    private fun initFloatView(context: Context){
        mCloudView = CloudView(context)
        mCloudView?.setOnRemoveListener {
            mPanelManager?.dismiss()
        }
        mCloudView?.setOnClickListener {
            if(mContainer == null){
                return@setOnClickListener
            }
            mPanelManager?.setupPanel(mContainer!!)
            mPanelManager?.toggle()
        }
        mCloudView?.setShouldStickToWall(true)
        mCloudView?.setLayoutCoordinator(mCoordinatorLayout)
        mCloudView?.layoutParams = getLayoutParams()
    }

    private fun getLayoutParams() : FrameLayout.LayoutParams{
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.setMargins(
            params.leftMargin,
            params.topMargin,
            (-ScreenUtils.dp2px(20F)).toInt(),
            ScreenUtils.getScreenHeight() /3
        )
        return params
    }

    private fun addViewToWindow(view:View){
        if(mContainer == null){
            return
        }
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        mContainer?.addView(view)
    }

    private fun initCoordinator(){
        mCoordinatorLayout = CloudCoordinator.Builder()
            .setTrashView(mTrashLayout)
            .build()
    }

    private fun removeView(view: View){
        if(mContainer != null && view.parent == mContainer){
            mContainer?.removeView(view)
        }
    }

    fun dismiss(){
        mPanelManager?.dismiss()
    }
}