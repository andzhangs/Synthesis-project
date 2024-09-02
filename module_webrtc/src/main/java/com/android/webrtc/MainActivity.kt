package com.android.webrtc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.webrtc.databinding.ActivityMainBinding
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection

class MainActivity : AppCompatActivity(), WebRtcClient.PeerConnectionObserver {

    private lateinit var mDataBinding: ActivityMainBinding
    private var mWebRtcClient: WebRtcClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this
        WebRtcManager.init(this.applicationContext)

        mDataBinding.acBtnConnect.setOnClickListener {
            mWebRtcClient = WebRtcClient(this).apply {
                createPeerConnection()
                createDataChannel()
            }
        }
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {

    }

    override fun onAddStream(mediaStream: MediaStream) {

    }

    override fun onRemoveStream(mediaStream: MediaStream) {

    }

    override fun onDataChannel(dataChannel: DataChannel) {

    }

    override fun onRenegotiationNeeded() {

    }

    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mWebRtcClient?.disconnect()
        mDataBinding.unbind()
    }
}