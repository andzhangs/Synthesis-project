package com.module.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.huawei.hms.common.ApiException
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.service.AccountAuthService
import com.module.scan.databinding.ActivityMainBinding
import com.module.scan.face.FaceActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private var mService: AccountAuthService? = null


    private lateinit var mScanHelperObserver:ScanHelperObserver

    private val mInputFlow = MutableSharedFlow<String?>()
    private val mInput2Flow = MutableSharedFlow<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        //普通登录华为账号
        fun loginAccount() {
            val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAuthorizationCode()
                .setAccessToken()
                .setIdToken()
                .createParams()
            mService = AccountAuthManager.getService(this@MainActivity, authParams)
            startActivityForResult(mService!!.signInIntent, 8888)
        }

        //静默登录华为账号
        fun loginSilent() {
            val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .createParams()
            mService = AccountAuthManager.getService(this@MainActivity, authParams)
            val task = mService!!.silentSignIn()
            task.addOnSuccessListener {
                //0表示华为帐号、1表示AppTouch帐号
                Log.i(
                    "print_logs", "静默登录: \n" +
                            "账号信息：${it.displayName}\n" +
                            "账号类型：${it.accountFlag}"
                )
            }.addOnFailureListener {
                Log.e("print_logs", "登录失败: ${it.message}")
                Toast.makeText(this, "登录失败：${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        mDataBinding.hwBtnLogin.setOnClickListener {
            loginSilent()
        }


        mScanHelperObserver=ScanHelperObserver(this,object :ScanHelperObserver.OnActivityResultCallback{
            override fun onResult(errorCode:Int,result: HmsScan?) {

            }
        })
        lifecycle.addObserver(mScanHelperObserver)

        mDataBinding.acBtnScan.setOnClickListener {
            mScanHelperObserver.requestPermission()
        }

        mDataBinding.acBtnMakeCode.setOnClickListener {
            mDataBinding.acIvCode.setImageBitmap(mScanHelperObserver.makeCode())
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

        mDataBinding.acBtnFace.setOnClickListener {
            startActivity(Intent(this,FaceActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mScanHelperObserver.requestResult(requestCode, resultCode, data)
        if (requestCode == 8888) {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful) {
                val authAccount = authAccountTask.result
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs", "登录成功：\n" +
                                "AuthorizationCode：\n${authAccount.authorizationCode} \n" +
                                "IDToken：\n${authAccount.idToken} \n" +
                                "AccessToken：\n${authAccount.accessToken}"
                    )
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "登录失败！${authAccountTask.exception}")
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mService?.signOut()?.addOnSuccessListener {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onStop: 退出华为账号")
            }
        }
        mService?.cancelAuthorization()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("print_logs", "onSuccess: 取消授权成功后的处理")
            }else{
                //异常处理
                val exception = it.exception
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    Log.i("print_logs", "onFailure: $statusCode")
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mScanHelperObserver)
        mDataBinding.unbind()
    }
}