package be.helmo.myscout.view.meeting.phaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.databinding.PhaseListItemBinding
import be.helmo.myscout.model.Phase
import be.helmo.myscout.view.meeting.phaselist.PhaseListFragment.ISelectPhase

class PhaseAdapter(private val phases: List<Phase>, private val callBacks: ISelectPhase) :
    RecyclerView.Adapter<PhaseAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding: PhaseListItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.phase_list_item, parent, false)

        return ViewHolder(dataBinding, callBacks)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(phases[position])
    }

    override fun getItemCount(): Int {
        return phases.size
    }

    inner class ViewHolder(viewDataBinding: PhaseListItemBinding, callBacks: ISelectPhase) :
        RecyclerView.ViewHolder(viewDataBinding.root), View.OnClickListener {
        private val viewDataBinding: PhaseListItemBinding
        private val callBacks: ISelectPhase
        var mItem: Phase? = null

        init {
            this.viewDataBinding = viewDataBinding
            this.callBacks = callBacks
            viewDataBinding.root.setOnClickListener(this)
        }

        override fun toString(): String {
            return super.toString() + " '" + mItem!!.description + "'"
        }

        fun bind(phase: Phase?) {
            mItem = phase
            viewDataBinding.viewModel = mItem
            viewDataBinding.executePendingBindings()
        }

        override fun onClick(view: View) {
            callBacks.onSelectedPhase(mItem!!.id)
        }
    }
}