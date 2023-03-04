package be.helmo.myscout.view.meeting.phaselist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.model.Phase
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class PhaseListFragment : Fragment() {

    //var phaseListViewModel: PhaseListViewModel? = null
    var recyclerView: RecyclerView? = null
    var callback: ISelectPhase? = null

    interface ISelectPhase {
        fun onSelectedPhase(placeId: UUID?)
        fun onAbortRequested()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView called")
        val view: View = inflater.inflate(R.layout.fragment_phase_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter =
                PhaseAdapter(emptyList(), callback!!)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")
        /*
        phaseListViewModel = ViewModelProvider(requireActivity()).get(
            PhaseListViewModel::class.java
        )

        //remplacer randomUUID todo
        phaseListViewModel!!.getPhases(UUID.randomUUID())!!.observe(
            viewLifecycleOwner
        ) { phases -> recyclerView!!.adapter = PhaseAdapter(phases as List<Phase>, callback!!) }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach called")
        callback = context as ISelectPhase
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach called")
        callback = null
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
}