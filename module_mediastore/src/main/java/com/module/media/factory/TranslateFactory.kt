package com.module.media.factory

import androidx.annotation.WorkerThread
import com.module.media.factory.translate.BaiduTranslate
import com.module.media.factory.translate.YouDaoTranslate

/**
 *
 * @author zhangshuai
 * @date 2024/8/7 15:12
 * @description 创建翻译工程类
 */
object TranslateFactory {

    private val mBaiduTranslate by lazy { BaiduTranslate() }

    private val mYouDaoTranslate by lazy { YouDaoTranslate() }

    @WorkerThread
    @JvmStatic
    suspend fun getChineseText(
        type: TranslateType,
        input: String,
        filePath: String
    ): Triple<String, String, String> {
        return when (type) {
            TranslateType.BAI_DU -> mBaiduTranslate.getChineseText(input, filePath)
            TranslateType.YOU_DAO -> mYouDaoTranslate.getChineseText(input, filePath)
        }
    }
}