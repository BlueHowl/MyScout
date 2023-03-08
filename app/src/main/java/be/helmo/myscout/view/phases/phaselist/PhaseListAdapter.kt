package be.helmo.myscout.view.phases.phaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IPhaseRowView
import be.helmo.myscout.view.interfaces.IPhasePresenter

class PhaseListAdapter(iPhasePresenter: IPhasePresenter) : RecyclerView.Adapter<PhaseListAdapter.PhaseViewHolder?>() {
    var presenter: IPhasePresenter? = iPhasePresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseViewHolder {
        return PhaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.phase_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhaseViewHolder, position: Int) {
        presenter!!.onBindPhaseRowViewAtPosition(position, holder)

        holder.itemView.setOnClickListener {
            presenter!!.goToPhase(position);
        }
    }

    override fun getItemCount(): Int {
        return presenter!!.getPhaseRowsCount()
    }

    inner class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IPhaseRowView {

        var phaseName: TextView
        var phaseDate: TextView

        init {
            phaseName = itemView.findViewById(R.id.phase_name)
            phaseDate = itemView.findViewById(R.id.phase_date)
        }

        override fun setName(title: String?) {
            phaseName.text = title
        }

        override fun setDateTime(dateTime: String?) {
            phaseDate.text = dateTime
        }

    }
}