package be.helmo.myscout.view.phases.phaselist

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter
import kotlin.math.absoluteValue

class ItemTouchHelperCallback(val adapter: IItemTouchHelperAdapter, val phasePresenter: IPhaseRecyclerCallbackPresenter) : ItemTouchHelper.SimpleCallback(0, 0) {

    var dragFrom = -1
    var dragTo = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, ItemTouchHelper.LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        if (dragFrom == -1) {
            dragFrom = fromPosition
        }
        dragTo = toPosition

        adapter.onItemMove(fromPosition, toPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if(direction == ItemTouchHelper.LEFT) {
            val swipedItemPosition = viewHolder.adapterPosition

            adapter.onItemRemove(swipedItemPosition)
            phasePresenter.removePhaseAt(swipedItemPosition)
        }
    }

    //transition couleur
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val background = ColorDrawable(ContextCompat.getColor(itemView.context, R.color.red))
            val iconView = viewHolder.itemView.findViewById<ImageView>(R.id.delete_icon2)

            if (dX < 0) {
                iconView.visibility = View.VISIBLE

                val swipeWidthPercentage = (dX.absoluteValue * 2 / itemView.width.toFloat()).coerceIn(0f, 1f)

                ValueAnimator.ofInt(0, (255 * swipeWidthPercentage).toInt()).apply {
                    duration = 0
                    addUpdateListener { animator ->
                        val alpha = animator.animatedValue as Int
                        background.alpha = alpha
                        itemView.setBackgroundColor(background.color)
                    }
                    start()
                }

                ValueAnimator.ofFloat(0f, swipeWidthPercentage).apply {
                    duration = 0
                    addUpdateListener { animator ->
                        val alpha = animator.animatedValue as Float
                        iconView.alpha = alpha
                    }
                    start()
                }

                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

            } else {
                iconView.visibility = View.GONE
                iconView.alpha = 0f
                background.alpha = 0
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is IItemTouchHelperViewHolder) {
                viewHolder.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is IItemTouchHelperViewHolder) {
            viewHolder.onItemClear()
        }

        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            adapter.onItemDragEnd(dragFrom, dragTo)
        }

        dragFrom = -1
        dragTo = -1;
    }

}