package com.module.koin.module

import com.module.koin.component.ClassA
import com.module.koin.component.ComponentA
import com.module.koin.component.ComponentB
import com.module.koin.component.IClassA
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.lazyModule
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 13:48
 * @mark 自定义类描述
 */
val moduleA = module {
    single { ComponentA() }
}

val moduleB = module {
    single { ComponentB(get()) }
}

/**
 * includes()类中提供了一个新函数Module，它允许您通过以有组织和结构化的方式包含其他模块来组成模块
 */
val parentModule = module {
    includes(moduleA, moduleB)
}


@OptIn(KoinExperimentalAPI::class)
val m2 = lazyModule {
    singleOf(::ClassA)
}

val m1 = module {
    singleOf(::ClassA) { bind<IClassA>() }
}

val koin=KoinPlatform.getKoin().also {
    //等待启动完成
//    it.waitAllStartJobs()

    //或在启动后运行代码
//    it.runOnKoinStarted {
        //后台加载完成后运行
//    }
}


