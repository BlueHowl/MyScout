package be.helmo.myscout.view.phases.phaselist

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter


class PhaseListAdapter(phaseListPresenter: IPhaseRecyclerCallbackPresenter) : RecyclerView.Adapter<PhaseListAdapter.PhaseViewHolder>(),
    IItemTouchHelperAdapter {
    var presenter: IPhaseRecyclerCallbackPresenter? = phaseListPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseViewHolder {
        return PhaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.phase_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhaseViewHolder, position: Int) {
        presenter!!.onBindPhaseRowViewAtPosition(position, holder)

        holder.itemView.setOnClickListener {
            presenter!!.goToPhase(position)
        }
    }

    override fun getItemCount(): Int {
        return presenter!!.getPhaseRowsCount()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDragEnd(fromPosition: Int, toPosition: Int) {
        presenter!!.movePhase(fromPosition, toPosition)
    }

    inner class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IPhaseRowView, IItemTouchHelperViewHolder {

        var titleTextView: TextView
        var startTimeTextView: TextView
        var descriptionTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.t_phase_name)
            startTimeTextView = itemView.findViewById(R.id.t_phase_startTime)
            descriptionTextView = itemView.findViewById(R.id.t_phase_resume)
        }

        override fun setTitle(title: String?) {
            titleTextView.text = title
        }

        override fun setStartTime(startTime: String?) {
            startTimeTextView.text = startTime
        }

        override fun setDescription(description: String?) {
            descriptionTextView.text = description
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.light_lime_green))
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

}

interface IItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDragEnd(fromPosition: Int, toPosition: Int)

}

interface IItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}