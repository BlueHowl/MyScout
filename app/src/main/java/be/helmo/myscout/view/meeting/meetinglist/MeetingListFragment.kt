package be.helmo.myscout.view.meeting.meetinglist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.IMeetingPresenterCallback
import be.helmo.myscout.view.interfaces.IMeetingListPresenter
import java.util.*

class MeetingListFragment : Fragment(), IMeetingPresenterCallback {
    var recyclerView: RecyclerView? = null
    lateinit var meetingPresenter: IMeetingListPresenter

    interface ISelectPhase {
        fun onSelectedPhase(placeId: UUID?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingListPresenter()
        meetingPresenter.setMeetingListCallback(this)

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
            recyclerView!!.adapter = MeetingListAdapter(meetingPresenter)

            val itemTouchHelper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(direction == ItemTouchHelper.LEFT) {
                        val swipedItemPosition = viewHolder.adapterPosition
                        recyclerView!!.adapter!!.notifyItemRemoved(swipedItemPosition)
                        meetingPresenter.removeMeeting(swipedItemPosition)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView

                        // Change the background color of the item view to red when swiped left
                        if (dX < 0) {
                            //itemView.setBackgroundResource(R.color.light_red)
                            itemView.setBackgroundColor(Color.RED)
                        } else {
                            itemView.setBackgroundResource(R.color.transparent)
                            itemView.setBackgroundColor(Color.TRANSPARENT)
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    }
                }
            })

            itemTouchHelper.attachToRecyclerView(recyclerView)
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

    override fun onMeetingDataAdd(meetingIndex: Int) { //todo enlever list ?
        // Update the RecyclerView with the new data
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(meetingIndex)
            //recyclerView?.adapter?.
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
