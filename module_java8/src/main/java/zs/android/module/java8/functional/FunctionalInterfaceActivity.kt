package zs.android.module.java8.functional

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import zs.android.module.java8.BuildConfig
import zs.android.module.java8.MainActivity
import zs.android.module.java8.R
import zs.android.module.java8.beans.EarthModel
import zs.android.module.java8.beans.SubModel
import zs.android.module.java8.beans.UserModel
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * 内置的函数式接口
 */
class FunctionalInterfaceActivity : AppCompatActivity() {

    private val mList: ArrayList<UserModel> by lazy {
        arrayListOf(
            UserModel("One", "男", "16"),
            UserModel("Two", "女", "17"),
            UserModel("Three", "男", "18"),
            UserModel("Four", "女", "19"),
            UserModel("Five", "女", "20")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_functional_interface)
        predicate()
        function()
        supplier()
        consumer()
        comparator()
        optional()
    }

    /**
     * Predicate（断言） 是一个可以指定入参类型，并返回 boolean 值的函数式接口。它内部提供了一些带有默认实现的方法，可以被用来组合一个复杂的逻辑判断（and, or, negate）
     */
    private fun predicate() {
        val predicate = Predicate<String> { t ->
            Log.i("print_logs", "输入条件参数: $t")
            t.isNotEmpty()
        }
        val result1 = predicate.test("foo")
        val result2 = predicate.negate().test("foo")
        val result3 = predicate.or { it.length > 2 }.test("123")
        Log.i("print_logs", "predicate,结果：$result1, $result2, $result3")
    }

    /**
     * Function 函数式接口的作用是，我们可以为其提供一个原料，他给生产一个最终的产品。通过它提供的默认方法，组合,链行处理(compose, andThen)
     */
    private fun function() {
        val function = Function<String, Int> { t -> t.toInt() }
        val function2 = function.andThen { it.toString() }

        Log.i("print_logs", "FunctionalInterfaceActivity::function: ${function2.apply("123")}")
    }

    /**
     * Supplier 与 Function 不同，==它不接受入参，直接为我们生产一个指定的结果，==有点像生产者模式：
     */
    private fun supplier() {
        val userModelSupplier = Supplier<UserModel> { UserModel("HelloWorld", "男", "18") }
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "supplier: ${userModelSupplier.get()}")
        }
    }

    /**
     * Consumer 消费者
     */
    private fun consumer() {
        val consumer = Consumer<UserModel> {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "consumer: $it")
            }
        }

        consumer.accept(UserModel("HelloWorld", "男", "18"))
    }

    /**
     * Comparator 在 Java 8 之前是使用比较普遍的。Java 8 中除了将其升级成了函数式接口，还为它拓展了一些默认方法：
     */
    private fun comparator() {
        val comparator = Comparator<UserModel> { p1, p2 -> p1.sex.compareTo(p2.sex) }
        val p1=UserModel("Annie", "女", "19")
        val p2=UserModel("Tom", "男", "18")
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "comparator:compare: ${comparator.compare(p1,p2)}, reversed.compare: ${comparator.reversed().compare(p1,p2)} ")

        }
    }

    /**
     * 空判断
     */
    private fun optional() {
        val data = mList[0]

        val optional = Optional.of(data)
        if (optional.isPresent) {
            Log.w("print_logs", "对象不为空则打出：${optional.get()}")
        } else {
            Log.e("print_logs", "对象为空则打出：X")
        }

        val optional1 = Optional.of(UserModel::name)
        if (optional1.isPresent) {
            Log.w("print_logs", "名称不为空则打出：${optional1.get()}")
        } else {
            Log.e("print_logs", "名称为空则打出：X")
        }

        Optional.ofNullable("Optional.ofNullable.ifPresent").ifPresent {
            Log.i("print_logs", "如果不为空，则打出来: $it")
        }

        Log.i(
            "print_logs",
            "如果为空，则返回指定字符串: ${Optional.ofNullable<Any>(null).orElse("_")}"
        )
        Log.i(
            "print_logs",
            "如果为空，则返回指定字符串：${Optional.ofNullable<Any>("14").orElse("_")}"
        )
        Log.i(
            "print_logs", "如果为空，则返回指定方法，或者代码-1：${
                Optional.ofNullable<Any>(null).orElseGet {
                    "哈哈哈"
                }
            }"
        )
        Log.i(
            "print_logs", "如果为空，则返回指定方法，或者代码-2：${
                Optional.ofNullable("123").orElseGet {
                    "哈哈哈"
                }
            }"
        )
        try {
            Log.w(
                "print_logs", "如果为空，则可以抛出异常：${
                    Optional.ofNullable(null).orElseThrow {
                        RuntimeException("Hello 异常了")
                    }
                }"
            )
        } catch (e: RuntimeException) {
            Log.e("print_logs", "异常: $e")
        }

        val subModel = SubModel().apply {
            name = "Android"
        }


        val model = EarthModel().apply {
            this.model = subModel
            this.userModel = mList
        }
        val isNull =
            Optional.ofNullable(model).map(EarthModel::model).map { return@map SubModel::name }.isPresent
        Log.w("print_logs", "Optional多级判断:: $isNull")

        Optional.ofNullable(model).map(EarthModel::userModel).map {
            it.stream().map(UserModel::name).collect(Collectors.toList())
        }.ifPresent {
            Log.d("print_logs", "判断对象中的list: $it")
        }
    }


    override fun onResume() {
        super.onResume()
        setResult(RESULT_OK, Intent().apply {
            putExtra(
                MainActivity.RESULT_VALUE,
                this@FunctionalInterfaceActivity::class.java.simpleName
            )
        })
    }
}