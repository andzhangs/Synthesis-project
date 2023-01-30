package zs.android.module.widget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringChain
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemListDialogFragment : BottomSheetDialogFragment() {


    private var mListener: Listener? = null
    private lateinit var recyclerView: RecyclerView

    companion object {
        private val ARG_ITEM_COUNT = "item_count"

        fun newInstance(itemCount: Int): ItemListDialogFragment {
            return ItemListDialogFragment().apply {
                val args = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
                arguments = args
            }
        }
    }


    interface Listener {
        fun onItemClicked(position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_dialog_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(
            context,
            4,
            RecyclerView.VERTICAL,
            false
        )
        val adapter = ItemAdapter(requireArguments().getInt(ARG_ITEM_COUNT))
        recyclerView.adapter = adapter
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            Log.i("print_logs", "ItemListDialogFragment::onViewCreated: ")
            loadAnimation()
        }
    }

    inner class ItemAdapter(private val itemCount: Int) :
        RecyclerView.Adapter<ItemAdapter.ItemViewHolder?>() {

        override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
            itemViewHolder.mCompatTextView.setOnClickListener {
                mListener?.onItemClicked(
                    position
                )
            }
        }

        override fun getItemCount(): Int {
            return itemCount
        }

        inner class ItemViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            val mCompatTextView: AppCompatTextView

            init {
                mCompatTextView = itemView.findViewById(R.id.acTv_Img)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
            ItemViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_bottom_dialog_list_item, parent, false)
            )
    }

    fun loadAnimation() {
        if (recyclerView.childCount > 0) {
            setAnimations(recyclerView)
        }
    }

    private fun setAnimations(recyclerView: RecyclerView) {
        val springChain = SpringChain.create(50, 7, 35, 7)
        val childCount = recyclerView.childCount
        for (i in 0 until childCount) {
            val view = recyclerView.getChildAt(i)
            springChain.addSpring(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring) {
                    super.onSpringUpdate(spring)
                    Log.i("print_logs", "ItemListDialogFragment::onSpringUpdate: ")
                    view.translationY = spring.currentValue.toFloat()
                }

                override fun onSpringAtRest(spring: Spring) {
                    super.onSpringAtRest(spring)
                    Log.i("print_logs", "ItemListDialogFragment::onSpringAtRest: ")
                }

                override fun onSpringActivate(spring: Spring) {
                    super.onSpringActivate(spring)
                    Log.i("print_logs", "ItemListDialogFragment::onSpringActivate: ")
                }

                override fun onSpringEndStateChange(spring: Spring) {
                    Log.i("print_logs", "ItemListDialogFragment::onSpringEndStateChange: ")
                }
            })
        }
        val springs = springChain.allSprings
        for (i in springs.indices) {
            springs[i].currentValue = 400.0
        }
        /**
         * 从第几个子view开始
         */
        springChain.setControlSpringIndex(0)
        /**
         * 设置结束的位置
         */
        springChain.controlSpring.endValue = 0.0
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent: Fragment? = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

}