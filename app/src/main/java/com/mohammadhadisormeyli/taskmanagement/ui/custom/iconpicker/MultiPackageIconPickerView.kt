package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.filter.DefaultIconFilter
import com.maltaisn.icondialog.pack.IconPackLoader
import com.mohammadhadisormeyli.taskmanagement.R


class MultiPackageIconPickerView : ConstraintLayout, IconPickerView.Callback {

    private var iconPackLoader: IconPackLoader? = null
    private var selectedIcon: Icon? = null
    private var callback: IconPickerView.Callback? = null

    //view
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var searchEditText: EditText? = null
    private var selectedIconIv: ImageView? = null


    private var settings: IconDialogSettings? = null
    private val viewPagerAdapter = ViewPagerAdapter()


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.icd_viewpager, this)
        iconPackLoader = IconPackLoader(context)

        val iconFilter = DefaultIconFilter()

        iconFilter.idSearchEnabled = true
        settings = IconDialogSettings(
            iconFilter,
            IconDialog.SearchVisibility.ALWAYS,
            IconDialog.HeadersVisibility.STICKY,
            IconDialog.TitleVisibility.ALWAYS,
            R.string.icon,
            1,
            showMaxSelectionMessage = true,
            showSelectBtn = true,
            showClearBtn = true
        )

        viewPager = findViewById(R.id.icd_vp_icon_list)
        tabLayout = findViewById(R.id.icon_picker_tab_layout)
        searchEditText = findViewById(R.id.icd_edt_search)
        selectedIconIv = findViewById(R.id.selected_icon_iv)

        viewPager!!.offscreenPageLimit = 2
        viewPager!!.adapter = viewPagerAdapter

        searchEditText!!.addTextChangedListener {
            viewPagerAdapter.getItem(viewPager!!.currentItem).search(it.toString())
        }

        searchEditText!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewPagerAdapter.getItem(viewPager!!.currentItem)
                    .stopSearch(searchEditText!!.text.toString())
                hideKeyboard()
                true
            } else {
                false
            }
        }

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (searchEditText!!.text != null)
                    viewPagerAdapter.getItem(position).search(searchEditText!!.text.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        tabLayout!!.setupWithViewPager(viewPager)
    }

    fun setCallback(callback: IconPickerView.Callback) {
        this.callback = callback
    }

    override fun onIconDialogIconsSelected(icon: Icon) {
        for (i in 0..2) {
            if (i != viewPager!!.currentItem) {
                val iconPickerView = viewPagerAdapter.getItem(i)
                iconPickerView.removeSelect()
            }
        }
        selectedIcon = icon
        selectedIconIv!!.visibility = VISIBLE
        selectedIconIv!!.setImageDrawable(selectedIcon!!.drawable)

        if (callback != null)
            callback!!.onIconDialogIconsSelected(icon)
    }

    fun getSelectedIcon(): Icon? = selectedIcon

    fun hideKeyboard() {
        if (searchEditText!!.requestFocus()) {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText!!.windowToken, 0)
        }
    }

    private inner class ViewPagerAdapter : PagerAdapter() {

        private val iconPickerViews = arrayListOf<IconPickerView>()
        private val titles = context.resources.getStringArray(R.array.icons)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val iconPickerView = IconPickerView(context)
            iconPickerView.setCallback(this@MultiPackageIconPickerView, iconPackLoader!!, position)
            container.addView(iconPickerView)
            iconPickerViews.add(iconPickerView)
            return iconPickerView
        }

        fun getItem(position: Int): IconPickerView {
            return iconPickerViews[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun getCount(): Int {
            return 3
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }
    }
}