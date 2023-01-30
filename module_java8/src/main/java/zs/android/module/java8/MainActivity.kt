package zs.android.module.java8

import android.os.Bundle
import android.system.Os.accept
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import zs.android.module.java8.beans.EarthModel
import zs.android.module.java8.beans.SubModel
import zs.android.module.java8.beans.UserModel
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Collectors.toList
import java.util.stream.Stream

class MainActivity : AppCompatActivity() {

    private val mList: ArrayList<UserModel> by lazy {
        arrayListOf(
            UserModel("One", "男", "16"),
            UserModel("Two", "女", "17"),
            UserModel("Three", "男", "18"),
            UserModel("Four", "女", "19"),
            UserModel("Five", "女", "20")
        )
    }

    private   val sb = StringBuilder()

    private val tv: AppCompatTextView by lazy { findViewById(R.id.acTv_info) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sb.append("filter操作符：\n")
        mList.stream().filter { "男" == it.sex }.collect(toList()).forEach {
            sb.append("\t\t").append(it).append("\n")
        }.apply {
            tv.text = sb.toString()
        }

        sb.append("map操作符：").append("\n")
        mList.stream().map { it.name }.collect(toList()).forEach {
            sb.append("\t\t").append(it).append("\n")
        }.apply {
            tv.text = sb.toString()
        }

        reduceTest()
        collectTest()
        optionalTest()
        parallelTest()
        peek()
    }

    /**
     * 累加
     */
    private fun reduceTest() {
        sb.append("reduce(累加)操作符：").append("\n")

        Log.i("print_logs", "MainActivity::reduceTest: 累加")

        Stream.of(1, 2, 3, 4, 5).reduce { sum, item ->
            Log.i("print_logs", "\t\treduce: $sum, $item")
            sum + item
        }.apply {
            Log.i("print_logs", "\t\treduceTest: ${this.get()}")
            sb.append(this.get())
        }
    }

    /**
     * 集合类型转换
     */
    private fun collectTest() {
        //toList
        val list = mList.stream().map(UserModel::name).collect(toList())
        //toSet
        val setList = mList.stream().map(UserModel::name).collect(Collectors.toSet())
        //toMap()
        val map1 = mList.stream().collect(Collectors.toMap(UserModel::name, UserModel::sex))
        val map2 = mList.stream().collect(Collectors.toMap(UserModel::name) {
            it.name + "自定义字符串！"
        })
//        val treeSet=mList.stream().collect(Collectors.toCollection(TreeSet::new))
        //分组
        val mapBoolList = mList.stream().collect(Collectors.groupingBy {
            it.sex == "男"
        })

        //分隔
        val str = mList.stream().map(UserModel::name).collect(Collectors.joining(",", "{", "}"))

        //自定义
        val strs = Stream.of("1", "2", "3").collect(
            Collectors.reducing(ArrayList(),
                { x ->
                    mutableListOf(
                        x
                    )
                },
                { y: MutableList<String>, z: List<String>? ->
                    y.addAll(
                        z!!
                    )
                    y
                })
        )

        Log.i(
            "print_logs", """
            集合类型转换:
            toList(): $list
            toSet(): $setList
            toMap()1: $map1
            toMap()2: $map2
            Map分组: $mapBoolList
            分隔：$str
            自定义: $strs
        """.trimIndent()
        )
    }

    /**
     * 空判断
     */
    private fun optionalTest() {
        val data = mList[0]

        val optional = Optional.of(data)
        if (optional.isPresent) {
            Log.w("print_logs", "对象为空则打出：${optional.get()}")
        } else {
            Log.e("print_logs", "对象为空则打出：X")
        }

        val optional1 = Optional.of(UserModel::name)
        if (optional1.isPresent) {
            Log.w("print_logs", "名称为空则打出：${optional1.get()}")
        } else {
            Log.e("print_logs", "名称为空则打出：X")
        }

        Optional.ofNullable("Optional.ofNullable.ifPresent").ifPresent {
            Log.i("print_logs", "如果不为空，则打出来: $it")
        }

        Log.i("print_logs", "如果为空，则返回指定字符串: ${Optional.ofNullable<Any>(null).orElse("_")}")
        Log.i("print_logs", "如果为空，则返回指定字符串：${Optional.ofNullable<Any>("14").orElse("_")}")
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
            Optional.ofNullable(model).map(EarthModel::model).map { SubModel::name }.isPresent
        Log.w("print_logs", "Optional多级判断:: $isNull")

        Optional.ofNullable(model).map(EarthModel::userModel).map {
            it.stream().map(UserModel::name).collect(toList())
        }.ifPresent {
            Log.d("print_logs", "判断对象中的list: $it")
        }
    }

    /**
     * 并发 parallelStream
     */
    private fun parallelTest() {
        val list = mList.parallelStream().map {
            it.name.split(" ")
        }.flatMap {
            listOf(it).stream()
        }.collect(toList())
        Log.i("print_logs", "并发 parallel: $list")
    }

    /**
     * peek操作符
     */
    private fun peek() {
        Log.i("print_logs", "peek操作符")

        val list = mList.stream().map(UserModel::name).peek {
            Log.i("print_logs", "\t\tvalue: $it")
        }.collect(toList())

        Log.i("print_logs", "MainActivity::peek: $list")
    }
}
