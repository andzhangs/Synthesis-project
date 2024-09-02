package com.android.webrtc

import android.content.Context
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnectionFactory.InitializationOptions

/**
 *
 * @author zhangshuai
 * @date 2024/9/2 14:05
 * @description 自定义类描述
 */
object WebRtcManager {


    private var sPeerConnectionFactory: PeerConnectionFactory? = null

    @JvmStatic
    fun init(context: Context) {
        if (sPeerConnectionFactory == null) {
            val initializationOptions = InitializationOptions.builder(context).createInitializationOptions()
            PeerConnectionFactory.initialize(initializationOptions)
            sPeerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()
        }
    }

    @JvmStatic
    fun getPeerConnectionFactory(): PeerConnectionFactory? {
        return sPeerConnectionFactory
    }

}