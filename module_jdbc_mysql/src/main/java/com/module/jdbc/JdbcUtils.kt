package com.module.jdbc

import android.content.Context
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import com.jcraft.jsch.JSch
import java.io.File
import java.sql.DriverManager
import java.util.Properties


object JdbcUtils {

    private const val TAG = "print_logs"

    /**
     * 先要启动本地mysql环境
     * 启动：sudo mysql.server start
     * 关闭：sudo mysql.server stop
     * 重启：sudo mysql.server restart
     *
     * 本地数据地址：/Users/zhangshuai/Library/Application Support/PremiumSoft CyberTech/Navicat CC/Common/Settings/0/0/MySQL/localhost_mysql
     */
    @JvmStatic
    fun init(block: (String) -> Unit) {
        try {
            //当前电脑网络ip
            val ip = "206.168.2.57:3306"
            val user = "root"
            val password = "zxcvbnm,."
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            Class.forName("com.mysql.jdbc.Driver")
            val url =
                "jdbc:mysql://$ip/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            var connection = DriverManager.getConnection(url, user, password)
            //执行查询
            val statement = connection.createStatement()
            val sql = "SELECT * FROM user"
            val resultSet = statement.executeQuery(sql)
            //处理结果
            val stringBuilder = StringBuilder()
            while (resultSet.next()) {
                val User = resultSet.getString("User")
                val Host = resultSet.getString("Host")
                Log.i(TAG, "打印: $User, $Host")
                stringBuilder.append("User： $User").append("Host： $Host").append("\n")
                block(stringBuilder.toString())
            }
            resultSet.close()
            statement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "异常: $e")
            block(e.toString())
        }
    }

    @JvmStatic
    fun load(context: Context, block: (String) -> Unit) {
        try {

            val fileCache =
                "${context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}${File.separator}pem"
            val file = File(fileCache)
            if (!file.exists()) {
                file.mkdirs()
            }

            val privateKeyFilePath =
                "${file.path}${File.separator}${context.assets.list("")?.get(0).toString()}"


            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "私钥文件是否存在：${File(privateKeyFilePath).exists()}，$privateKeyFilePath"
                )
            }


            val sshUserName = "root"
            val sshHost = "47.102.44.105"
            val sshPort = 22

            //创建SSH会话
            val jsch = JSch()
            val privateKey = jsch.getSession(sshUserName, sshHost, sshPort)
            jsch.addIdentity(privateKeyFilePath,"QwWrSMZST16jLO2U")

            val sessionConfig = Properties()
            sessionConfig.setProperty("StrictHostKeyChecking", "no")
            privateKey.setConfig(sessionConfig)
            privateKey.connect()


            val localPort = 3306
            val remoteHost = "localhost_3306"
            val remotePort = 3306

            // 2. 将 SSH 隧道绑定到本地端口
            val assignedPort = privateKey.setPortForwardingL(localPort, remoteHost, remotePort)

            val ip = "206.168.2.57:$assignedPort"

            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "SSH连接成功：${privateKey.isConnected}, $ip")
            }

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            kotlin.runCatching {
                val jdbcUrl =
                    "jdbc:mysql://$ip/mysql?connectTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"

                Class.forName("com.mysql.jdbc.Driver")

                DriverManager.getConnection(jdbcUrl, "debian-sys-maint", "QwWrSMZST16jLO2U")
            }.onSuccess {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "JdbcUtils::load: 连接成功！")
                }
                //执行查询
                val statement = it.createStatement()
                val sql = "SELECT * FROM user"
                val resultSet = statement.executeQuery(sql)
                //处理结果
                val stringBuilder = StringBuilder()
                while (resultSet.next()) {
                    val user = resultSet.getString("User")
                    val host = resultSet.getString("Host")
                    Log.i(TAG, "输出: $user, $host")
                    stringBuilder.append("User： $user").append("Host： $host").append("\n")
                    block(stringBuilder.toString())
                }

                resultSet.close()
                statement.close()
                it.close()
            }.onFailure {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "连接失败: $it")
                }
            }
            privateKey.disconnect()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "异常: $e")
            block(e.toString())
        }
    }
}