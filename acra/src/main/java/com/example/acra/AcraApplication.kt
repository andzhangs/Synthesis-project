package com.example.acra

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import org.json.JSONObject
import xcrash.ICrashCallback
import xcrash.TombstoneManager
import xcrash.TombstoneParser
import xcrash.XCrash
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/**
 *
 * @author zhangshuai
 * @date 2024/7/10 10:04
 * @description 自定义类描述
 */
class AcraApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        initXCrash(this)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "AcraApplication::onCreate: ")
        }

//        initAcra {
//            ACRA.DEV_LOGGING = BuildConfig.DEBUG
//            buildConfigClass = BuildConfig::class.java
//            reportFormat = StringFormat.JSON
//
//            toast {
//                text = "我是崩溃的提示！"
//            }
//        }
    }

    private fun initXCrash(it: Context) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "LibBaseAppLoader::initXCrash: ")
        }

        // callback for java crash, native crash and ANR
        val callback = ICrashCallback { logPath, emergency ->
            if (emergency != null) {
                debug(it, logPath, emergency)

                // Disk is exhausted, send crash report immediately.
                sendThenDeleteCrashLog(logPath, emergency)
            } else {
                // Add some expanded sections. Send crash report at the next time APP startup.

                // OK
                TombstoneManager.appendSection(logPath, "expanded_key_1", "expanded_content")
                TombstoneManager.appendSection(
                    logPath,
                    "expanded_key_2",
                    "expanded_content_row_1\\n\\expanded_content_row_2"
                )

                // Invalid. (Do NOT include multiple consecutive newline characters ("\n\n") in the content string.)
                // TombstoneManager.appendSection(logPath, "expanded_key_3", "expanded_content_row_1\n\nexpanded_content_row_2");
                debug(it, logPath, null)
            }
        }

//        val anrCallback = ICrashCallback { logPath, emergency ->
//            Log.e("print_logs", "anr 异常: $logPath, $emergency")
//        }
        try {
            val packageInfo: PackageInfo = it.packageManager.getPackageInfo(it.packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode
            }

            // 在这里使用 versionCode 或 versionName
            XCrash.init(it, XCrash.InitParameters().apply {
                setAppVersion("$versionName - $versionCode")
                setJavaRethrow(true)
                setJavaLogCountMax(10)
                setJavaDumpAllThreadsWhiteList(arrayOf("^main$", "^Binder:.*", ".*Finalizer.*"))
                setJavaDumpAllThreadsCountMax(10)
                setJavaCallback(callback)
                setNativeRethrow(true)
                setNativeLogCountMax(10)
                setNativeDumpAllThreadsWhiteList(
                    arrayOf(
                        "^xcrash\\.sample$",
                        "^Signal Catcher$",
                        "^Jit thread pool$",
                        ".*(R|r)ender.*",
                        ".*Chrome.*"
                    )
                )
                setNativeDumpAllThreadsCountMax(10)
                setNativeCallback(callback)
                setAnrRethrow(true)
                setAnrLogCountMax(10)
                setAnrCallback(callback)
                setPlaceholderCountMax(3)
                setPlaceholderSizeKb(512)
                setLogFileMaintainDelayMs(1000)

                val fileFolder =
                    "${it.applicationContext.getExternalFilesDir("Log")?.absolutePath}${File.separator}crash${File.separator}xCrash"

                File(fileFolder).apply {
                    if (!exists()) {
                        mkdirs()
                    }
                }
                setLogDir(fileFolder)
            })
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            TombstoneManager.getAllTombstones().forEach { file ->
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "LibBaseAppLoader::initXCrash: ${file.absolutePath}")
//                }
//                sendThenDeleteCrashLog(file.absolutePath, null)
//            }
//        }
    }

    private fun sendThenDeleteCrashLog(logPath: String?, emergency: String?) {
        // Parse
        val map = TombstoneParser.parse(logPath, emergency)
        val crashReport = JSONObject(map as Map<*, *>?).toString()


        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "LibBaseAppLoader::sendThenDeleteCrashLog: $crashReport")
        }
        // Send the crash report to server-side.
        // ......

        // If the server-side receives successfully, delete the log file.
        //
        // Note: When you use the placeholder file feature,
        //       please always use this method to delete tombstone files.
        //
        //TombstoneManager.deleteTombstone(logPath);
    }

    private fun debug(context: Context, logPath: String?, emergency: String?) {

        val map=TombstoneParser.parse(logPath,emergency)

        map.forEach { (t, u) ->
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "打印: $t, $u")
            }
        }

        val time = System.currentTimeMillis()
        val file = File("${XCrash.getLogDir()}${File.separator}info").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        try {
            PrintWriter(FileWriter(File(file, "${time}_crash_info.txt"))).apply {
                println("time: ${map["Crash time"]}")
                println("appVersion：Name-${map["App version"]}")
                println("mobileDevice: ${map["Brand"]} ${map["Model"]}")
                println("androidVersion：${map["API level"]} ${map["OS version"]}")
                println("error：${map["tname"]}-> ${map["java stacktrace"]}")
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }



        // Parse and save the crash info to a JSON file for debugging.
        var writer: FileWriter? = null
        try { //XCrash.getLogDir()
            val debug = File("${XCrash.getLogDir()}${File.separator}debug.json")
            debug.createNewFile()
            writer = FileWriter(debug, false)
            writer.write(
                (TombstoneParser.parse(
                    logPath,
                    emergency
                ) as Map<*, *>?)?.let {
                    JSONObject(
                        it
                    ).toString()
                }
            )
        } catch (e: Exception) {
            Log.d("print_logs", "debug failed", e)
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (ignored: Exception) {
                }
            }
        }
    }
}