package be.helmo.myscout.phaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.databinding.PhaseListItemBinding
import be.helmo.myscout.model.Phase
import be.helmo.myscout.phaselist.PhaseListFragment.ISelectPlace

class PhaseAdapter(val phases: List<Phase>, val callBacks: ISelectPlace) :
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

    inner class ViewHolder(viewDataBinding: PhaseListItemBinding, callBacks: ISelectPlace) :
        RecyclerView.ViewHolder(viewDataBinding.getRoot()), View.OnClickListener {
        private val viewDataBinding: PhaseListItemBinding
        private val callBacks: ISelectPlace
        var mItem: Phase? = null

        init {
            this.viewDataBinding = viewDataBinding
            this.callBacks = callBacks
            viewDataBinding.getRoot().setOnClickListener(this)
        }

        override fun toString(): String {
            return super.toString() + " '" + mItem!!.description + "'"
        }

        fun bind(phase: Phase?) {
            mItem = phase
            viewDataBinding.setViewModel(mItem)
            viewDataBinding.executePendingBindings()
        }

        override fun onClick(view: View) {
            callBacks.onSelectedPhase(mItem!!.id)
        }
    }
}