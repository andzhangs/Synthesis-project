package com.example.acra

/**
 *
 * @author zhangshuai
 * @date 2024/7/10 10:54
 * @description 当使用ACRA是，XCrash不生成崩溃文件
 */
//@AutoService(ReportSenderFactory::class)
//class HttpReportSenderFactory : ReportSenderFactory {
//    init {
//        if (BuildConfig.DEBUG) {
//            Log.i("print_logs", "HttpReportSenderFactory::: ")
//        }
//    }
//    override fun create(context: Context, config: CoreConfiguration): ReportSender {
//        return object : ReportSender {
//            override fun send(context: Context, errorContent: CrashReportData) {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "HttpReportSenderFactory::send:  ${errorContent.toJSON()}")
//                }
//                val fileFolder =
//                    "${context.applicationContext.getExternalFilesDir("Log")?.absolutePath}${File.separator}crash${File.separator}acra"
//
//                val dir = File(fileFolder)
//                if (!dir.exists()) {
//                    dir.mkdirs()
//                }
//
//                val time = System.currentTimeMillis()
//                val simpleDateFormat =
//                    SimpleDateFormat("yyyy年MM月dd日_HH时mm分ss秒_E", Locale.CHINA)
//                val date = Date(time)
//                val result = simpleDateFormat.format(date)
//                val file = File(dir, "$time.txt")
//                try {
//                    PrintWriter(FileWriter(file)).apply {
//                        println("time: $result")
//                        println("crashDate: ${errorContent["USER_CRASH_DATE"]}")
//                        println("appVersion：Name-${errorContent["APP_VERSION_NAME"]}, Code-${errorContent["APP_VERSION_CODE"]}")
//                        println("mobileDevice: ${errorContent["BRAND"]} ${errorContent["PHONE_MODEL"]}")
//                        println("androidVersion：${errorContent["ANDROID_VERSION"]}")
//                        println("filePath：$fileFolder")
//                        println("error：${errorContent["STACK_TRACE"]}")
//                        close()
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//            }
//        }
//    }
//}