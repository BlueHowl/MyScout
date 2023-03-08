package be.helmo.myscout.view.meeting.meetinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.interfaces.IMeetingRowView
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import be.helmo.myscout.view.interfaces.IMeetingRecyclerCallbackPresenter

class MeetingListAdapter(meetingListPresenter: IMeetingPresenter) : RecyclerView.Adapter<MeetingListAdapter.MeetingViewHolder?>() {
    var presenter: IMeetingPresenter? = meetingListPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        return MeetingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.meeting_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        presenter!!.onBindMeetingRowViewAtPosition(position, holder)

        holder.itemView.setOnClickListener {
            presenter!!.goToMeeting(position);
        }
    }

    override fun getItemCount(): Int {
        return presenter!!.getMeetingRowsCount()
    }


    inner class MeetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IMeetingRowView {

        var titleTextView: TextView
        var address1TextView: TextView
        var address2TextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.meeting_title)
            address1TextView = itemView.findViewById(R.id.address1)
            address2TextView = itemView.findViewById(R.id.address2)
        }

        override fun setTitle(title: String?) {
            titleTextView.text = title
        }

        override fun setAddress(address: String?) {
            address1TextView.text = address
        }

    }

}