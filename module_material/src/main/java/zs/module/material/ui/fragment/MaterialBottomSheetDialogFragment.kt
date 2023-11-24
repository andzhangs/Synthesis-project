package zs.module.material.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment
import zs.module.material.ui.activity.BottomSheetDragHandleActivity

class MaterialBottomSheetDialogFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = MaterialBottomSheetDialogFragment("BottomSheetDialog")
    }

    override fun setLayoutRes(): Int = R.layout.fragment_material_bottom_sheet_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mRootView.findViewById<AppCompatButton?>(R.id.acBtn_BottomSheetDialog).setOnClickListener {
            createBottomSheetDialog()
        }
        mRootView.findViewById<AppCompatButton?>(R.id.acBtn_BottomSheetDialogFragment)
            .setOnClickListener {
                createBottomSheetDialogFragment()
            }

        mRootView.findViewById<AppCompatButton?>(R.id.acBtn_BottomSheetBehavior)
            .setOnClickListener {
                startActivity(Intent(requireActivity(), BottomSheetDragHandleActivity::class.java))
            }
    }

    private fun createBottomSheetDialog() {
        BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_item)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setOnCancelListener {
                Log.i(
                    "print_logs",
                    "MaterialBottomSheetDialogFragment::createBottomSheetDialog: setOnCancelListener"
                )
            }
            setOnDismissListener {
                Log.i(
                    "print_logs",
                    "MaterialBottomSheetDialogFragment::createBottomSheetDialog: setOnDismissListener"
                )
            }
            create()
            show()
        }
    }

    private fun createBottomSheetDialogFragment() {
        MyBottomSheetDialogFragment().show(childFragmentManager, "dialog_fragment")
    }


    class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {

        private lateinit var mView: View
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            mView = inflater.inflate(R.layout.layout_dialog_item, container, false)
            return mView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        }
    }
}