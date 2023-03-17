package be.helmo.myscout.view.phases.favoritephaselist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IFavoritePhaseRowView
import be.helmo.myscout.view.interfaces.IFavoritePhaseRecyclerCallbackPresenter


class FavoritePhaseListAdapter(phaseListPresenter: IFavoritePhaseRecyclerCallbackPresenter, fplf: FavoritePhaseListFragment) : RecyclerView.Adapter<FavoritePhaseListAdapter.PhaseViewHolder>() {
    var presenter: IFavoritePhaseRecyclerCallbackPresenter? = phaseListPresenter
    var favoritePhaseListFragment: FavoritePhaseListFragment? = fplf

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseViewHolder {
        return PhaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.favorite_phase_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhaseViewHolder, position: Int) {
        presenter!!.onBindFavoritePhaseRowViewAtPosition(position, holder)

        holder.itemView.setOnClickListener {
            favoritePhaseListFragment!!.onFavoritePhaseSelected()
            presenter!!.copyFavoritePhase(position)
        }
    }

    override fun getItemCount(): Int {
        return presenter!!.getFavoritePhaseRowsCount()
    }


    inner class PhaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IFavoritePhaseRowView {

        var titleTextView: TextView
        var descriptionTextView: TextView
        var favorite: RatingBar

        init {
            titleTextView = itemView.findViewById(R.id.t_phase_name)
            descriptionTextView = itemView.findViewById(R.id.t_phase_resume)
            favorite = itemView.findViewById(R.id.favorite2)
        }

        override fun setTitle(title: String?) {
            titleTextView.text = title
        }

        override fun setFavorite(favorite: Boolean?) {
            this.favorite.rating = if(favorite == true) 1F else 0F
        }

        override fun setDescription(description: String?) {
            descriptionTextView.text = description
        }

    }

}