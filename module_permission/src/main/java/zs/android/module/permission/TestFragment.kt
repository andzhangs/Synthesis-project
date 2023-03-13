package zs.android.module.permission

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/14 17:58
 * @description
 */
class TestFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        }.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }
}