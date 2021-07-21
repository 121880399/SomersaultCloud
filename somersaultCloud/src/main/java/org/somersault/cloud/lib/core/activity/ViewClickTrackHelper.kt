package org.somersault.cloud.lib.core.activity

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.somersault.cloud.lib.been.Operation
import org.somersault.cloud.lib.manager.OperationPathManager
import org.somersault.cloud.lib.utils.AopUtils

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/13 11:59
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ViewClickTrackHelper {


///////////////////////////////////////////////Fragment 专区//////////////////////////////////////////////////////////////
    /**
     * Hook Fragment onViewCreated
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/20 9:12
     */
    fun onFragmentViewCreated(`object`: Any, rootView: View, bundle: Bundle?) {
        try {
            if (!isFragment(`object`)) {
                return
            }
            val fragmentName =  `object`.javaClass.simpleName
            val operation =  Operation("$fragmentName:onViewCreated()")
            OperationPathManager.instance.addOperation(operation)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    /**
     * Hook Fragment onResume
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/20 9:13
     */
    fun trackFragmentResume(`object`: Any) {
        try {
            if (!isFragment(`object`)) {
                return
            }
            val fragmentName =  `object`.javaClass.simpleName
            val operation =  Operation("$fragmentName:onResume()")
            OperationPathManager.instance.addOperation(operation)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    /**
     * Fragment+ViewPager架构
     * 需要使用setUserVisibleHint 判断是否可见
     * tab每切换一次调用一次setUserVisibleHint
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/20 9:20
     */
    fun trackFragmentSetUserVisibleHint(`object`: Any, isVisibleToUser: Boolean) {
        // TODO: 2021/7/20 这里需要验证Fragment名字是否正确
        try {
            if (!isFragment(`object`)) {
                return
            }
            val fragmentName =  `object`.javaClass.simpleName
            val operation =  Operation("$fragmentName:setUserVisibleHint($isVisibleToUser)")
            OperationPathManager.instance.addOperation(operation)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    /**
     * FragmentManager中进行hide或者show某个fragment的形式切换时候会被调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/20 9:30
     */
    fun trackOnHiddenChanged(`object`: Any, hidden: Boolean) {
        try {
            if (!isFragment(`object`)) {
                return
            }
            val fragmentName =  `object`.javaClass.simpleName
            val operation =  Operation("$fragmentName:setUserVisibleHint($hidden)")
            OperationPathManager.instance.addOperation(operation)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun isFragment(`object`: Any):Boolean{
        //思路：通过反射拿到Fragment类，然后通过isInstance方法判断
        if(`object` == null){
            return false
        }
        var supportFragmentClass: Class<*>? = null
        var androidXFragmentClass: Class<*>? = null
        var fragment: Class<*>? = null
        //普通
        try {
            fragment = Class.forName("android.app.Fragment")
        } catch (e: Exception) {
            //ignored
        }
        //v4版本
        try {
            supportFragmentClass = Class.forName("android.support.v4.app.Fragment")
        } catch (e: Exception) {
            //ignored
        }

        //androidx版本
        try {
            androidXFragmentClass = Class.forName("androidx.fragment.app.Fragment")
        } catch (e: Exception) {
            //ignored
        }

        if (supportFragmentClass == null && androidXFragmentClass == null && fragment == null) {
            return false
        }

        if (supportFragmentClass != null && supportFragmentClass.isInstance(`object`) ||
            androidXFragmentClass != null && androidXFragmentClass.isInstance(`object`) ||
            fragment != null && fragment.isInstance(`object`)
        ) {
            return true
        }

        return false
    }

///////////////////////////////////////////////Fragment 专区//////////////////////////////////////////////////////////////




    /**
     * 点击onGroupClick方法时Hook
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/21 8:19
     */
    fun trackExpandableListViewOnGroupClick(
        expandableListView: ExpandableListView?, view: View?,
        groupPosition: Int
    ) {
        addOperation("onGroupClick",view!!)
    }

    /**
     * 点击onChildClick方法时Hook
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/21 8:36
     */
    fun trackExpandableListViewOnChildClick(
        expandableListView: ExpandableListView?, view: View?,
        groupPosition: Int, childPosition: Int
    ) {
        addOperation("onChildClick",view!!)
    }

    /**
     * onTabChanged时hook
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/21 8:43
     */
    fun trackTabHost(tabName: String?) {
        addOnClickOperation(tabName!!)
    }

    /**
     * onTabSelected时hook
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/21 8:44
     */
    fun trackTabLayoutSelected(`object`: Any, tab: Any?) {

    }

    fun trackMenuItem(menuItem: MenuItem?) {

    }

    fun trackMenuItem(`object`: Any?, menuItem: MenuItem?) {

    }

    fun trackRadioGroup(view: RadioGroup?, checkedId: Int) {

    }

    fun trackDialog(dialogInterface: DialogInterface?, whichButton: Int) {

    }

    /**
     * onItemClick时Hook
     * ListView,GridView,Spinner的onItemClick
     * 都会走这个方法
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/21 9:17
     */
    fun trackListView(adapterView: AdapterView<*>, view: View?, position: Int) {
        try{
            if(view == null){
                return
            }
            //获取所在的 Context
            val context = view.context ?: return
            val operationBuilder = StringBuilder()
            when (view) {
                is ListView -> {
                    operationBuilder.append("ListView")
                }
                is GridView -> {
                    operationBuilder.append("GridView")
                }
                is Spinner -> {
                    operationBuilder.append("Spinner")
                }
            }
            val viewId = AopUtils.getViewId(view)
            if(!TextUtils.isEmpty(viewId)){
                operationBuilder.append(":$viewId")
            }

            var viewText: String? = null
            if (view is ViewGroup) {
                try {
                    val stringBuilder = StringBuilder()
                    viewText = AopUtils.traverseView(stringBuilder, view as ViewGroup?)
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length - 1)
                    }
                } catch (e: java.lang.Exception) {
                   e.printStackTrace()
                }
            } else {
                viewText = AopUtils.getViewText(view)
            }

            if(!TextUtils.isEmpty(viewText)){
                operationBuilder.append(":$viewText")
            }
            val operation = Operation(operationBuilder.toString())
            OperationPathManager.instance.addOperation(operation)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun trackDrawerOpened(view: View?) {

    }

    fun trackDrawerClosed(view: View?) {

    }

    fun trackViewOnClick(view: View?) {

    }

    fun trackViewOnClick(view: View?, isFromUser: Boolean) {

    }

    fun track(eventName: String?, properties: String?) {

    }

    fun showChannelDebugActiveDialog(activity: Activity?) {

    }

    fun loadUrl(webView: View?, url: String?) {

    }

    fun loadUrl2(webView: View?, url: String?) {

    }

    fun loadUrl(webView: View?, url: String?, additionalHttpHeaders: Map<String?, String?>?) {

    }

    fun loadUrl2(webView: View?, url: String?, additionalHttpHeaders: Map<String?, String?>?) {

    }

    fun loadData(webView: View?, data: String?, mimeType: String?, encoding: String?) {

    }

    fun loadData2(webView: View?, data: String?, mimeType: String?, encoding: String?) {

    }

    fun loadDataWithBaseURL(
        webView: View?,
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {

    }

    fun loadDataWithBaseURL2(
        webView: View?,
        baseUrl: String?,
        data: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {

    }

    fun postUrl(webView: View?, url: String?, postData: ByteArray?) {

    }

    fun postUrl2(webView: View?, url: String?, postData: ByteArray?) {

    }



    fun addWebViewVisualInterface(webView: View?) {

    }

    private fun addOperation(name:String,view: View){
        val resourceEntryName = view?.resources?.getResourceEntryName(view!!.id)
        val operation = Operation("$resourceEntryName:$name")
        OperationPathManager.instance.addOperation(operation)
    }

    private fun addOnClickOperation(name:String){
        val operation = Operation("$name:onClick")
        OperationPathManager.instance.addOperation(operation)
    }

}