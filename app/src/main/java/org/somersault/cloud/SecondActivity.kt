package org.somersault.cloud

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import org.somersault.cloud.databinding.ActivitySecondBinding

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/5 8:48
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val inflate = ActivitySecondBinding.inflate(LayoutInflater.from(this))
        setContentView(inflate.root)
    }
}