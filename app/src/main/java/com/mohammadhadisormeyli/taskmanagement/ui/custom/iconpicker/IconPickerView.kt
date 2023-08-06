package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.icondialog.data.Category
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.filter.DefaultIconFilter
import com.maltaisn.icondialog.filter.IconFilter
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*


@SuppressLint("PrivateResource")
class IconPickerView : ConstraintLayout, IconDialogContract.View {

    private var presenter: IconDialogContract.Presenter? = null

    override var iconPack: IconPack? = null
    override var iconFilter: IconFilter? = null

    private var state: Bundle? = null

    override var selectedIconId: Int = -1
    private var selectedIconColor: Int? = null
    private var iconColor: Int? = null

    override val locale: Locale
        get() = ConfigurationCompat.getLocales(context.resources.configuration)[0]!!

    private var noResultTxv: TextView
    private var progressBar: ProgressBar
    private var listRcv: RecyclerView
    private var listAdapter: IconAdapter
    private var listLayout: IconLayoutManager

    private var progressHandler: Handler
    private var progressCallback: Runnable? = null

    private var searchHandler: Handler
    private var searchQuery: String? = null
    private val searchCallback = Runnable {
        presenter?.onSearchQueryEntered(searchQuery!!)
    }

    private var iconSize = 0
    private var unavailableIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.icd_dialog_icon, this)


        iconFilter = DefaultIconFilter()

        iconSize = ScreenUtils.toDp(48F).toInt()
        iconColor = Color.parseColor("#CCFF9100")
        selectedIconColor = Color.parseColor("#40FF9100")


        progressHandler = Handler()
        searchHandler = Handler()


        unavailableIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_add)!!

        noResultTxv = findViewById(R.id.icd_txv_no_result)
        progressBar = findViewById(R.id.icd_progress_bar)


        listRcv = findViewById(R.id.icd_rcv_icon_list)
        listAdapter = IconAdapter()
        listLayout = IconLayoutManager(context, iconSize)
        listLayout.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(pos: Int): Int {
                if (pos !in 0 until listAdapter.itemCount) return 0
                return presenter?.getItemSpanCount(pos, listLayout.spanCount) ?: 0
            }
        }
        listRcv.adapter = listAdapter
        listRcv.layoutManager = listLayout


        val fgPadding = Rect()
        val metrics = context.resources.displayMetrics
        val height = metrics.heightPixels - fgPadding.top - fgPadding.bottom
        val width = metrics.widthPixels - fgPadding.top - fgPadding.bottom


        layoutParams = ViewGroup.LayoutParams(width, height)

        presenter = IconDialogPresenter()

        if (state != null) {
//                settings = state!!.getParcelable("settings")!!

            listLayout.onRestoreInstanceState(state!!.getParcelable("listLayoutState")!!)

//                searchEdt.onRestoreInstanceState(state!!.getParcelable("searchEdtState"))
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()
        state = Bundle();
        state!!.putParcelable("listLayoutState", listLayout.onSaveInstanceState())

//        state!!.putParcelable("searchEdtState", searchEdt.onSaveInstanceState())

        presenter?.saveState(state!!)
        return super.onSaveInstanceState()
    }

    override fun postDelayed(delay: Long, action: () -> Unit) {
        val callback = Runnable(action)
        progressHandler.post(callback)
        progressCallback = callback
    }

    override fun cancelCallbacks() {
        progressHandler.removeCallbacks(progressCallback ?: return)
        progressCallback = null
    }

    override fun setSelectionResult(selected: Icon) {
        callback!!.onIconDialogIconsSelected(selected)
    }

    override fun setNoResultLabelVisible(visible: Boolean) {
        noResultTxv.isVisible = visible
    }

    override fun setProgressBarVisible(visible: Boolean) {
        progressBar.isVisible = visible
    }


    override fun removeLayoutPadding() {
        this.setPadding(0, 0, 0, 0)
    }

    override fun addStickyHeaderDecoration() {
        listRcv.addItemDecoration(
            StickyHeaderDecoration(
                listRcv, listAdapter,
                IconDialogPresenter.ITEM_TYPE_STICKY_HEADER
            )
        )
    }

    fun search(query: String) {
        searchQuery = query
        searchHandler.removeCallbacks(searchCallback)
        searchHandler.postDelayed(searchCallback, SEARCH_DELAY)
        presenter?.onSearchQueryChanged(query)
    }

    fun stopSearch(query: String) {
        searchQuery = query
        searchHandler.removeCallbacks(searchCallback)
        presenter?.onSearchActionEvent(query)
    }

    fun removeSelect() {
        presenter!!.onClearBtnClicked()
    }

    override fun scrollToItemPosition(pos: Int) {
        listLayout.scrollToPositionWithOffset(pos, iconSize)
    }

    override fun setSearchQuery(query: String) {

    }

    override fun notifyIconItemChanged(pos: Int) {
        listAdapter.notifyItemChanged(pos)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyAllIconsChanged() {
        listAdapter.notifyDataSetChanged()
    }

    override fun showMaxSelectionMessage() {
        Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT).show()
    }

    private var callback: Callback? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var packLoadJob: Job? = null

    fun setCallback(callback: Callback, iconPackLoader: IconPackLoader, currentPackIndex: Int) {
        if (this.callback == null) {
            this.callback = callback
            packLoadJob?.cancel()
            packLoadJob = coroutineScope.launch(Dispatchers.Default) {
                iconPack = withContext(Dispatchers.Default) {
                    val pack = when (currentPackIndex) {
                        0 -> createDefaultIconPack(iconPackLoader)
                        1 -> createFontAwesomeIconPack(iconPackLoader)
                        2 -> createMaterialDesignIconPack(iconPackLoader)
                        else -> error("Invalid icon pack index.")
                    }

                    pack.loadDrawables(iconPackLoader.drawableLoader)

                    pack
                }

                packLoadJob = null
            }

            presenter!!.attach(this, state)
        }
    }

    private inner class IconAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        StickyHeaderDecoration.Callback {

        init {
            setHasStableIds(true)
        }

        inner class IconViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView), IconDialogContract.IconItemView {
            private val iconImv: IconImageView = itemView.findViewById(R.id.icd_imv_icon)
            private val iconForeground: View = itemView.findViewById(R.id.icd_icon_foreground)

            init {
                iconForeground.setOnClickListener {
                    presenter?.onClearBtnClicked()
                    val iconItem =
                        presenter?.onIconItemClicked(adapterPosition) as IconDialogPresenter.IconItem
                    callback!!.onIconDialogIconsSelected(iconItem.icon)
                }
            }

            override fun bindView(icon: Icon, selected: Boolean) {
                val drawable = DrawableCompat.wrap(
                    icon.drawable
                        ?: unavailableIconDrawable
                ).mutate()
                iconImv.apply {
                    setImageDrawable(drawable)
                    checked = selected
                    val parent = parent as FrameLayout
                    if (selected) {
                        parent.background =
                            ContextCompat.getDrawable(context, R.drawable.all_corner_bg)
                        parent.backgroundTintList = ColorStateList.valueOf(selectedIconColor!!)
                    } else {
                        parent.background = null
                    }
                    imageTintList = ColorStateList.valueOf(iconColor!!)
                }
            }
        }

        inner class HeaderViewHolder(view: View) :
            RecyclerView.ViewHolder(view), IconDialogContract.HeaderItemView {
            private val headerTxv: TextView = view.findViewById(R.id.icd_header_txv)

            override fun bindView(category: Category) {
                headerTxv.text = category.resolveName(context)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                IconDialogPresenter.ITEM_TYPE_ICON -> {
                    IconViewHolder(inflater.inflate(R.layout.icd_item_icon, parent, false))
                }
                IconDialogPresenter.ITEM_TYPE_HEADER -> {
                    HeaderViewHolder(inflater.inflate(R.layout.icd_item_header, parent, false))
                }
                IconDialogPresenter.ITEM_TYPE_STICKY_HEADER -> {
                    HeaderViewHolder(
                        inflater.inflate(
                            R.layout.icd_item_sticky_header,
                            parent,
                            false
                        )
                    )
                }
                else -> error("Unknown view type.")
            }
        }

        override fun bind(holder: RecyclerView.ViewHolder, pos: Int) {
            when (holder) {
                is IconDialogContract.IconItemView -> presenter?.onBindIconItemView(pos, holder)
                is IconDialogContract.HeaderItemView -> presenter?.onBindHeaderItemView(pos, holder)
            }
        }

        override fun getItemCount() = presenter?.itemCount ?: 0
        override fun getItemId(pos: Int) = presenter?.getItemId(pos) ?: 0
        override fun getItemViewType(pos: Int) = presenter?.getItemType(pos) ?: 0
        override fun isHeader(pos: Int) = presenter?.isHeader(pos) == true
        override fun getHeaderPositionForItem(pos: Int) = presenter?.getHeaderPositionForItem(pos)
            ?: 0

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            bind(holder, position)
        }
    }

    interface Callback {

        fun onIconDialogIconsSelected(icon: Icon)
    }

    companion object {
        private const val SEARCH_DELAY = 300L
    }

}
