package zs.android.module.spi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import zs.android.module.spi.loader.IClient
import zs.android.module.spi.loader.IClientAndroid
import zs.android.module.spi.loader.ISayHello
import java.util.ServiceLoader

/**
 * ServiceLoader 通过配置文件和反射机制，实现了服务接口与实现类的解耦，
 * 是 Java 和 Android 中实现插件化、组件化的重要工具。
 * 在 Android 中使用时需注意类加载器差异和性能优化，
 * 推荐结合编译时工具（如 AutoService）生成配置文件，减少运行时开销。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mLoaderManager: LoaderManager
    private var mLoader: Loader<IClient>? = null
    private val LOADER_ID_USERACCOUNT = 10010


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLoaderManager = LoaderManager.getInstance(this)

        ServiceLoader.load(ISayHello::class.java).onEach {
            it.sayHello()
        }
    }


    fun onClickLoad(view: View) {
        try {
            mLoader = getCustomLoader(this).apply {
                registerListener(LOADER_ID_USERACCOUNT, mLoadCompleteListener)
                registerOnLoadCanceledListener(mLoadCanceledListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

        }
        mLoader?.forceLoad() // Start Loading..
    }

    private val mLoadCanceledListener = Loader.OnLoadCanceledListener<IClient> { loader ->
        Log.i("print_logs", "MainActivity::registerOnLoadCanceledListener: ${loader.id}")
        mLoaderManager.destroyLoader(loader.id)
    }

    private val mLoadCompleteListener = Loader.OnLoadCompleteListener<IClient> { loader, data ->
        Log.i("print_logs", "MainActivity::: ")
    }

    fun onClickCancel(view: View) {
        mLoaderManager.getLoader<Int>(LOADER_ID_USERACCOUNT)?.cancelLoad()
    }


    private fun getCustomLoader(activity: AppCompatActivity): Loader<IClient> {
        return mLoaderManager.initLoader(
            LOADER_ID_USERACCOUNT,
            Bundle(),
            object : LoaderManager.LoaderCallbacks<IClient> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<IClient> {
                    Log.w("print_logs", "MainActivity::onCreateLoader: ${Thread.currentThread().name}")
                    return CustomLoader(activity)
                }

                override fun onLoadFinished(loader: Loader<IClient>, data: IClient?) {
                    Log.w(
                        "print_logs",
                        "MainActivity::onLoadFinished: ${loader.id},${Thread.currentThread().name}"
                    )
//                    mLoaderManager.destroyLoader(loader.id)
                    mLoader?.deliverCancellation()
                }

                override fun onLoaderReset(loader: Loader<IClient>) {
                    Log.w(
                        "print_logs",
                        "MainActivity::onLoaderReset: ${loader.id}, ${Thread.currentThread().name}"
                    )
                }
            })
    }

    private class CustomLoader(context: Context) : AsyncTaskLoader<IClient>(context) {

        init {
            Log.i("print_logs", "CustomLoader::: ")
        }

        override fun onStartLoading() {
            super.onStartLoading()
            Log.i("print_logs", "CustomLoader::onStartLoading: ${Thread.currentThread().name}")
            forceLoad()
        }

        override fun onCancelLoad(): Boolean {
            Log.i("print_logs", "CustomLoader::onCancelLoad: ${Thread.currentThread().name}")
            return super.onCancelLoad()
        }

        override fun onForceLoad() {
            super.onForceLoad()
            Log.i("print_logs", "CustomLoader::onForceLoad: ${Thread.currentThread().name}")
        }

        override fun loadInBackground(): IClient {
            Log.i("print_logs", "CustomLoader::loadInBackground-1: ${Thread.currentThread().name}, ${System.currentTimeMillis()}")
            Thread.sleep(2000L)
            Log.i("print_logs", "CustomLoader::loadInBackground-2: ${Thread.currentThread().name}, ${System.currentTimeMillis()}")
            return IClientAndroid()
        }

        override fun onStopLoading() {
            super.onStopLoading()
            Log.i("print_logs", "CustomLoader::onStopLoading: ${Thread.currentThread().name}")
//            cancelLoad()
        }

        override fun onAbandon() {
            super.onAbandon()
            Log.i("print_logs", "CustomLoader::onAbandon: ${Thread.currentThread().name}")
        }

        override fun onReset() {
            super.onReset()
            Log.i("print_logs", "CustomLoader::onReset: ${Thread.currentThread().name}")
        }

        override fun onContentChanged() {
            super.onContentChanged()
            Log.i("print_logs", "CustomLoader::onContentChanged: ${Thread.currentThread().name}")
        }

        override fun onCanceled(data: IClient?) {
            super.onCanceled(data)
            Log.i("print_logs", "CustomLoader::onCanceled: ${Thread.currentThread().name}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoader?.unregisterOnLoadCanceledListener(mLoadCanceledListener)
        mLoader?.unregisterListener(mLoadCompleteListener)
    }
}