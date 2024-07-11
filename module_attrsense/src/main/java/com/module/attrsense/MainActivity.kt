package com.module.attrsense

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.module.attrsense.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    // 服务器 IP 地址
    private val HOST = "206.168.2.29"

    // SSH端口
    private val PORT = 51983

    // 账号
    private val ACCOUNT = "attrsense_mingzhe"

    // 密码
    private val PASSWORD = "attrsense"

    //远程文件路径
    private val ROOT_PATH = "/mnt/PUBLIC/mingzhe/testtflite/userdata/"

    //本地文件存在地址
    private val DOWNLOADS_FOLDER by lazy { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) }

    private lateinit var mLauncherreadWrite: ActivityResultLauncher<Array<String>>
    private lateinit var mLauncherAllFiles: ActivityResultLauncher<Intent>
    private val mLiveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mLauncherAllFiles =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        request()
                    } else {
                        Toast.makeText(this, "权限申请失败！", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        mLauncherreadWrite =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "读写权限: $it")
                }
                if (!it.values.contains(false)) {
                    request()
                }
            }

        mDataBinding.acBtnRequest.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    request()
                } else {
                    mLauncherAllFiles.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                        it.data = Uri.parse("package:${this.packageName}")
                    })
                }
            } else {
                mLauncherreadWrite.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }


        mDataBinding.acBtnClear.setOnClickListener {
            File(DOWNLOADS_FOLDER, "attrsense").also { file ->

                deleteFiles(file)

                file.delete()

                if (file.exists()) {
                    Toast.makeText(this@MainActivity, "文件清空失败!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "文件清空成功!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mLiveData.observe(this) {
            mDataBinding.acTvFolder.text = it

//            val file = File(DOWNLOADS_FOLDER, "attrsense").create()
//            try {
//                PrintWriter(FileWriter(File(file, "info.txt"))).apply {
//                    println(it)
//                    close()
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
        }
    }

    private fun deleteFiles(file: File){
        if (file.isDirectory) {
            file.listFiles()?.forEach { childFile->
                if (childFile.isDirectory) {
                    deleteFiles(childFile)
                }else{
                    file.delete()
                }
                childFile.delete()
            }
        }else{
            file.delete()
        }
    }

    private fun request() {
        thread {

            val sb = StringBuffer().append("扫描父级目录：").append("\n")

            try {
                val jsch = JSch()
                val session = jsch.getSession(ACCOUNT, HOST, PORT).apply {
                    this.setPassword(PASSWORD)
                    this.setConfig("StrictHostKeyChecking", "no")
                    this.connect()
                }
                val channelExec = session.openChannel("exec") as ChannelExec
                channelExec.setCommand("ls $ROOT_PATH")
                channelExec.connect()

                val inputStream = channelExec.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))

                while (true) {
                    val line = reader.readLine()
                    if (line != null) {
                        sb.append("\t\t$line").append("\n")
                    } else {
                        sb.append("扫描完成.").append("\n").append(">>>> 启动下载 <<<<")
                            .append("\n")
                        mLiveData.postValue(sb.toString())
                        break
                    }
                }
                reader.close()

                val channelSftp = session.openChannel("sftp") as ChannelSftp
                channelSftp.connect()


                channelSftp.ls(ROOT_PATH).forEach {
                    val entity = it as ChannelSftp.LsEntry
                    if (entity.attrs.isDir && !entity.filename.contains(".")) {
                        val parentFileName = entity.filename

                        val cacheFile = File(
                            DOWNLOADS_FOLDER,
                            "attrsense${File.separator}${parentFileName}"
                        ).create()

                        channelSftp.ls("${ROOT_PATH}$parentFileName").forEach { child ->
                            val childEntity = child as ChannelSftp.LsEntry
                            if (!childEntity.attrs.isDir) {
                                val fileName = childEntity.filename
                                val downloadFile =
                                    "${ROOT_PATH}${parentFileName}${File.separator}$fileName"

                                sb.append("$parentFileName：$fileName").append("\n")

                                mLiveData.postValue(sb.toString())

                                channelSftp.get(
                                    downloadFile,
                                    "${cacheFile}${File.separator}$fileName"
                                )
                            }
                        }

                    }
                }


                //下载文件
                channelSftp.exit()

                if (channelSftp.isConnected) {
                    channelSftp.disconnect()
                }

                if (channelExec.isConnected) {
                    channelExec.disconnect()
                }
                if (session.isConnected) {
                    session.disconnect()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "下载失败: $e")
                }
                sb.append("下载失败：$e")
                mLiveData.postValue(sb.toString())
            } finally {
                sb.append("<<<< 下载完成 >>>>")
                mLiveData.postValue(sb.toString())
            }
        }
    }


    private fun File.create(): File {
        if (!this.exists()) {
            this.mkdirs()
        }
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        mLauncherAllFiles.unregister()
        mLauncherreadWrite.unregister()
        mDataBinding.unbind()
    }
}