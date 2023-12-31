package be.helmo.myscout.factory.interfaces

interface IPhaseRecyclerCallback {

    fun onPhaseDataChanged(phaseIndex: Int)

    fun onPhaseRemoved(phaseIndex: Int)
}