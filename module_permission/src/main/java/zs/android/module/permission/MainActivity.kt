package zs.android.module.permission

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startIntentSenderForResult: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var openDocument: ActivityResultLauncher<Array<String>>
    private lateinit var openMultipleDocuments: ActivityResultLauncher<Array<String>>
    private lateinit var openDocumentTree: ActivityResultLauncher<Uri?>
    private lateinit var takePicturePreview: ActivityResultLauncher<Void?>
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var captureVideo: ActivityResultLauncher<Uri>
    private lateinit var pickContact: ActivityResultLauncher<Void?>
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var createDocument: ActivityResultLauncher<String>
    private lateinit var getMultipleContents: ActivityResultLauncher<String>

    private lateinit var mImageView: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPictureClick()
        setCallback()

        mImageView = findViewById(R.id.acIv)
    }

    private fun setCallback() {
        //常规跳转，下一页面调用方法：setResult(RESULT_OK)返回，触发回调！
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.i("print_logs", "startActivityForResult 跳转成功！")
                } else {
                    Log.e("print_logs", "startActivityForResult 跳转失败！")
                }
            }
        findViewById<AppCompatButton>(R.id.onStartActivityForResult).setOnClickListener {
            startActivityForResult.launch(
                Intent(this, SubActivity::class.java)
            )
        }


        startIntentSenderForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.i("print_logs", "startIntentSenderForResult")
                }
            }
        findViewById<AppCompatButton>(R.id.onStartIntentSenderForResult).setOnClickListener {
//            startIntentSenderForResult.launch(IntentSenderRequest.Builder().build())
        }


        //用于请求单个权限
        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.i("print_logs", "requestPermission 同意：WRITE_EXTERNAL_STORAGE")

            } else {
                Log.e("print_logs", "requestPermission 拒绝：WRITE_EXTERNAL_STORAGE")

            }
        }
        findViewById<AppCompatButton>(R.id.onRequestPermission).setOnClickListener {
            requestPermission.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        //用于请求一组权限
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions 同意：READ_EXTERNAL_STORAGE"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions 拒绝：READ_EXTERNAL_STORAGE"
                    )
                }

                if (it[Manifest.permission.CAMERA]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions 同意：CAMERA"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions 拒绝：CAMERA"
                    )
                }

                if (it[Manifest.permission.READ_CONTACTS]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions 同意：READ_CONTACTS"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions 拒绝：READ_CONTACTS"
                    )
                }

                if (it[Manifest.permission.WRITE_CONTACTS]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions 同意：WRITE_CONTACTS"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions 拒绝：WRITE_CONTACTS"
                    )
                }

                if (it[Manifest.permission.RECORD_AUDIO]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions 同意：RECORD_AUDIO"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions 拒绝：RECORD_AUDIO"
                    )
                }
            }

        findViewById<AppCompatButton>(R.id.onRequestMultiplePermissions).setOnClickListener {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }

        //提示用户选择一个文档，返回一个(file:/http:/content:)开头的Uri。
        createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "createDocument: ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onCreateDocument).setOnClickListener {
            createDocument.launch(
                null
            )
        }

        //提示用户选择文档（可以选择一个），分别返回它们的Uri
        openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                val path = Uri2PathUtil.getRealPathFromUri(this, it)
                Log.i("print_logs", "openDocument $path")
            }
        }
        findViewById<AppCompatButton>(R.id.onOpenDocument).setOnClickListener {
            openDocument.launch(
                arrayOf("image/*", "text/plain")
            )
        }


        //提示用户选择文档（可以选择多个），分别返回它们的Uri，以List的形式。
        openMultipleDocuments =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
                it.forEach { uri ->
                    if (uri != null) {
                        Log.i("print_logs", "openMultipleDocuments $uri")
                    }
                }
            }
        findViewById<AppCompatButton>(R.id.onOpenMultipleDocuments).setOnClickListener {
            openMultipleDocuments.launch(
                arrayOf("image/*", "text/plain")
            )
        }


        //提示用户选择一个目录，并返回用户选择的作为一个Uri返回，应用程序可以完全管理返回目录中的文档。
        openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "openDocumentTree ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onOpenDocumentTree).setOnClickListener {
            openDocumentTree.launch(
                null
            )
        }


        //调用MediaStore.ACTION_IMAGE_CAPTURE拍照，返回值为Bitmap图片
        takePicturePreview =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                Log.i("print_logs", "takePicturePreview: $it")

                with(mImageView) {
                    visibility = View.VISIBLE
                    setImageBitmap(it)
                }
            }
        findViewById<AppCompatButton>(R.id.onTakePicturePreview).setOnClickListener {
            takePicturePreview.launch(null)
        }


        var uri: Uri? = null
        //调用MediaStore.ACTION_IMAGE_CAPTURE拍照，并将图片保存到给定的Uri地址，返回true表示保存成功
        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.i("print_logs", "takePicture: $it")
            if (it) {
                with(mImageView) {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
            }
        }

        findViewById<AppCompatButton>(R.id.onTakePicture).setOnClickListener {
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "${System.currentTimeMillis()}_test.jpg"
                    )
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
            takePicture.launch(
                uri
            )
        }


        //调用MediaStore.ACTION_VIDEO_CAPTURE 拍摄视频，保存到给定的Uri地址，返回一张缩略图
        captureVideo = registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
            Log.i("print_logs", "takeVideo: $it")
        }
        findViewById<AppCompatButton>(R.id.onCaptureVideo).setOnClickListener {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "${System.currentTimeMillis()}_test.mp4"
                    )
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                }
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
            captureVideo.launch(
                uri
            )
        }


        //从通讯录APP获取联系人
        pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "pickContact: $it")
                try {
                    val cursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
                    )
                    if (cursor != null) {  //当游标不为空
                        while (cursor.moveToNext()) {
                            val name =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            val number =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.i("print_logs", "MainActivity::onCreate: $name, $number")
                        }
                    }
                    cursor?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        findViewById<AppCompatButton>(R.id.onPickContact).setOnClickListener {
            pickContact.launch(null)
        }

        //提示用选择一条内容，返回一个通过ContentResolver#openInputStream(Uri)访问原生数据的Uri地址（content://形式）。
        //默认情况下，它增加了Intent#CATEGORY_OPENABLE, 返回可以表示流的内容。
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "getContent: ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onGetContent).setOnClickListener {
            getContent.launch("text/plain")
        }


        getMultipleContents =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                if (it.isNotEmpty()) {
                    it.forEach { uri ->
                        if (it != null && !TextUtils.isEmpty(uri.path)) {
                            Log.i("print_logs", "getMultipleContents: ${uri.path}")
                        }
                    }
                }
            }
        findViewById<AppCompatButton>(R.id.onGetMultipleContents).setOnClickListener {
            getMultipleContents.launch(
                "text/plain"
            )
        }
    }


    private fun setPictureClick() {

        findViewById<AppCompatButton>(R.id.acBtn_getImage).setOnClickListener {
            getImage(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getSystemPicture).setOnClickListener {
            getSystemPicture(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getCamera).setOnClickListener {
            getCamera(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getAlbum).setOnClickListener {
            getAlbum(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getAllAlbum).setOnClickListener {
            getAllAlbum(it)
        }
    }

    //获取图片
    private fun getImage(view: View) {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //使用系统相册
    private fun getSystemPicture(view: View) {
        PictureSelector.create(this)
            .openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //拍照
    private fun getCamera(view: View) {
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //专辑列表
    private fun getAlbum(view: View) {
        PictureSelector.create(this)
            .dataSource(SelectMimeType.ofImage())
            .obtainAlbumData { result ->
                result.forEach {

                    Log.i("print_logs", "文件夹：${it?.firstImagePath}, ${it.data.size}")
                    it.data.forEach { file->
                        Log.i("print_logs", "专辑列表: $file")
                    }
                }
            }
    }

    //相册列表
    private fun getAllAlbum(view: View) {
        PictureSelector.create(this)
            .dataSource(SelectMimeType.ofAll())
            .obtainMediaData { result ->
                result.forEach {
                    Log.i("print_logs", "图片: ${it?.realPath}")
                }
            }
    }
}