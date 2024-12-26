package com.module.scan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.huawei.hms.ml.scan.HmsScan
import com.module.scan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private lateinit var mPermissionObserver:PermissionObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mPermissionObserver=PermissionObserver(this,object :PermissionObserver.OnActivityResultCallback{
            override fun onResult(errorCode:Int,result: HmsScan?) {

            }
        })
        lifecycle.addObserver(mPermissionObserver)

        mDataBinding.acBtnScan.setOnClickListener {
            mPermissionObserver.requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPermissionObserver.requestResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mPermissionObserver)
        mDataBinding.unbind()
    }
}