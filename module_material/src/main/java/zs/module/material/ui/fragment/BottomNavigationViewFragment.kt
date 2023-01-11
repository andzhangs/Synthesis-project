package zs.module.material.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment
import zs.module.material.ui.activity.DragActivity

class BottomNavigationViewFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = BottomNavigationViewFragment("BottomNavigationView")
    }

    private lateinit var mNavigationView: BottomNavigationView

    override fun setLayoutRes(): Int = R.layout.fragment_bottom_navigation_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mRootView.findViewById<AppCompatButton>(R.id.acBtn_jump).setOnClickListener {
            startActivity(Intent(activity, DragActivity::class.java))
        }
        mNavigationView = mRootView.findViewById(R.id.navigation_view)
        mNavigationView.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.action_search -> {
                    Log.i("print_logs", "setOnItemReselectedListener:: action_search")
                }
                R.id.action_settings -> {
                    Log.i("print_logs", "setOnItemReselectedListener:: action_settings")
                }
                R.id.action_navigation -> {
                    Log.i("print_logs", "setOnItemReselectedListener:: action_navigation")
                }
                else -> {}
            }
        }

        //选中同一个
        mNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_search -> {
                    Log.i("print_logs", "setOnItemSelectedListener:: action_search")

                }
                R.id.action_settings -> {
                    Log.i("print_logs", "setOnItemSelectedListener:: action_settings")
                }
                R.id.action_navigation -> {
                    Log.i("print_logs", "setOnItemSelectedListener:: action_navigation")
                }
                else -> {}
            }
            true
        }
    }
}