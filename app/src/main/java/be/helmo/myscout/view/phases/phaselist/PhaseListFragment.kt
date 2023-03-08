package be.helmo.myscout.view.phases.phaselist

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.interfaces.ISetMeetingInfos
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class PhaseListFragment : Fragment(), IPhaseRecyclerCallback, ISetMeetingInfos {

    //var phaseListViewModel: PhaseListViewModel? = null
    var recyclerView: RecyclerView? = null
    //var callback: ISelectPhase? = null

    var meeting: MeetingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        val view: View = inflater.inflate(R.layout.fragment_phase_list, container, false)

        // Set the adapter
        /*
        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter =
                PhaseAdapter(emptyList(), callback!!)
        }*/
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

    }

    override fun setMeetingValues(meeting: MeetingViewModel?) {
        this.meeting = meeting
    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.new_phase) {
            val phase = Phase(UUID.randomUUID(), "", "", 0, "", false) //test
            phaseListViewModel!!.addPhase(phase)
            callback!!.onSelectedPhase(phase.id)
            true
        } else super.onOptionsItemSelected(item)
    }*/

    companion object {
        private const val TAG = "PhaseListFragment"
        fun newInstance(): PhaseListFragment {
            return PhaseListFragment()
        }
    }

    override fun onPhaseDataAdd(phaseViewModels: Int) {
        TODO("Not yet implemented")
    }
}