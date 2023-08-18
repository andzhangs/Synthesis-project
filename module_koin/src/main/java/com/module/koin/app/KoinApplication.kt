package com.module.koin.app

import android.app.Application
import com.module.koin.module.appModule
import com.module.koin.module.dlsModule
import com.module.koin.module.fragmentModule
import com.module.koin.module.m1
import com.module.koin.module.m2
import com.module.koin.module.parentModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.extension.coroutinesEngine
import org.koin.core.lazyModules
import org.koin.core.logger.Level

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 11:05
 * @mark 自定义类描述
 */
class KoinApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@KoinApplication)
            androidFileProperties()
            // setup a KoinFragmentFactory instance
            fragmentFactory()
            workManagerFactory()
            coroutinesEngine()
            createEagerInstances()

            modules(appModule, dlsModule, parentModule, m1, fragmentModule)
            lazyModules(m2)
        }
       setupWorkManagerFactory()
    }
}