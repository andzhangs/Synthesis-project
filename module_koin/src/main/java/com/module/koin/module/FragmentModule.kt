package com.module.koin.module

import com.module.koin.MainActivity
import com.module.koin.dsl.SimpleServiceImpl
import com.module.koin.fragment.BlankFragment
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 14:31
 * @mark 自定义类描述
 */
val fragmentModule = module {
    single { SimpleServiceImpl() }

    scope<MainActivity> {
        fragment { BlankFragment(get()) }
    }
}