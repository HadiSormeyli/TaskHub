package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import android.os.Bundle
import com.maltaisn.icondialog.data.Category
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.filter.IconFilter
import com.maltaisn.icondialog.pack.IconPack
import java.util.*


internal interface IconDialogContract {

    interface View {
        val iconPack: IconPack?
        val iconFilter: IconFilter?
        val selectedIconId: Int
        val locale: Locale

        fun postDelayed(delay: Long, action: () -> Unit)
        fun cancelCallbacks()

        fun setSelectionResult(selected: Icon)
        fun setProgressBarVisible(visible: Boolean)
        fun setNoResultLabelVisible(visible: Boolean)
        fun removeLayoutPadding()
        fun addStickyHeaderDecoration()

        fun setSearchQuery(query: String)
        fun scrollToItemPosition(pos: Int)
        fun notifyIconItemChanged(pos: Int)
        fun notifyAllIconsChanged()

        fun showMaxSelectionMessage()
    }

    interface Presenter {
        fun attach(view: View, state: Bundle?)
        fun detach()
        fun saveState(state: Bundle)

        fun onSearchQueryChanged(query: String)
        fun onSearchQueryEntered(query: String)
        fun onSearchActionEvent(query: String)
        fun onSearchClearBtnClicked()

        val itemCount: Int
        fun getItemId(pos: Int): Long
        fun getItemType(pos: Int): Int
        fun getItemSpanCount(pos: Int, max: Int): Int
        fun onBindIconItemView(pos: Int, itemView: IconItemView)
        fun onBindHeaderItemView(pos: Int, itemView: HeaderItemView)
        fun onIconItemClicked(pos: Int): IconDialogPresenter.IconItem

        fun isHeader(pos: Int): Boolean
        fun getHeaderPositionForItem(pos: Int): Int

        fun onSelectBtnClicked()
        fun onCancelBtnClicked()
        fun onClearBtnClicked()

        fun onDialogCancelled()
    }

    interface IconItemView {
        fun bindView(icon: Icon, selected: Boolean)
    }

    interface HeaderItemView {
        fun bindView(category: Category)
    }
}
