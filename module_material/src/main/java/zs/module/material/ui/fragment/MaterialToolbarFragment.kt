package zs.module.material.ui.fragment

import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment

class MaterialToolbarFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = MaterialToolbarFragment("MaterialToolbar")
    }

    override fun setLayoutRes(): Int = R.layout.fragment_material_toolbar
}