package org.somersault.cloud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import org.somersault.cloud.R
import org.somersault.cloud.databinding.ActivityMainBinding
import org.zzy.somersault.cloud.lib.SomersaultCloud

class MainActivity : AppCompatActivity() {

    private var mBinding : ActivityMainBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding!!.root)
        mBinding?.btnFloat?.setOnClickListener {
            SomersaultCloud.instance.show(this)
        }
    }
}