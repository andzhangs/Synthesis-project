package com.module.koin.module

import com.module.koin.MainActivity
import com.module.koin.dsl.FactoryPresenter
import com.module.koin.dsl.Session
import com.module.koin.dsl.SimpleService
import com.module.koin.dsl.SimpleServiceImpl
import com.module.koin.dsl.SimpleViewModel
import com.module.koin.dsl.SimpleWorker
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 13:39
 * @mark 自定义类描述
 */
val dlsModule = module {

    singleOf(::SimpleServiceImpl) {
        bind<SimpleService>()
    }
    factoryOf(::FactoryPresenter)
    //Session类使用范围仅在MainActivity生命周期类
    scope<MainActivity> {
        scopedOf(::Session)
    }
    viewModelOf(::SimpleViewModel)
    workerOf(::SimpleWorker)
}