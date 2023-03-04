package be.helmo.myscout.view.meeting.meetinglist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.IMeetingPresenterCallback
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import java.util.*

class MeetingListFragment : Fragment(), IMeetingPresenterCallback {
    var recyclerView: RecyclerView? = null
    lateinit var meetingPresenter: IMeetingPresenter

    interface ISelectPhase {
        fun onSelectedPhase(placeId: UUID?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingPresenter(this)

        Log.d(TAG, "onCreate called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView called")
        val view: View = inflater.inflate(R.layout.fragment_meeting_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter =
                MeetingListAdapter(meetingPresenter)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")
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
        //callback = context as ISelectPhase
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach called")
        //callback = null
    }

    override fun onMeetingDataReady(meetingViewModels: List<MeetingViewModel>) {
        // Update the RecyclerView with the new data
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyDataSetChanged()
        }
        //recyclerView?.adapter?.notifyDataSetChanged()
        //recyclerView?.adapter.updateData(meetingViewModels)
    }

    companion object {
        private const val TAG = "MeetingListFragment"
        fun newInstance(): MeetingListFragment {
            return MeetingListFragment()
        }
    }
}
