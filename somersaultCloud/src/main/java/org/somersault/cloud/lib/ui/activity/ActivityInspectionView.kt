package org.somersault.cloud.lib.ui.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.databinding.ScViewActivityInspectionBinding
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.plugin.ActivityInspectionPlugin
import org.somersault.cloud.lib.ui.BaseFloatView
import org.somersault.cloud.lib.utils.ResUtils
import org.somersault.cloud.lib.utils.ScreenUtils
import org.somersault.cloud.lib.widget.CustomLayoutParams
import org.somersault.cloud.lib.manager.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/2 8:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ActivityInspectionView: BaseFloatView() {

    private var mBinding : ScViewActivityInspectionBinding ? = null

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context): View {
        mBinding = ScViewActivityInspectionBinding.inflate(LayoutInflater.from(context))
        return mBinding!!.root
    }

    /**
     * 对view进行数据填充
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/2 9:13
     */
    override fun onViewCreated(floatView: View) {
        mBinding?.ivClose?.setOnClickListener {
            ActivityInspectionPlugin.isOpen = false
            FloatViewManager.instance.detach(this.mTag)
        }
    }



    override fun onResume() {
        super.onResume()
        val topActivity = ActivityManager.instance.getTopActivity()
        if(topActivity !=null) {
            mBinding?.tvActivityName?.text = ResUtils.getString(R.string.sc_activity_inspection_activity_name,topActivity.javaClass.simpleName)
            val visibleFragment = getVisibleFragment(topActivity)
            if(!TextUtils.isEmpty(visibleFragment)){
                mBinding?.tvFragmentName?.visibility = View.VISIBLE
                mBinding?.tvFragmentName?.text = ResUtils.getString(R.string.sc_activity_inspection_fragment_name,visibleFragment)
            }else{
                mBinding?.tvFragmentName?.visibility = View.GONE
            }
        }else{
            mBinding?.tvActivityName?.text = ""
            mBinding?.tvFragmentName?.visibility = View.GONE
        }
    }

    override fun onStop() {

    }

    override fun initFloatViewLayoutParams(params: CustomLayoutParams) {
       params.width = ViewGroup.LayoutParams.WRAP_CONTENT
       params.height = ViewGroup.LayoutParams.WRAP_CONTENT
       params.x = ScreenUtils.dp2px(30F).toInt()
       params.y = ScreenUtils.dp2px(30F).toInt()
    }

    private fun getVisibleFragment(activity: Activity):String {
        if (activity == null) {
            return ""
        }
        if (activity is AppCompatActivity) {
            val compatActivity = activity as AppCompatActivity
            val supportFragmentManager = compatActivity.supportFragmentManager
            val fragments = supportFragmentManager.fragments
            if (fragments != null && fragments.size > 0) {
                fragments.forEach {
                    if (it.isVisible) {
                        return it.javaClass.simpleName
                    }
                }
            } else {
                return getFragmentForActivity(activity)
            }
        } else {
            return getFragmentForActivity(activity)
        }
        return ""
    }

    private fun getFragmentForActivity(activity: Activity):String{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val fragmentManager = activity.fragmentManager
            val fragments = fragmentManager.fragments
            if(fragments!=null && fragments.size > 0) {
                fragments.forEach {
                    if(it.isVisible){
                        return it.javaClass.simpleName
                    }
                }
            }
        }
        return ""
    }
}