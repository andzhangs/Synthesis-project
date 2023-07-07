package com.android.module.java11

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.module.java11.bean.Garbage
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.function.Predicate
import java.util.stream.Collectors
import kotlin.io.path.Path


/**
 * Java11 部分适配
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isBlank()
        lines()
        strip()
        repeat()
        predicate()
        readWriteFile()
//        epsilon()

    }

    /**
     * 断字符串是不是空字符"“或者trim()之后(” ")为空字符
     */
    private fun isBlank() {
        val blankStr = "     "
        Log.i("print_logs", "isBlank: ${blankStr.isBlank()}")
    }

    /**
     * 将一个字符串按照行终止符（换行符\n或者回车符\r）进行分割，并将分割为Stream流
     */
    private fun lines() {
        val newStr = "Hello Java 11 \n felord.cn \r 2021-09-28"
        newStr.lines().forEach {
            Log.i("print_logs", "lines: $it")
        }
    }

    /**
     * trim() 只能去除半角空白符。
     * 变种：
     *  stripLeading()用来去除前面的全角半角空白符；
     *  stripTrailing()用来去除尾部的全角半角空白符。
     */
    private fun strip() {
        val str = "HELLO\u3000"
        if (BuildConfig.DEBUG) {
            Log.i(
                "print_logs",
                "str：${str.length}, trim：${str.trim().length}, strip: ${str.strip().length}"
            )
        }
    }

    /**
     * repeat(n)
     * 按照给定的次数重复串联字符串的内容：
     */
    private fun repeat() {
        val str = "HELLO"
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "repeat(n): ${str.repeat(0)}, ${str.repeat(1)}, ${str.repeat(2)}")
        }
    }

    /**
     * java.util.function.Predicate是我们很常用的断言谓词函数。在以前取反我们得借助于!符号，到了Java 11我们可以借助于其静态方法not来实现
     */
    private fun predicate() {

        val sampleList = listOf("张三", "java 11", "jack")

        // 在以前取反我们得借助于!符号，Java 11借助于其静态方法not
        val result1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = sampleList.stream()
                .filter { it.startsWith("j") }
                .filter(Predicate.not { it.contains("11") })
                .collect(Collectors.toList())
            Log.i("print_logs", "predicate取反: $result")
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
    }

    /**
     * 文件中读写字符串内容更方便 Java 11中可以更轻松地从文件中读取和写入字符串内容了，
     * 通过Files工具类提供的新的静态方法readString和writeString分别进行读写文件的字符串内容，
     *
     * 在Android 13设备上自动拥有外部存储上所有应用程序特定目录和所有公共目录的写入权限
     */
    private fun readWriteFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val path =
                application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absoluteFile.toString() + File.separator + "hello.txt"
//            val path =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absoluteFile.toString() + File.separator + "hello.txt"

            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::readWriteFile: $path")
            }

            File(path).apply {
                if (!exists()) {
                    createNewFile()
                }
            }

            Files.write(Path(path), "哈哈哈".toByteArray(), StandardOpenOption.APPEND)
            Files.readAllLines(Path(path), Charset.defaultCharset()).forEach {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "readFile: $it")
                }
            }
        }
    }

    /**
     * Epsilon垃圾收集器
     * JDK上对这个特性的描述是：开发一个处理内存分配但不实现任何实际内存回收机制的GC，一旦可用堆内存用完，JVM就会退出
     */
    private fun epsilon() {
        try {
            val list = mutableListOf<Garbage>()
            val flag = true
            var count = 0
            while (flag){
                list.add(Garbage(count))
                if (count++ ==500){
                    list.clear()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "MainActivity::epsilon: 内存泄漏！")
            }
        }
        
    }

}
