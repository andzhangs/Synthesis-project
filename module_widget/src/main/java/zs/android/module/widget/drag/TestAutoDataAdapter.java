package zs.android.module.widget.drag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;

import zs.android.module.widget.R;

/**
 * @author zhangshuai
 * @date 2023/10/19 15:06
 * @mark 自定义类描述
 */
public class TestAutoDataAdapter extends RecyclerView.Adapter<TestAutoDataAdapter.ViewHolder> {

    private int mDataSize;
    private Context mContext;
    private ItemClickListener mClickListener;

    boolean misLongClick = false;

    private HashSet<Integer> mSelected;

    public TestAutoDataAdapter(Context context, int size) {
        mContext = context;
        mDataSize = size;
        mSelected = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_drag_select, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvText.setText(String.valueOf(position));


        holder.ivSelect.setVisibility(misLongClick ? View.VISIBLE : View.GONE);

        if (mSelected.contains(position)) {
//            holder.tvText.setBackgroundColor(Color.RED);
            holder.ivSelect.setImageResource(R.drawable.icon_selected);
        } else {
//            holder.tvText.setBackgroundColor(Color.WHITE);
            holder.ivSelect.setImageResource(R.drawable.icon_unselected);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSize;
    }

    // ----------------------
    // Selection
    // ----------------------

    public void toggleSelection(int pos) {
        if (mSelected.contains(pos)) {
            mSelected.remove(pos);
        } else {
            mSelected.add(pos);
        }
        notifyItemChanged(pos);
    }

    public void select(int pos, boolean selected) {
        if (selected) {
            mSelected.add(pos);
        } else {
            mSelected.remove(pos);
        }
        notifyItemChanged(pos);
    }

    public void selectRange(int start, int end, boolean selected) {
        for (int i = start; i <= end; i++) {
            if (selected) {
                mSelected.add(i);
            } else {
                mSelected.remove(i);
            }
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public void deselectAll() {
        // this is not beautiful...
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < mDataSize; i++) {
            mSelected.add(i);
        }
        notifyDataSetChanged();
    }

    public int getCountSelected() {
        return mSelected.size();
    }

    public HashSet<Integer> getSelection() {
        return mSelected;
    }

    public void setLong(boolean isLongClick) {
        this.misLongClick = isLongClick;
        notifyDataSetChanged();
    }

    // ----------------------
    // Click Listener
    // ----------------------

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    // ----------------------
    // ViewHolder
    // ----------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tvText;
        private AppCompatImageView ivSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            ivSelect = itemView.findViewById(R.id.acIv_select);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {

                return mClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }
}
