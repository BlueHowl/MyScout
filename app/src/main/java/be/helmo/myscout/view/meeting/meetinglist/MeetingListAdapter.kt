package be.helmo.myscout.view.meeting.meetinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.myscout.R
import be.helmo.myscout.presenters.IMeetingRowView
import be.helmo.myscout.view.interfaces.IMeetingListPresenter

class MeetingListAdapter(meetingListPresenter: IMeetingListPresenter) : RecyclerView.Adapter<MeetingListAdapter.MeetingViewHolder?>() {
    private var presenter: IMeetingListPresenter? = meetingListPresenter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        return MeetingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.meeting_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        presenter!!.onBindMeetingRowViewAtPosition(position, holder)
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