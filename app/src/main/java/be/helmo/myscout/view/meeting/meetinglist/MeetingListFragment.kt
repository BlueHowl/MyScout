package be.helmo.myscout.view.meeting.meetinglist

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.factory.PresenterSingletonFactory
import be.helmo.myscout.factory.interfaces.IMeetingRecyclerCallback
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.math.absoluteValue

class MeetingListFragment : Fragment(), IMeetingRecyclerCallback {
    var recyclerView: RecyclerView? = null
    lateinit var meetingPresenter: IMeetingPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        meetingPresenter = PresenterSingletonFactory.instance!!.getMeetingPresenter()
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
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(direction == ItemTouchHelper.LEFT) {
                        val swipedItemPosition = viewHolder.adapterPosition

                        recyclerView!!.adapter!!.notifyItemRemoved(swipedItemPosition)
                        meetingPresenter.removeMeetingAt(swipedItemPosition)
                    }
                }

                //transition couleur
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
                        val background = ColorDrawable(ContextCompat.getColor(itemView.context, R.color.red))
                        val iconView = viewHolder.itemView.findViewById<ImageView>(R.id.delete_icon)

                        if (dX < 0) {
                            iconView.visibility = View.VISIBLE

                            val swipeWidthPercentage = (dX.absoluteValue * 2 / itemView.width.toFloat()).coerceIn(0f, 1f)

                            ValueAnimator.ofInt(0, (255 * swipeWidthPercentage).toInt()).apply {
                                duration = 0
                                addUpdateListener { animator ->
                                    val alpha = animator.animatedValue as Int
                                    background.alpha = alpha
                                    itemView.setBackgroundColor(background.color)
                                }
                                start()
                            }

                            ValueAnimator.ofFloat(0f, swipeWidthPercentage).apply {
                                duration = 0
                                addUpdateListener { animator ->
                                    val alpha = animator.animatedValue as Float
                                    iconView.alpha = alpha
                                }
                                start()
                            }

                            background.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            background.draw(c)

                        } else {
                            iconView.visibility = View.GONE
                            iconView.alpha = 0f
                            background.alpha = 0
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

        //change le titre du menu
        val menuTitle = requireActivity().findViewById<TextView>(R.id.menu_title)
        menuTitle.text = getString(R.string.app_name_meetings)

        //rend le btn add_element visible
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.VISIBLE

        //rend le btn delete_element invisible
        val deleteElement = requireActivity().findViewById<ImageView>(R.id.delete_element)
        deleteElement.visibility = View.GONE

        //rend le btn edit_element invisible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.GONE
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

    override fun onMeetingDataAdd(meetingIndex: Int) {
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(meetingIndex)
        }
    }

    companion object {
        private const val TAG = "MeetingListFragment"
        fun newInstance(): MeetingListFragment {
            return MeetingListFragment()
        }
    }
}
