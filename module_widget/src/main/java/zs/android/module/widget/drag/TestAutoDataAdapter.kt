package zs.android.module.widget.drag

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import zs.android.module.widget.BuildConfig
import zs.android.module.widget.R
import zs.android.module.widget.drag.TestAutoDataAdapter.ViewHolder

/**
 *
 * @author zhangshuai
 * @date 2023/10/19 16:53
 * @mark 自定义类描述
 */
class TestAutoDataAdapter constructor(
    private val mContext: Context? = null,
    private val mDataSize: Int = 0
) : RecyclerView.Adapter<ViewHolder>() {

    private var mClickListener: ItemClickListener? = null
    private var mMultiSelectEnable = false
    private var mSelected: HashSet<Int> = HashSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.item_drag_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvText.text = position.toString()
        holder.ivSelect.visibility = if (mMultiSelectEnable) View.VISIBLE else View.GONE
        if (mSelected.contains(position)) {
            holder.ivSelect.setImageResource(R.drawable.icon_selected)
        } else {
            holder.ivSelect.setImageResource(R.drawable.icon_unselected)
        }
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "TestAutoDataAdapter2::onBindViewHolder: $position")
        }
    }

    override fun getItemCount(): Int {
        return mDataSize
    }

    // ----------------------
    // Selection
    // ----------------------

    // ----------------------
    // Selection
    // ----------------------
    fun toggleSelection(pos: Int) {
        if (mSelected.contains(pos)) {
            mSelected.remove(pos)
        } else {
            mSelected.add(pos)
        }
        notifyItemChanged(pos)
    }

    fun select(pos: Int, selected: Boolean) {
        if (selected) {
            mSelected.add(pos)
        } else {
            mSelected.remove(pos)
        }
        notifyItemChanged(pos)
    }

    fun selectRange(start: Int, end: Int, selected: Boolean) {
        for (i in start..end) {
            if (selected) {
                mSelected.add(i)
            } else {
                mSelected.remove(i)
            }
        }
        notifyItemRangeChanged(start, end - start + 1)
    }

    fun deselectAll() {
        // this is not beautiful...
        mSelected.clear()
        notifyDataSetChanged()
    }

    fun selectAll() {
        for (i in 0 until mDataSize) {
            mSelected.add(i)
        }
        notifyDataSetChanged()
    }

    fun getCountSelected(): Int {
        return mSelected.size
    }

    fun getSelection(): HashSet<Int> {
        return mSelected
    }

    fun setMultiSelectEnable(enable: Boolean) {
        mMultiSelectEnable = enable
        notifyDataSetChanged()
    }

    fun isMultiSelect() = mMultiSelectEnable

    // ----------------------
    // Click Listener
    // ----------------------

    // ----------------------
    // Click Listener
    // ----------------------
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemLongClick(view: View?, position: Int): Boolean
    }

    // ----------------------
    // ViewHolder
    // ----------------------

    // ----------------------
    // ViewHolder
    // ----------------------
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        var tvText: TextView
        val ivSelect: AppCompatImageView

        init {
            tvText = itemView.findViewById<TextView>(R.id.tvText)
            ivSelect = itemView.findViewById<AppCompatImageView>(R.id.acIv_select)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            return mClickListener?.onItemLongClick(view, adapterPosition) ?: false
        }
    }
}
