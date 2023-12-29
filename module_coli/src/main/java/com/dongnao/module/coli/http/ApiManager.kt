package com.dongnao.module.coli.http

import android.annotation.SuppressLint
import android.util.Log
import com.dongnao.module.coli.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.orhanobut.logger.Logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jaxb.JaxbConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.wire.WireConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 *
 * @author zhangshuai
 * @date 2023/12/27 14:09
 * @description 自定义类描述
 */
object ApiManager {

    private val TIME_OUT = if (BuildConfig.DEBUG) 30L else 60L

    fun <T> getService(baseUrl:String,t: Class<T>): T {
        val singleRetrofit=createRetrofit(baseUrl)
        return singleRetrofit.create(t)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createRetrofit(baseUrl:String,): Retrofit {

        //允许不合规的json格式
//        val gson=GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(WireConverterFactory.create())
            .addConverterFactory(JaxbConverterFactory.create())
            .addConverterFactory(ProtoConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val interceptorLogger = HttpLoggingInterceptor(httpLogger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .dns(HttpDns())
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .sslSocketFactory(createSSLSocketFactory()!!, trustManager)
            .hostnameVerifier(hostnameVerifier)
            .addInterceptor(interceptorLogger)
            .addInterceptor(OkHttpProfilerInterceptor())
            .build()
    }

    private val httpLogger = object : HttpLoggingInterceptor.Logger {

        private val gson: Gson by lazy { GsonBuilder().setPrettyPrinting().create() }
        private val jsonParser: JsonParser by lazy { JsonParser() }
        private val mMessage = java.lang.StringBuilder()

        override fun log(message: String) {
            try {
                // 请求或者响应开始
                var localMsg: String? = message
                if (localMsg!!.startsWith("--> POST")
                    || localMsg.startsWith("--> GET")
                    || localMsg.startsWith("--> PUT")
                    || localMsg.startsWith("--> DELETE")
                ) {
                    mMessage.setLength(0)
                }

                // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
                if ((localMsg.startsWith("{") && localMsg.startsWith("}", localMsg.length - 2))
                    || (localMsg.startsWith("[") && localMsg.startsWith("]", localMsg.length - 2))
                ) {
                    localMsg = gson.toJson(jsonParser.parse(localMsg).asJsonObject)
                }
                mMessage.append("$localMsg\n")
                // 响应结束，打印整条日志
                if (localMsg!!.startsWith("<-- END HTTP")) {
                    Logger.i(mMessage.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    internal class HttpDns : Dns {

        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                val mInetAddressesList: MutableList<InetAddress> = ArrayList()
                val mInetAddresses = InetAddress.getAllByName(
                    hostname
                )
                if (mInetAddresses.size > 1) {
                    for (address in mInetAddresses) {
                        if (address is Inet4Address) {
                            mInetAddressesList.add(address)
                        } else {
                            mInetAddressesList.add(0, address)
                        }
                    }
                } else {
                    for (address in mInetAddresses) {
                        if (address is Inet4Address) {
                            mInetAddressesList.add(0, address)
                        } else {
                            mInetAddressesList.add(address)
                        }
                    }
                }
                mInetAddressesList
            } catch (var4: NullPointerException) {
                val unknownHostException = UnknownHostException(
                    "Broken system behaviour"
                )
                unknownHostException.initCause(var4)
                throw unknownHostException
            }
        }

    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        var ssfFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(trustManager), SecureRandom())
            ssfFactory = sc.socketFactory
        } catch (e: Exception) {
            Log.e("print_logs", "HttpModule::createSSLSocketFactory: $e")
        }
        return ssfFactory
    }

    private val trustManager: X509TrustManager = @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            // 在此执行证书验证，如果验证失败则抛出 CertificateException
            // 例如：检查客户端证书是否由受信任的 CA 签名
            // 如果不是，则抛出 CertificateException
            // 否则，将认为证书是受信任的。
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
    }

    private val hostnameVerifier = HostnameVerifier { _, _ -> true }
}