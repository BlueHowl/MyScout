package be.helmo.myscout.presenters.interfaces

interface IPhaseRowView {
    fun setTitle(title: String?)
    fun setStartTime(startTime: String?)

    fun setDuration(duration: String?)
    fun setDescription(description: String?)
}