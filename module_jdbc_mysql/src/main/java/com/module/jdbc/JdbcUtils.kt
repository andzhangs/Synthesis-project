package com.module.jdbc

import android.os.StrictMode
import android.util.Log
import java.sql.DriverManager

object JdbcUtils {

    private const val TAG = "JdbcUtils"

    @JvmStatic
    fun init(block: (String) -> Unit) {
        try {
            //当前电脑网络ip
            val ip = "206.168.2.39:3306"
            val user = "root"
            val password = "zxcvbnm,."
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            // com.mysql.jdbc.Driver
            // com.mysql.cj.jdbc.Driver
            Class.forName("com.mysql.jdbc.Driver")
            val url =
                "jdbc:mysql://${ip}/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
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
                Log.i(TAG, "输出: $User, $Host")
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
}