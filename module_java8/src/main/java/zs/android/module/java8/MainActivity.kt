package zs.android.module.java8

import android.content.Intent
import android.os.Bundle
import android.system.Os.accept
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import zs.android.module.java8.beans.EarthModel
import zs.android.module.java8.beans.SubModel
import zs.android.module.java8.beans.UserModel
import zs.android.module.java8.functional.FunctionalInterfaceActivity
import zs.android.module.java8.lambda.LambdaActivity
import zs.android.module.java8.newapi.NewDateApiActivity
import zs.android.module.java8.stream.StreamActivity
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Collectors.toList
import java.util.stream.Stream

class MainActivity : AppCompatActivity() {

    companion object {
        const val RESULT_VALUE = "result_value"
    }

    private var mActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == AppCompatActivity.RESULT_OK) {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            " 从 ${it.data?.getStringExtra(RESULT_VALUE)} 返回来的！"
                        )
                    }
                }
            }

        clickListener()
    }

    private fun clickListener() {
        findViewById<AppCompatButton>(R.id.acBtn_stream).apply {
            setOnClickListener {
                jumpActivity(StreamActivity::class.java)
            }
        }
        findViewById<AppCompatButton>(R.id.acBtn_lambda).apply {
            setOnClickListener {
                jumpActivity(LambdaActivity::class.java)
            }
        }
        findViewById<AppCompatButton>(R.id.acBtn_functional).apply {
            setOnClickListener {
                jumpActivity(FunctionalInterfaceActivity::class.java)
            }
        }

        findViewById<AppCompatButton>(R.id.acBtn_functional).apply {
            setOnClickListener {
                jumpActivity(FunctionalInterfaceActivity::class.java)
            }
        }

        findViewById<AppCompatButton>(R.id.acBtn_new_date_api).apply {
            setOnClickListener {
                jumpActivity(NewDateApiActivity::class.java)
            }
        }
    }

    private inline fun <reified T> jumpActivity(cla: Class<T>) {
        mActivityResultLauncher?.launch(Intent(this, T::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivityResultLauncher?.unregister()
    }
}
