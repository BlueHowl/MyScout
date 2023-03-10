package be.helmo.myscout.view.phases.phaselist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter


class PhaseListAdapter(phaseListPresenter: IPhaseRecyclerCallbackPresenter) : RecyclerView.Adapter<PhaseListAdapter.PhaseViewHolder>(),
    IItemTouchHelperAdapter {
    private var presenter: IPhaseRecyclerCallbackPresenter? = phaseListPresenter

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
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                presenter!!.movePhase(i, 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                presenter!!.movePhase(i, -1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemRemove(position: Int) {
        notifyItemRemoved(position)
    }

    inner class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IPhaseRowView, IItemTouchHelperViewHolder {

        private var titleTextView: TextView
        private var durationTextView: TextView
        private var descriptionTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.t_phase_name)
            durationTextView = itemView.findViewById(R.id.t_phase_duration)
            descriptionTextView = itemView.findViewById(R.id.t_phase_resume)
        }

        override fun setTitle(title: String?) {
            titleTextView.text = title
        }

        override fun setDuration(duration: String?) {
            durationTextView.text = duration
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

    fun onItemRemove(position: Int)
}

interface IItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}