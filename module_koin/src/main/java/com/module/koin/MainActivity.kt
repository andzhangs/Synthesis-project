package com.module.koin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import com.module.koin.fragment.BlankFragment
import com.module.koin.presenter.Presenter
import com.module.koin.vm.DetailParamsViewModel
import com.module.koin.vm.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoinScope
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), AndroidScopeComponent {

    private val mPresenter: Presenter by inject()

    private val detailViewModel: DetailViewModel by viewModel()

    private val id = "你好啊"

    private val detailParamsViewModel: DetailParamsViewModel by viewModel { parametersOf(id) }

    @OptIn(KoinInternalApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Koin Fragment Factory
        setupKoinFragmentFactory(getKoinScope())

        setContentView(R.layout.activity_main)

        mPresenter.getFun()
        val presenter: Presenter = get()
        presenter.getFunContext()

        supportFragmentManager.beginTransaction()
            .replace<BlankFragment>(
                containerViewId = R.id.fl_root,
                args = Bundle(),
                tag = MainActivity::class.java.name
            ).commit()
//        lifecycleScope.launch {
            ////内部存储
//            createFile(getDir("customCache",Context.MODE_PRIVATE))
//            createFile(filesDir)
//            createFile(cacheDir)
//            createFile(codeCacheDir)
//            createFile(obbDir)
//        }

        lifecycleScope.launch {
            //lifecycle.withResumed 和 lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) 都是在 Lifecycle 处于 RESUMED 状态时执行代码。
            //
            //lifecycle.withResumed 会在 Lifecycle 第一次进入 RESUMED 状态时执行代码，并且在 Lifecycle 离开 RESUMED 状态时取消执行。
            //
            //lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) 会在 Lifecycle 每次进入 RESUMED 状态时执行代码，并且在 Lifecycle 离开 RESUMED 状态时重新执行。
            //
            //因此，如果您只需要在 Lifecycle 第一次进入 RESUMED 状态时执行代码，
            //那么您应该使用 lifecycle.withResumed。
            //如果您需要在 Lifecycle 每次进入 RESUMED 状态时执行代码，
            //那么您应该使用 lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED)。
            lifecycle.withResumed {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::withResumed: ")
                }
            }
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::repeatOnLifecycle: RESUMED")
                }
            }
        }
    }

    private suspend fun createFile(file: File) {
        withContext(Dispatchers.IO) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onCreate: ${file.absolutePath}")
            }
            val newFile = File(file, "test.txt")
            try {
                val fos = FileOutputStream(newFile)
                fos.write("[${System.currentTimeMillis()}] - [${Thread.currentThread().name}] 你好！我的朋友".toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //----------------------------------------------------------------------------------------------

    //    override val scope: Scope by activityScope()
    override val scope: Scope by activityRetainedScope()

    override fun onCloseScope() {
        super.onCloseScope()
    }
}