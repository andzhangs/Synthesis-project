package com.module.koin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.module.koin.fragment.BlankFragment
import com.module.koin.presenter.Presenter
import com.module.koin.vm.DetailParamsViewModel
import com.module.koin.vm.DetailViewModel
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
    }


    //----------------------------------------------------------------------------------------------

    //    override val scope: Scope by activityScope()
    override val scope: Scope by activityRetainedScope()

    override fun onCloseScope() {
        super.onCloseScope()
    }
}