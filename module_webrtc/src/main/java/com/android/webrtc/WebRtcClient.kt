package com.android.webrtc

import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 *
 * @author zhangshuai
 * @date 2024/9/2 14:10
 * @description 自定义类描述
 */
class WebRtcClient(private val mPeerConnectionObserver: PeerConnectionObserver) {

    interface PeerConnectionObserver {
        fun onIceCandidate(iceCandidate: IceCandidate)

        fun onAddStream(mediaStream: MediaStream)

        fun onRemoveStream(mediaStream: MediaStream)

        fun onDataChannel(dataChannel: DataChannel)

        fun onRenegotiationNeeded()

        fun onSignalingChange(signalingState: PeerConnection.SignalingState)
    }


    private val mPeerConnectionFactory: PeerConnectionFactory? =
        WebRtcManager.getPeerConnectionFactory()

    private val iceServers = mutableListOf<IceServer>().apply {
        add(
            IceServer.builder("stun:stun.l.google.com:19302").setPassword("123456")
                .createIceServer()
        )
    }
    private var mPeerConnection: PeerConnection? = null
    private var mDataChannel: DataChannel? = null
    private val mObserver by lazy { PeerConnectionObserverImpl() }

    fun createPeerConnection() {
        try {
            val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
            mPeerConnection = mPeerConnectionFactory?.createPeerConnection(
                rtcConfig,
                mObserver
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createDataChannel() {
        mDataChannel = mPeerConnection?.createDataChannel("dataChannel", DataChannel.Init())
    }

    fun setLocationDescription(sessionDescription: SessionDescription) {
        mPeerConnection?.setLocalDescription(mObserver, sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        mPeerConnection?.addIceCandidate(iceCandidate)
    }

    fun disconnect() {
        mDataChannel?.close()
        mDataChannel = null

        mPeerConnection?.close()
        mPeerConnection = null
    }

    private inner class PeerConnectionObserverImpl : PeerConnection.Observer, SdpObserver {
        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onSignalingChange: ")
            }
            p0?.also {
                mPeerConnectionObserver.onSignalingChange(it)
            }
        }

        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onIceConnectionChange: ")
            }
        }

        override fun onIceConnectionReceivingChange(p0: Boolean) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onIceConnectionReceivingChange: ")
            }
        }

        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onIceGatheringChange: ")
            }
        }

        override fun onIceCandidate(p0: IceCandidate?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onIceCandidate: ")
            }
            p0?.also {
                mPeerConnectionObserver.onIceCandidate(it)
            }
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onIceCandidatesRemoved: ")
            }
        }

        override fun onAddStream(p0: MediaStream?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onAddStream: ")
            }
            p0?.also {
                mPeerConnectionObserver.onAddStream(it)
            }
        }

        override fun onRemoveStream(p0: MediaStream?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onRemoveStream: ")
            }
            p0?.also {
                mPeerConnectionObserver.onRemoveStream(it)
            }
        }

        override fun onDataChannel(p0: DataChannel?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onDataChannel: ")
            }
            p0?.also {
                mPeerConnectionObserver.onDataChannel(it)
            }
        }

        override fun onRenegotiationNeeded() {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onRenegotiationNeeded: ")
            }
            mPeerConnectionObserver.onRenegotiationNeeded()
        }

        override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onAddTrack: ")
            }
        }

        override fun onCreateSuccess(p0: SessionDescription?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onCreateSuccess: ")
            }
            p0?.also {
                setLocationDescription(it)
            }
        }

        override fun onSetSuccess() {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onSetSuccess: ")
            }
        }

        override fun onCreateFailure(p0: String?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onCreateFailure: ")
            }
        }

        override fun onSetFailure(p0: String?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "PeerConnectionObserverImpl::onSetFailure: ")
            }
        }
    }
}