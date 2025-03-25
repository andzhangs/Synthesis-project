package com.module.section.drag

/**
 *
 * @author zhangshuai
 * @date 2023/10/19 16:45
 * @mark https://github.com/MFlisar/DragSelectRecyclerView/tree/2350e2888e04af9cf55d64ae7d9843d7adc2b85a
 */
class DragSelectionProcessor constructor(private val mSelectionHandler: ISelectionHandler? = null) :
    DragSelectTouchListener.OnAdvancedDragSelectListener {

    /**
     * Different existing selection modes
     */
    enum class Mode {
        /**
         * simply selects each item you go by and unselects on move back
         * 只需选择所经过的每个项目，然后在向后移动时取消选择
         */
        Simple,

        /**
         * toggles each items original state, reverts to the original state on move back
         * 切换每个项目的原始状态，在向后移动时恢复到原始状态
         */
        ToggleAndUndo,

        /**
         * toggles the first item and applies the same state to each item you go by and applies inverted state on move back
         * 切换第一个项目并将相同的状态应用于所经过的每个项目，并在向后移动时应用反向状态
         */
        FirstItemDependent,

        /**
         * toggles the item and applies the same state to each item you go by and reverts to the original state on move back
         * 切换项目并将相同的状态应用于所经过的每个项目，并在向后移动时恢复到原始状态
         */
        FirstItemDependentToggleAndUndo
    }

    var mMode: Mode? = Mode.Simple
    var mStartFinishedListener: ISelectionStartFinishedListener? = null
    var mOriginalSelection: HashSet<Int>? = null
    var mFirstWasSelected = false
    var mCheckSelectionState = false

    /**
     * @param mode the mode in which the selection events should be processed
     * @return this
     */
    fun withMode(mode: Mode?): DragSelectionProcessor {
        mMode = mode
        return this
    }

    /**
     * @param startFinishedListener a listener that get's notified when the drag selection is started or finished
     * @return this
     */
    fun withStartFinishedListener(startFinishedListener: ISelectionStartFinishedListener?): DragSelectionProcessor {
        mStartFinishedListener = startFinishedListener
        return this
    }

    /**
     * If this is enabled, the processor will check if an items selection state is toggled before notifying the [ISelectionHandler]
     * @param check true, if this check should be enabled
     * @return this
     */
    fun withCheckSelectionState(check: Boolean): DragSelectionProcessor {
        mCheckSelectionState = check
        return this
    }

    override fun onSelectionStarted(start: Int) {
        mOriginalSelection = HashSet()
        val selected: Set<Int>? = mSelectionHandler?.selection
        if (selected != null) {
            mOriginalSelection!!.addAll(selected)
        }
        mFirstWasSelected = mOriginalSelection!!.contains(start)
        when (mMode) {
            Mode.Simple -> {
                mSelectionHandler?.updateSelection(start, start, true, true)
            }

            Mode.ToggleAndUndo -> {
                mSelectionHandler?.updateSelection(
                    start,
                    start,
                    !mOriginalSelection!!.contains(start),
                    true
                )
            }

            Mode.FirstItemDependent, Mode.FirstItemDependentToggleAndUndo -> {
                mSelectionHandler?.updateSelection(start, start, !mFirstWasSelected, true)
            }

            else -> {}
        }
        mStartFinishedListener?.onSelectionStarted(start, mFirstWasSelected)
    }

    override fun onSelectionFinished(end: Int) {
        mOriginalSelection = null
        mStartFinishedListener?.onSelectionFinished(end)
    }

    override fun onSelectChange(start: Int, end: Int, isSelected: Boolean) {
        when (mMode) {
            Mode.Simple -> {
                if (mCheckSelectionState) {
                    checkedUpdateSelection(start, end, isSelected)
                } else {
                    mSelectionHandler?.updateSelection(start, end, isSelected, false)
                }
            }

            Mode.ToggleAndUndo -> {
                var i = start
                while (i <= end) {
                    checkedUpdateSelection(
                        i,
                        i,
                        if (isSelected) !mOriginalSelection!!.contains(i) else mOriginalSelection!!.contains(
                            i
                        )
                    )
                    i++
                }
            }

            Mode.FirstItemDependent -> {
                checkedUpdateSelection(
                    start,
                    end,
                    if (isSelected) !mFirstWasSelected else mFirstWasSelected
                )
            }

            Mode.FirstItemDependentToggleAndUndo -> {
                var i = start
                while (i <= end) {
                    checkedUpdateSelection(
                        i,
                        i,
                        if (isSelected) !mFirstWasSelected else mOriginalSelection!!.contains(i)
                    )
                    i++
                }
            }

            else -> {}
        }
    }

    open fun checkedUpdateSelection(start: Int, end: Int, newSelectionState: Boolean) {
        mSelectionHandler?.also {
            if (mCheckSelectionState) {
                for (i in start..end) {
                    if (it.isSelected(i) != newSelectionState) {
                        it.updateSelection(i, i, newSelectionState, false)
                    }
                }
            } else {
                it.updateSelection(start, end, newSelectionState, false)
            }
        }

    }

    interface ISelectionHandler {
        /**
         * @return the currently selected items => can be ignored for [Mode.Simple] and [Mode.FirstItemDependent]
         */
        val selection: HashSet<Int>

        /**
         * only used, if [DragSelectionProcessor.withCheckSelectionState] was enabled
         * @param index the index which selection state wants to be known
         * @return the current selection state of the passed in index
         */
        fun isSelected(index: Int): Boolean

        /**
         * update your adapter and select select/unselect the passed index range, you be get a single for all modes but [Mode.Simple] and [Mode.FirstItemDependent]
         *
         * @param start      the first item of the range who's selection state changed
         * @param end         the last item of the range who's selection state changed
         * @param isSelected      true, if the range should be selected, false otherwise
         * @param calledFromOnStart true, if it was called from the [DragSelectionProcessor.onSelectionStarted] event
         */
        fun updateSelection(start: Int, end: Int, isSelected: Boolean, calledFromOnStart: Boolean)
    }

    interface ISelectionStartFinishedListener {
        /**
         * @param start      the item on which the drag selection was started at
         * @param originalSelectionState the original selection state
         */
        fun onSelectionStarted(start: Int, originalSelectionState: Boolean)

        /**
         * @param end      the item on which the drag selection was finished at
         */
        fun onSelectionFinished(end: Int)
    }
}