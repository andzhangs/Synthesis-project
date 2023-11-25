package zs.module.material

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import zs.module.material.base.BaseMaterialFragment
import zs.module.material.databinding.ActivityMainBinding
import zs.module.material.ui.fragment.*

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mList: List<Fragment>
    private lateinit var mMediator: TabLayoutMediator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mList = arrayListOf(
            CircularRevealFragment.newInstance(),
            ShapeableImageViewFragment.newInstance(),
            MaterialShapeDrawableFragment.newInstance(),
            ChipFragment.newInstance(),
            MaterialToolbarFragment.newInstance(),
            BottomNavigationViewFragment.newInstance(),
            MaterialBottomSheetDialogFragment.newInstance(),
            SliderFragment.newInstance(),
            SwipeDismissBehaviorFragment.newInstance()
        )
        mList.forEach { _ -> mBinding.tabLayout.addTab(mBinding.tabLayout.newTab()) }

        mBinding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = mList.size
            override fun createFragment(position: Int): Fragment = mList[position]
        }

        mMediator = TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewPager2,
        ) { tab, position ->
            tab.text = (mList[position] as BaseMaterialFragment).getIndexName()
        }.apply {
            attach()
        }

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.i("print_logs", "MainActivity::onTabSelected: ${tab?.text}")
                sentIntent("I'm from MainActivity::onTabSelected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.i("print_logs", "MainActivity::onTabUnselected: ${tab?.text}")
                sentIntent("I'm from MainActivity::onTabUnselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i("print_logs", "MainActivity::onTabReselected: ${tab?.text}")
                sentIntent("I'm from MainActivity::onTabReselected")
            }
        })

        mBinding.tabLayout.getTabAt(0)?.let {
            it.orCreateBadge.apply {
                backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.teal_200)
                badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
                number = 6
            }
        }
        mBinding.tabLayout.getTabAt(1)?.let {
            it.orCreateBadge.apply {
                backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.purple_700)
                badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
                number = 10
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          本地广播
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    private val RECEIVER_ACTION = "zs.module.material.action"

    private fun sentIntent(msg: String) {
        val intent = Intent(RECEIVER_ACTION).apply {
            putExtra("data", Bundle().also {
                it.putString("msg", msg)
            })
            putExtras(Bundle().apply {
                putString("msg2", msg)
            })
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == RECEIVER_ACTION) {
                    val bundle = it.getBundleExtra("data")
                    Log.d("print_logs", "onReceive: $bundle")

                    val msg = bundle?.getString("msg")

                    val msg2 = it.getStringExtra("msg2")
                    Log.w("print_logs", "MainActivity::onReceive: $msg, $msg2")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter().apply {
            addAction(RECEIVER_ACTION)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediator.detach()
    }
}