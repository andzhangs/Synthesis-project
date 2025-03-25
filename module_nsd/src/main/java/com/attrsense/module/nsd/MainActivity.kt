package com.attrsense.module.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private val mNsdManager by lazy {
        getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    private val SERVER_IP = "206.168.2.49"
    private val PORT = 8888
    private lateinit var mContentTv: AppCompatTextView
    private val stringBuffer = StringBuffer()
    private var isServerSending = false
    private var isClientSending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mContentTv = findViewById(R.id.acTv_content)

        findViewById<AppCompatButton>(R.id.acBtn_server).setOnClickListener {
            if (isServerSending) {
                isServerSending = false
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    isServerSending = true
                    loadServer()
                }
            }
        }
        findViewById<AppCompatButton>(R.id.acBtn_client).setOnClickListener {
            if (isClientSending) {
                isClientSending = false
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    isClientSending = true
                    loadClient()
                }
            }
        }
    }

    private fun loadServer() {
        try {
            ServerSocket(PORT).use {
                while (true) {
                    it.accept().use { socket ->
                        Log.i("print_logs", "MainActivity::loadServer: 连接成功 $isServerSending")

                        setContent("Client连接：$isServerSending")

                        while (isServerSending) {  //发送消息
                            //向客户端发送数据
                            val out = PrintWriter(socket.getOutputStream(), true)
                            out.println("我是来自-Service-的数据：${System.currentTimeMillis()}")
                            out.flush()

                            Thread.sleep(1000L)
                            //读取客户端数据
                            val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                            val response = br.readLine()
                            if (response == null) {
                                isServerSending = false
                                socket.close()
                                it.close()
                            } else {
                                setContent("From-Client：$response")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setContent("Server异常：$e")
        } finally {
            Log.d("print_logs", "Server is stop")
        }
    }

    private fun loadClient() {
        try {
            Socket(SERVER_IP, PORT).use { socket ->
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::loadClient: ${socket.isConnected}")
                }
                setContent("Server连接：${socket.isConnected}")

                if (socket.isConnected) {
                    while (isClientSending) {
                        //向服务端发送数据
                        val out = PrintWriter(socket.getOutputStream(), true)
                        out.println("我是来自-Client-的数据：${System.currentTimeMillis()}")
                        out.flush()

                        Thread.sleep(1000L)
                        //读取服务端数据
                        val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                        val response = br.readLine()
                        if (response == null) {
                            isClientSending = false
                            socket.close()
                        } else {
                            setContent("From-Client：$response")
                        }
                    }
                } else {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setContent("Client异常：$e")
        } finally {
            Log.d("print_logs", "Client is stop")
        }
    }

    private fun setContent(content: String) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "setContent: $content")
        }
        runOnUiThread {
            stringBuffer.append(content).append("\n")
            mContentTv.text = stringBuffer.toString()
        }
    }

//    private fun loadServer() {
//        val serviceInfo = NsdServiceInfo().apply {
//            serviceName = ""
//            serviceType = ""
//        }
//        val localPort = 8080 //getLocalPort()
//        serviceInfo.port=localPort
//        serviceInfo.setAttribute("Attribute_UUID","")
//
//        val ipAddress= ""
//        serviceInfo.setAttribute("Attribute_IP",ipAddress)
//        mNsdManager.registerService(serviceInfo,NsdManager.PROTOCOL_DNS_SD,object :NsdManager.RegistrationListener{
//            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
//                //异常上报
//            }
//
//            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
//
//            }
//
//            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
//                //注册成功
//            }
//
//            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
//
//            }
//        })
//    }
//
//    private var mDiscoveryListener : NsdManager.DiscoveryListener? = null
//
//    private fun loadClient(){
//
//        mDiscoveryListener=object :NsdManager.DiscoveryListener{
//            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
//
//            }
//
//            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
//
//            }
//
//            override fun onDiscoveryStarted(serviceType: String?) {
//
//            }
//
//            override fun onDiscoveryStopped(serviceType: String?) {
//
//            }
//
//            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
//                serviceInfo?.let {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                        mNsdManager.registerServiceInfoCallback(it,Executors.newSingleThreadExecutor(),object :NsdManager.ServiceInfoCallback{
//                            override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
//
//                            }
//
//                            override fun onServiceUpdated(serviceInfo: NsdServiceInfo) {
//
//                            }
//
//                            override fun onServiceLost() {
//
//                            }
//
//                            override fun onServiceInfoCallbackUnregistered() {
//
//                            }
//                        })
//                    }else{
//                        mNsdManager.resolveService(it,object :NsdManager.ResolveListener{
//                            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
//
//                            }
//
//                            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    serviceInfo?.attributes?.forEach { (t, u) ->
//                                        Log.i(
//                                            "print_logs",
//                                            "MainActivity::onServiceResolved: "
//                                        )
//                                    }
//                                }
//                            }
//                        })
//                    }
//                }
//            }
//
//            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
//
//            }
//        }
//
//        mNsdManager.discoverServices("",NsdManager.PROTOCOL_DNS_SD,mDiscoveryListener!!)
//
//
//    }

    override fun onStop() {
        super.onStop()
    }
}