package com.module.koin.module

import com.module.koin.vm.DetailParamsViewModel
import com.module.koin.vm.DetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 14:08
 * @mark 自定义类描述
 */
val vmModule = module {

    viewModel { DetailViewModel(get()) }

    viewModel { parameters -> DetailParamsViewModel(parameters.get()) }

}