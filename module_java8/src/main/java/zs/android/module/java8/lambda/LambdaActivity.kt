package zs.android.module.java8.lambda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zs.android.module.java8.MainActivity
import zs.android.module.java8.R
import zs.android.module.java8.beans.UserModel

class LambdaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lambda)

        listOf(1, 2, 3, 4).apply {
            //all：都是判断集合中所有的元素是否满足条件, 只要有一个不满足直接返回 false, 否则返回 true
            this.all { it > 2 }
            //any：判断集合中是否存在至少一个满足条件的, 如果满足返回 true, 否则返回 false
            this.any { it > 2 }
            //count：判断集合内有几个满足条件判断的
            this.count { it >= 2 }
            //find：查找集合内第一个满足条件的元素
            this.find { it == 2 }
        }


        val stringConverter = object : Converter<Int, String> {
            override fun convert(from: Int): String {
                return from.toString()
            }
        }

        stringConverter.convert(100)
    }

    @FunctionalInterface
    interface Converter<F, T> {
        fun convert(from: F): T
    }

    override fun onResume() {
        super.onResume()
        setResult(RESULT_OK, Intent().apply {
            putExtra(MainActivity.RESULT_VALUE, this@LambdaActivity::class.java.simpleName)
        })
    }
}