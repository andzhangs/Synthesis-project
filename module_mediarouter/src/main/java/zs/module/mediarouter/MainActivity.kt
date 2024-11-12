package zs.module.mediarouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import zs.module.mediarouter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private lateinit var mMediaRouter: MediaRouter
    private val mSelector by lazy {
        MediaRouteSelector.Builder()
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this
        mMediaRouter = MediaRouter.getInstance(this)

        mDataBinding.mediaButton.routeSelector = mSelector
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.menu_media_router_item, menu)
//        val mediaRouterActionProvider = menu?.findItem(R.id.media_router_menu_item)?.let {
//            MenuItemCompat.getActionProvider(it) as? MediaRouteActionProvider
//        }
//        mediaRouterActionProvider?.routeSelector = mSelector
//        return true
//    }

    private val mMediaRouterCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(
            router: MediaRouter,
            route: MediaRouter.RouteInfo,
            reason: Int
        ) {
            super.onRouteSelected(router, route, reason)
        }

        override fun onRouteUnselected(
            router: MediaRouter,
            route: MediaRouter.RouteInfo,
            reason: Int
        ) {
            super.onRouteUnselected(router, route, reason)
        }
    }

    override fun onStart() {
        mMediaRouter.addCallback(
            mSelector,
            mMediaRouterCallback,
            MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
        )
        super.onStart()
    }

    override fun onPause() {
        mMediaRouter.removeCallback(mMediaRouterCallback)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}