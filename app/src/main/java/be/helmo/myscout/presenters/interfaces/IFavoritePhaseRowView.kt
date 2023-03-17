package be.helmo.myscout.presenters.interfaces

interface IFavoritePhaseRowView {
    fun setTitle(title: String?)
    fun setFavorite(favorite: Boolean?)
    fun setDescription(description: String?)
}