package zs.android.module.spi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import zs.android.module.spi.loader.IClient
import zs.android.module.spi.loader.ISayHello
import java.util.ServiceLoader

class MainActivity : AppCompatActivity() {

    private val mLoaderManager: LoaderManager by lazy { LoaderManager.getInstance(this) }
    private val mLoader: Loader<Int> by lazy { getCustomLoader(this) }

    private val LOADER_ID_USERACCOUNT = 10010


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        ServiceLoader.load(ISayHello::class.java).onEach {
//            it.sayHello()
//        }
//
//        ServiceLoader.load(IClient::class.java).onEach {
//            it.onEvent()
//        }


        mLoader.registerOnLoadCanceledListener { loader ->
            Log.i("print_logs", "registerOnLoadCanceledListener: ${loader.id}")
            mLoaderManager.destroyLoader(loader.id)
        }
    }


    fun onClickLoad(view: View) {
        try {
            mLoader.registerOnLoadCanceledListener { loader ->
                Log.i("print_logs", "MainActivity::registerOnLoadCanceledListener: ${loader.id}")
                mLoaderManager.destroyLoader(loader.id)
            }
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {

        }
        mLoader.forceLoad() // Start Loading..
    }

    fun onClickCancel(view: View) {
        val _loader = mLoaderManager.getLoader<Int>(LOADER_ID_USERACCOUNT)
        if (_loader != null) {
            Log.i("print_logs", "MainActivity::onClickCancel: ${mLoader.cancelLoad()}")
        }
    }


    private fun getCustomLoader(activity: AppCompatActivity): Loader<Int> {
        return mLoaderManager.initLoader(
            LOADER_ID_USERACCOUNT,
            Bundle(),
            object : LoaderManager.LoaderCallbacks<Int> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Int> {
                    Log.i("print_logs", "MainActivity::onCreateLoader: ${Thread.currentThread()}")
                    return CustomLoader(activity)
                }

                override fun onLoadFinished(loader: Loader<Int>, data: Int?) {
                    Log.i(
                        "print_logs",
                        "MainActivity::onLoadFinished: ${loader.id}, $data, ${Thread.currentThread()}"
                    )
//                    mLoaderManager.destroyLoader(loader.id)
                    mLoader.deliverCancellation()
                }

                override fun onLoaderReset(loader: Loader<Int>) {
                    Log.i(
                        "print_logs",
                        "MainActivity::onLoaderReset: ${loader.id}, ${Thread.currentThread()}"
                    )
                }
            })
    }

    private class CustomLoader(context: Context) : AsyncTaskLoader<Int>(context) {
        override fun loadInBackground(): Int {
            Log.i("print_logs", "CustomLoader::loadInBackground: ${Thread.currentThread()}")
            return 100
        }

        override fun onStartLoading() {
            super.onStartLoading()
            Log.i("print_logs", "CustomLoader::onStartLoading: ${Thread.currentThread()}")
            forceLoad()
        }

        override fun onCancelLoad(): Boolean {
            Log.i("print_logs", "CustomLoader::onCancelLoad: ${Thread.currentThread()}")

            return super.onCancelLoad()
        }

        override fun onForceLoad() {
            super.onForceLoad()
            Log.i("print_logs", "CustomLoader::onForceLoad: ${Thread.currentThread()}")
        }

        override fun onStopLoading() {
            super.onStopLoading()
            Log.i("print_logs", "CustomLoader::onStopLoading: ${Thread.currentThread()}")
            cancelLoad()
        }

        override fun onAbandon() {
            super.onAbandon()
            Log.i("print_logs", "CustomLoader::onAbandon: ${Thread.currentThread()}")
        }

        override fun onReset() {
            super.onReset()
            Log.i("print_logs", "CustomLoader::onReset: ${Thread.currentThread()}")
        }

        override fun onContentChanged() {
            super.onContentChanged()
            Log.i("print_logs", "CustomLoader::onContentChanged: ${Thread.currentThread()}")
        }

        override fun onCanceled(data: Int?) {
            super.onCanceled(data)
            Log.i("print_logs", "CustomLoader::onCanceled: ${Thread.currentThread()}")
        }
    }
}