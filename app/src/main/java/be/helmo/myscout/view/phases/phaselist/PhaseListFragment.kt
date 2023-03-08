package be.helmo.myscout.view.phases.phaselist

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
import be.helmo.myscout.factory.interfaces.IPhaseRecyclerCallback
import be.helmo.myscout.model.Meeting
import be.helmo.myscout.presenters.interfaces.ISetMeetingInfos
import be.helmo.myscout.presenters.viewmodel.MeetingViewModel
import be.helmo.myscout.view.interfaces.IPhaseRecyclerCallbackPresenter
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.absoluteValue

/**
 * A fragment representing a list of Items.
 */
class PhaseListFragment : Fragment(), IPhaseRecyclerCallback, ISetMeetingInfos {

    //var phaseListViewModel: PhaseListViewModel? = null
    var recyclerView: RecyclerView? = null
    lateinit var phasePresenter: IPhaseRecyclerCallbackPresenter
    //var callback: ISelectPhase? = null

    var meeting: MeetingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phasePresenter = PresenterSingletonFactory.instance!!.getRecyclerCallbackPhasePresenter()
        phasePresenter.setPhaseListCallback(this)

        Log.d(TAG, "onCreate called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView called")
        val view: View = inflater.inflate(R.layout.fragment_phase_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            recyclerView = view
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.adapter = PhaseListAdapter(phasePresenter)

            val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
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
                        phasePresenter.removePhase(swipedItemPosition)
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
                    if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        val background = ColorDrawable(ContextCompat.getColor(itemView.context, R.color.red))

                        if(dX < 0) {
                            val swipeWidthPercentage = (dX.absoluteValue * 2 / itemView.width.toFloat()).coerceIn(0f, 1f)

                            ValueAnimator.ofInt(0,(255 * swipeWidthPercentage).toInt()).apply {
                                duration = 0
                                addUpdateListener {animator ->
                                    val alpha = animator.animatedValue as Int
                                    background.alpha = alpha
                                    itemView.setBackgroundColor(background.color)
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
        menuTitle.text = getString(R.string.app_name_phases)

        //rend le btn add element visible
        val addElement = requireActivity().findViewById<ImageView>(R.id.add_element)
        addElement.visibility = View.VISIBLE

        //rend le btn edit_element visible
        val editElement = requireActivity().findViewById<ImageView>(R.id.edit_element)
        editElement.visibility = View.VISIBLE

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

    override fun onPhaseDataAdd(phaseIndex: Int){
        requireActivity().runOnUiThread {
            recyclerView?.adapter?.notifyItemChanged(phaseIndex)
        }
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
}