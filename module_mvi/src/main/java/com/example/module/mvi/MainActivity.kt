package com.example.module.mvi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.module.mvi.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mBinding.lifecycleOwner=this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                render(state)
            }
        }

        viewModel.handlerIntent(MainIntent.loadAllData)
        
        //模拟用户Intent
        mBinding.acBtnSearch.setOnClickListener {
            val keyword = mBinding.acEtContent.text.toString().trim()
            viewModel.handlerIntent(MainIntent.Search(keyword))
        }

    }

    private fun render(state: MainViewState) {
        //渲染UI
        mBinding.loadingView.isVisible = state.loading
        if (state.error != null) {
            Toast.makeText(this, "加载失败：${state.error}", Toast.LENGTH_SHORT).show()
        }else{
            if (state.data.isNotEmpty()) {
                mBinding.acTvResult.text = state.data.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}