package zs.module.material.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment
import java.util.Random


class ChipFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    override fun setLayoutRes() = R.layout.fragment_chip

    companion object {
        @JvmStatic
        fun newInstance() = ChipFragment("Chip")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup2 = mRootView.findViewById<ChipGroup>(R.id.chip_group_2)

        for (i in 1..10) {
            val chipItemView = layoutInflater.inflate(R.layout.item_chip, null, false)
            val chipView = chipItemView.findViewById<Chip>(R.id.chip)
            chipView.apply {
                text = "标签.${i * Random().nextInt(100)}"
                setOnCloseIconClickListener {
                    Toast.makeText(requireContext(), "移出了：$text", Toast.LENGTH_SHORT).show()
                    chipGroup2.removeView(chipItemView)
                }

                setOnClickListener {
                    Toast.makeText(requireContext(), "点击了：$text", Toast.LENGTH_SHORT).show()
                    chipGroup2.removeView(chipItemView)
                    chipGroup2.addView(chipItemView, 0)
                }
            }

            chipGroup2.addView(chipItemView)
        }
    }
}