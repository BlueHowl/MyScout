package be.helmo.myscout.view.phases.phaselist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.presenters.interfaces.ISetMeetingInfos
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter

/**
 * A fragment representing a list of Items.
 */
class PhaseListFragment : Fragment(), IPhaseRecyclerCallback, ISetMeetingInfos {
    var recyclerView: RecyclerView? = null
    var recyclerAdapter: IItemTouchHelperAdapter? = null

    lateinit var phasePresenter: IPhaseRecyclerCallbackPresenter

    var meeting: MeetingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phasePresenter = PresenterSingletonFactory.instance!!.getPhaseRecyclerCallbackPresenter()
        phasePresenter.setPhaseListCallback(this)

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
            val adapter = PhaseListAdapter(phasePresenter)
            recyclerAdapter = adapter
            recyclerView!!.adapter = adapter

            val itemTouchHelperCallback = ItemTouchHelperCallback(recyclerAdapter!!, phasePresenter)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        //change le titre du menu
        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_name_phases)

        //rend le btn add element visible
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.VISIBLE

        //rend le btn edit_element visible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.VISIBLE

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

    override fun onPhaseDataChanged(phaseIndex: Int) {
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(phaseIndex)
        }
    }

    override fun onPhaseRemoved(phaseIndex: Int) {
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemRemoved(phaseIndex)
        }
    }

    override fun setMeetingValues(meeting: MeetingViewModel) {
        this.meeting = meeting
    }

    companion object {
        private const val TAG = "PhaseListFragment"
        fun newInstance(): PhaseListFragment {
            return PhaseListFragment()
        }
    }
}