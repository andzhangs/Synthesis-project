/**
 *
 * @author zhangshuai
 * @date 2025/5/28 17:01
 * @description 自定义类描述
 */
class Main {


    companion object{
        fun hh(){

        }
    }

    fun innerFun1(a: () -> Unit) {
        a()
    }

    fun test() {
        innerFun1 {
            //return 这样写会报错，非局部返回，直接退出 test() 函数。
            return@innerFun1 //局部返回，退出 innerFun() 函数，这里必须明确退出哪个函数，写成 return@test 则会退出 test() 函数
        }
        //以下代码依旧会执行
        println("test...")
    }


    //----------------------------------------------------------------------------------------------

    inline fun fun1(block: () -> Unit) {
        block()
    }

    fun test1() {
        fun1{
            return //非局部返回，直接退出 test() 函数。
        }

        //以下代码不会执行
        println("test...")
    }


    //----------------------------------------------------------------------------------------------

    inline fun innerFun2(crossinline block: () -> Unit) {
        block()
    }

    fun test2() {
        innerFun2{
            //return 这里这样会报错，只能 return@innerFun2
            return@innerFun2
        }

        //以下代码不会执行
        println("test...")
    }
}