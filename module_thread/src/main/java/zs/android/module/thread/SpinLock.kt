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

//    @Synchronized
    fun lock(){
        synchronized(locked){

        }
        while (!locked.compareAndSet(false,true)){
            // 自旋等待（空循环或短暂休眠）
        }
    }

    fun unlock(){
        locked.set(false)
    }
}