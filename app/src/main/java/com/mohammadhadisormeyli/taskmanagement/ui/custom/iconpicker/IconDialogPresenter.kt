package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Category
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.filter.IconFilter
import com.maltaisn.icondialog.pack.IconPack


internal class IconDialogPresenter : IconDialogContract.Presenter {

    private var view: IconDialogContract.View? = null

    private val iconFilter: IconFilter?
        get() = view!!.iconFilter

    private val iconPack: IconPack?
        get() = view!!.iconPack

    private val listItems = mutableListOf<Item>()
    private var selectedIconId = -1
    private var searchQuery = ""


    override fun attach(view: IconDialogContract.View, state: Bundle?) {
        check(this.view == null) { "Presenter already attached." }
        this.view = view

        listItems.clear()
        searchQuery = ""

        if (iconPack == null) {
            // Set view state to show progress with no action available.
            view.apply {
                setProgressBarVisible(true)
                setNoResultLabelVisible(false)
                notifyAllIconsChanged()
            }

            // Wait until icon pack is not null.
            waitForIconPack(view, state)

        } else {
            // Initialize view state.
            initialize(view, state)
        }
    }

    /** Periodically query icon pack and if it's not null, initialize view. */
    private fun waitForIconPack(view: IconDialogContract.View, state: Bundle?) {
        view.postDelayed(ICON_PACK_CHECK_DELAY) {
            if (iconPack != null) {
                initialize(view, state)
            } else {
                waitForIconPack(view, state)
            }
        }
    }

    /** Initialize the presenter and the view. */
    private fun initialize(view: IconDialogContract.View, state: Bundle?) {

        if (state == null) {
            // Check if initial selection is valid.
            val selection = view.selectedIconId
            selectedIconId = selection

        } else {
            // Restore state
            selectedIconId = state.getInt("selectedIconIds")
            searchQuery = state.getString("searchQuery")!!
        }

        // Initialize view state
        view.let {
            it.setProgressBarVisible(false)
            it.setNoResultLabelVisible(false)


            it.addStickyHeaderDecoration()
        }

        // Create list items
        updateList()

        if (state == null) {
            // Scroll to first selected item.
            if (selectedIconId == -1) {
                view.scrollToItemPosition(getPosByIconId(selectedIconId))
            }
        }
    }

    override fun detach() {
        view?.cancelCallbacks()
        view = null
    }

    override fun saveState(state: Bundle) {
        state.putInt("selectedIconIds", selectedIconId)
        state.putString("searchQuery", searchQuery)
    }

    override fun onSelectBtnClicked() {
        confirmSelection()
    }

    override fun onCancelBtnClicked() {
        onDialogCancelled()
    }

    override fun onClearBtnClicked() {
        val pos = getPosByIconId(selectedIconId)
        if (pos != -1) {
            val item = listItems[pos] as IconItem
            item.selected = false
            view?.notifyIconItemChanged(pos)
        }
        selectedIconId = -1
    }

    override fun onDialogCancelled() {
        view?.cancelCallbacks()
    }

    override fun onSearchQueryChanged(query: String) {
    }

    override fun onSearchQueryEntered(query: String) {
        val trimQuery = query.trim()
        if (trimQuery != searchQuery) {
            searchQuery = trimQuery
            updateList()
        }
    }

    override fun onSearchActionEvent(query: String) {
        onSearchQueryEntered(query)
    }

    override fun onSearchClearBtnClicked() {
        onSearchQueryEntered("")
        view?.setSearchQuery("")
    }

    override val itemCount: Int
        get() = listItems.size

    override fun getItemId(pos: Int) = listItems[pos].id

    override fun getItemType(pos: Int) = if (listItems[pos] is IconItem) {
        ITEM_TYPE_ICON
    } else {
        ITEM_TYPE_HEADER
    }

    override fun getItemSpanCount(pos: Int, max: Int) =
        if (listItems[pos] is HeaderItem) max else 1

