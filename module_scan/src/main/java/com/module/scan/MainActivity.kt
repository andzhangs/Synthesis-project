package com.module.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.huawei.hms.ml.scan.HmsScan
import com.module.scan.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private lateinit var mPermissionObserver:PermissionObserver

    private val mInputFlow = MutableSharedFlow<String?>()
    private val mInput2Flow = MutableSharedFlow<String?>()

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

        mDataBinding.acEtInput.doAfterTextChanged {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "发送数据一: $it")
            }
            lifecycleScope.launch {
                mInputFlow.emit(it?.toString())
            }
        }

        mDataBinding.acEtInput2.doAfterTextChanged {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "发送数据二: $it")
            }
            lifecycleScope.launch {
                mInput2Flow.emit(it?.toString())
            }
        }

        lifecycleScope.launch {
            combine(mInputFlow,mInput2Flow){text1,text2->
                "$text1, $text2"
            }.collectLatest {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "监听数据: $it")
                }
            }
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