package org.somersault.cloud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import org.somersault.cloud.databinding.ActivityMainBinding
import org.somersault.cloud.lib.SomersaultCloud

class MainActivity : AppCompatActivity() {

    private var mBinding : ActivityMainBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding!!.root)
        mBinding?.btnFloat?.setOnClickListener {
            SomersaultCloud.instance.show(this)
        }
        mBinding?.btnJump?.setOnClickListener {
            val intent = Intent(
               this,
                SecondActivity::class.java
            )
            startActivity(intent)
        }
        Log.d("somersault","MainActivity onCreate")
    }

    override fun onStop() {
        super.onStop()
        Log.d("somersault","MainActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("somersault","MainActivity onDestroy")
    }
}