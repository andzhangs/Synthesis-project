package com.module.koin.module

import com.module.koin.presenter.Presenter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 11:44
 * @mark 自定义类描述
 */
val appModule = module {
    factory {
        Presenter(androidContext())
    }
}