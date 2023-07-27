package zs.module.material.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class BaseMaterialFragment constructor(private val mIndexName: String) :
    Fragment() {

    protected lateinit var mRootView: View

    @LayoutRes
    abstract fun setLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(setLayoutRes(), container, false)
        return mRootView
    }

    fun getIndexName() = mIndexName

}