package zs.android.module.thread

import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author zhangshuai
 * @date 2025/5/27 15:30
 * @description 自定义类描述
 */
class SpinLock {
    private val locked= AtomicBoolean(false)

    fun lock(){
        while (!locked.compareAndSet(false,true)){
            // 自旋等待（空循环或短暂休眠）
            // 可以加入Thread.yield()减少CPU占用
            Thread.yield()
        }
    }

    fun unlock(){
        locked.set(false)
    }
}