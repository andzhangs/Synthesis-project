package zs.module.material.ui.fragment

import android.os.Bundle
import android.view.View
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment


/**
 * 参考链接：
 * https://blog.csdn.net/qq_44950283/article/details/128522029
 * https://www.jianshu.com/p/20dbe17d8c91
 */
class MaterialToolbarFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = MaterialToolbarFragment("MaterialToolbar")
    }

    override fun setLayoutRes(): Int = R.layout.fragment_material_toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}