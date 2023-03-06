package be.helmo.myscout.view.phases.phaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.databinding.PhaseListItemBinding
import be.helmo.myscout.model.Phase

class PhaseListAdapter(val phases: List<Phase>) :
    RecyclerView.Adapter<PhaseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding: PhaseListItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.phase_list_item, parent, false) //remove

        return ViewHolder(dataBinding, 0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(phases[position])
    }

    override fun getItemCount(): Int {
        return phases.size
    }

    inner class ViewHolder(viewDataBinding: PhaseListItemBinding, position: Int) :
        RecyclerView.ViewHolder(viewDataBinding.root), View.OnClickListener {
        val viewDataBinding: PhaseListItemBinding

        var mItem: Phase? = null

        init {
            this.viewDataBinding = viewDataBinding
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
            //callBacks.onSelectedPhase(mItem!!.id)
        }
    }
}