    override fun onBindIconItemView(pos: Int, itemView: IconDialogContract.IconItemView) {
        val item = listItems[pos] as IconItem
        itemView.bindView(item.icon, item.selected)
    }

    override fun onBindHeaderItemView(pos: Int, itemView: IconDialogContract.HeaderItemView) {
        val item = listItems[pos] as HeaderItem
        itemView.bindView(item.category)
    }

    override fun isHeader(pos: Int) = listItems[pos] is HeaderItem

    override fun getHeaderPositionForItem(pos: Int): Int {
        // Find first header item position starting from position and going up.
        var i = pos
        while (i >= 0) {
            if (listItems[i] is HeaderItem) {
                return i
            }
            i--
        }
        return RecyclerView.NO_POSITION
    }

    override fun onIconItemClicked(pos: Int): IconItem {
//        view?.hideKeyboard()

        val item = listItems[pos] as IconItem
        if (item.icon.drawable == null) {
            // Icon drawable is unavailable, can't select it.
            return null!!
        }


        if (selectedIconId != -1
            && 1 != IconDialogSettings.NO_MAX_SELECTION
        ) {
            // Max selection reached

            // Unselect first selected icon.
            val firstPos = getPosByIconId(selectedIconId)
            if (firstPos != -1) {
                val firstItem = listItems[firstPos] as IconItem
                firstItem.selected = false
                view?.notifyIconItemChanged(firstPos)
            }

            // Select new icon
            item.selected = true
            selectedIconId = item.icon.id
        } else {
            // Select new icon
            item.selected = true
            selectedIconId = item.icon.id
        }


        view?.notifyIconItemChanged(pos)
        return item
    }

    private fun confirmSelection() {
        val iconPack = iconPack ?: return
        view?.setSelectionResult(iconPack.getIcon(selectedIconId)!!)
    }

    /**
     * Get the position of an icon with [id] in the list.
     * Returns `-1` if icon is not currently shown or doesn't exist.
     */
    private fun getPosByIconId(id: Int) =
        listItems.indexOfFirst { it is IconItem && it.icon.id == id }

    /**
     * Update icon list to current search query, inserting category header items too.
     */
    private fun updateList() {
        // Get icons matching search
        val iconPack = iconPack ?: return
        val icons = iconFilter!!.queryIcons(iconPack, searchQuery)

        // Sort icons by category, then use icon filter for secondary sorting rules.
        icons.sortWith(Comparator { icon1, icon2 ->
            val result = icon1.categoryId.compareTo(icon2.categoryId)
            if (result != 0) {
                result
            } else {
                iconFilter!!.compare(icon1, icon2)
            }
        })

        // Create icon items
        listItems.clear()
        listItems += icons.map { IconItem(it, it.id == selectedIconId) }

        // Insert category headers
        if (listItems.isNotEmpty()) {
            var i = 0
            while (i < listItems.size) {
                val prevId = (listItems.getOrNull(i - 1) as IconItem?)?.icon?.categoryId
                val currId = (listItems[i] as IconItem).icon.categoryId
                if (currId != prevId && currId != Icon.NO_CATEGORY) {
                    listItems.add(i, HeaderItem(iconPack.getCategory(currId)!!))
                    i++
                }
                i++
            }
        }

        view?.notifyAllIconsChanged()
        view?.setNoResultLabelVisible(listItems.isEmpty())
    }

    private interface Item {
        val id: Long
    }

    class IconItem(val icon: Icon, var selected: Boolean) : Item {
        override val id: Long
            get() = icon.id.toLong()
    }

    private class HeaderItem(val category: Category) : Item {
        override val id: Long
            get() = category.id.inv().toLong()
    }

    companion object {
        internal const val ITEM_TYPE_ICON = 0
        internal const val ITEM_TYPE_HEADER = 1
        internal const val ITEM_TYPE_STICKY_HEADER = 2

        private const val ICON_PACK_CHECK_DELAY = 100L
    }
}
