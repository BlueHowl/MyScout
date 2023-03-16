package be.helmo.myscout.view.phases.favoritephaselist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.IFavoritePhaseRecyclerCallback
import be.helmo.myscout.view.interfaces.IFavoritePhaseRecyclerCallbackPresenter

/**
 * A fragment representing a list of Items.
 */
class FavoritePhaseListFragment : Fragment(), IFavoritePhaseRecyclerCallback {
    var recyclerView: RecyclerView? = null

    lateinit var phasePresenter: IFavoritePhaseRecyclerCallbackPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phasePresenter = PresenterSingletonFactory.instance!!.getFavoritePhaseRecyclerCallbackPresenter()
        phasePresenter.setFavoritePhaseListCallback(this)

        phasePresenter.getFavoritePhases()

        Log.d(TAG, "onCreate called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView called")
        val view: View = inflater.inflate(R.layout.fragment_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            val adapter = FavoritePhaseListAdapter(phasePresenter, this)
            recyclerView!!.adapter = adapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        //change le titre du menu
        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_name_favorite_phases)

        //rend le btn add element visible
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.GONE

        //rend le btn edit_element visible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.GONE

        //rend le btn delete_element invisible
        val deleteElement = requireActivity().findViewById<ImageView>(R.id.delete_element)
        deleteElement.visibility = View.GONE

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach called")
        //callback = context as ISelectPhase
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach called")
        //callback = null
    }

    override fun onFavoritePhaseAdd(phaseIndex: Int) {
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(phaseIndex)
        }
    }

    fun onFavoritePhaseSelected() {
        activity?.onBackPressed() //todo changer?
    }

    companion object {
        private const val TAG = "PhaseListFragment"
        fun newInstance(): FavoritePhaseListFragment {
            return FavoritePhaseListFragment()
        }
    }
}