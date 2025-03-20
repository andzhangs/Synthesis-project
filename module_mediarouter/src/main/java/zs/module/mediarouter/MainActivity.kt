package zs.module.mediarouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import zs.module.mediarouter.databinding.ActivityMainBinding

/**
 * 主要用于媒体路由，例如将音频或视频投射到外部设备（如 Chromecast、蓝牙音箱等）
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private lateinit var mMediaRouter: MediaRouter

    //// 创建一个媒体路由选择器，用于指定要搜索的媒体类型
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
        /**
         * 当用户选择一个媒体路由时触发
         */
        override fun onRouteSelected(
            router: MediaRouter,
            route: MediaRouter.RouteInfo,
            reason: Int
        ) {
            super.onRouteSelected(router, route, reason)
        }

        /**
         * 当用户取消选择一个媒体路由时触发
         */
        override fun onRouteUnselected(
            router: MediaRouter,
            route: MediaRouter.RouteInfo,
            reason: Int
        ) {
            super.onRouteUnselected(router, route, reason)
        }

        /**
         * 当发现新的媒体路由时触发
         */
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
            super.onRouteAdded(router, route)

        }

        /**
         * 当媒体路由被移除时触发
         */
        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
            super.onRouteRemoved(router, route)
        }
    }

    override fun onStart() {
        // 注册媒体路由回调
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