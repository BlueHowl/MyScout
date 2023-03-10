package be.helmo.myscout.view.phases.phaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter

class PhaseListAdapter(phaseListPresenter: IPhaseRecyclerCallbackPresenter) : RecyclerView.Adapter<PhaseListAdapter.PhaseViewHolder>() {
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

    inner class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IPhaseRowView {

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
    }
}