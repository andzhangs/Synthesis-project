package zs.android.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UriSchemeActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            loadURIScheme(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uri_scheme)

        loadURIScheme(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun loadURIScheme(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                if (it.scheme == "test-uri") {
                    if (it.host == "test.uri.com") {  //打开相册广场详情页
                        if (it.path == "testpath1") {

                        }
                    }
                }
            }
        }
    }
}