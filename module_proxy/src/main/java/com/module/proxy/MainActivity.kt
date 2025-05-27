package com.module.proxy

import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.module.proxy.jdk.UserService
import com.module.proxy.jdk.UserService2
import com.module.proxy.jdk.UserServiceImpl
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadJdkProxy()
        loadCglibProxy()
    }

    //基于接口的 JDK 动态代理
    private fun loadJdkProxy() {
        val userService = Proxy.newProxyInstance(
            UserService::class.java.classLoader,
            arrayOf(UserService::class.java),
            object : InvocationHandler {
                private val mTarget = UserServiceImpl()
                override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                    Log.i("print_logs", "UserInvocationHandler::invoke: Before")

                    val result = method?.invoke(mTarget, *(args ?: emptyArray()))

                    Log.i("print_logs", "UserInvocationHandler::invoke: After")
                    return result
                }

            }
        ) as UserService

        userService.getInfo("哈哈")
    }

    //基于类的 CGLIB 动态代理
    private fun loadCglibProxy() {
        try {
            val enhancer = Enhancer()

            //设置父类(目标类)
            enhancer.setSuperclass(UserService2::class.java)

            //设置回调(拦截器)
            enhancer.setCallback(object : MethodInterceptor {
                override fun intercept(
                    obj: Any?,
                    method: Method?,
                    args: Array<out Any>?,
                    proxy: MethodProxy?
                ): Any? {
                    Log.i("print_logs", "MainActivity::intercept: Before")

                    val result = proxy?.invokeSuper(obj, args)

                    Log.i("print_logs", "MainActivity::intercept: After")
                    return result
                }
            })

            val proxy=enhancer.create() as UserService2

            proxy.getInfo("哈哈哈.")
        }catch (e:Exception){
            e.printStackTrace()
            Log.e("print_logs", "loadCglibProxy: $e")
        }
    }

}