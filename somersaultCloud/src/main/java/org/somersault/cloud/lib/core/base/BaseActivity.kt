package org.somersault.cloud.lib.core.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/16 15:19
 * 描    述：kotlin中所有类默认都是final的，想要允许它被继承，
 * 那么需要使用open声明
 * 修订历史：
 * ================================================
 */
open class BaseActivity : AppCompatActivity{

    constructor(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}