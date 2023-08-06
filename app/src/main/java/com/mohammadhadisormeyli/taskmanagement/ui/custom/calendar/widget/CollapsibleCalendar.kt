package com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.data.CalendarAdapter
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.data.Event
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.view.BounceAnimator
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.view.ExpandIconView
import com.mohammadhadisormeyli.taskmanagement.utils.DateUtils
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class CollapsibleCalendar : UICalendar, View.OnClickListener {
    override fun changeToToday() {
        val calendar = Calendar.getInstance()
        val calenderAdapter = CalendarAdapter(context, calendar, calenderType)
        calenderAdapter.startDate = mAdapter!!.startDate
        calenderAdapter.mEventList = mAdapter!!.mEventList
        calenderAdapter.setFirstDayOfWeek(firstDayOfWeek)
        mCurrentWeekIndex = suitableRowIndex
        if (mListener != null) {
            if (SAVE_STATE && ((YEAR == calendar.get(Calendar.YEAR) && MONTH != (calendar.get(
                    Calendar.MONTH
                ) + 1)) || YEAR != calendar.get(Calendar.YEAR))
            ) {
                YEAR = calendar.get(Calendar.YEAR)
                MONTH = calendar.get(Calendar.MONTH) + 1
                mListener!!.onMonthChange(YEAR, MONTH)
            }
        }
        setAdapter(calenderAdapter)
    }

    override fun onClick(view: View?) {
        view?.let {
            mListener.let { mListener ->
                mListener?.onClickListener() ?: expandIconView.performClick()
            }
        }
    }

    private var mAdapter: CalendarAdapter? = null
    private var mListener: CalendarListener? = null

    var expanded = false

    private var mInitHeight = 0

    private val mHandler = Handler()
    private var mIsWaitingForUpdate = false

    private var mCurrentWeekIndex: Int = 0


    private val suitableRowIndex: Int
        get() {
            return when {
//                selectedItemPosition != -1 -> {
//                    val view = mAdapter!!.getView(selectedItemPosition)
//                    val row = view.parent as TableRow
//
//                    mTableBody.indexOfChild(row)
//                }
                todayItemPosition != -1 -> {
                    val view = mAdapter!!.getView(todayItemPosition)
                    val row = view.parent as TableRow

                    mTableBody.indexOfChild(row)
                }

                else -> {
                    0
                }
            }
        }


    private var selectedDay: Date? = null
        get() {
            return selectedItem
        }
        set(value: Date?) {
            field = value
            redraw()
        }

    protected var selectedItemPosition: Int = 0
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (isSelectedDay(day)) {
                    position = i
                    break
                }
            }
            if (position == -1) {
                position = todayItemPosition
            }
            return position
        }

    private val todayItemPosition: Int
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (DateTimeUtils.isToday(day)) {
                    position = i
                    break
                }
            }
            return position
        }

    override var state: Int
        get() = super.state
        set(state) {
            super.state = state
            if (state == STATE_COLLAPSED) {
                expanded = false
            }
            if (state == STATE_EXPANDED) {
                expanded = true
            }
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {


        val cal = Calendar.getInstance()

        if (!SAVE_STATE) {
            this.post { collapseTo(mCurrentWeekIndex) }
        } else {
            cal.set(Calendar.YEAR, YEAR)
            cal.set(Calendar.MONTH, MONTH - 1)
            state = STATE
        }
        val adapter = CalendarAdapter(context, cal, calenderType)
        setAdapter(adapter)


        // bind events
        mBtnPrevMonth.setOnClickListener { prevMonth() }

        mBtnNextMonth.setOnClickListener { nextMonth() }

        mBtnPrevWeek.setOnClickListener { prevWeek() }

        mBtnNextWeek.setOnClickListener { nextWeek() }

        mTodayIcon.setOnClickListener { changeToToday() }

        expandIconView.setState(ExpandIconView.MORE, true)


        expandIconView.setOnClickListener {
            if (expanded) {
                collapse(400)
            } else {
                expand(400)
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mInitHeight = mTableBody.measuredHeight

        if (mIsWaitingForUpdate) {
            redraw()
            mHandler.post { collapseTo(mCurrentWeekIndex) }
            mIsWaitingForUpdate = false
            if (mListener != null) {
                mListener!!.onDataUpdate()
            }
        }
    }

    override fun redraw() {
        // redraw all views of week
        val rowWeek = mTableHead.getChildAt(0) as TableRow?
        if (rowWeek != null) {
            for (i in 0 until rowWeek.childCount) {
                (rowWeek.getChildAt(i) as TextView).setTextColor(textColor)
            }
        }
        // redraw all views of day
        if (mAdapter != null) {
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)
                val view = mAdapter!!.getView(i)
                val txtDay = view.findViewById<View>(R.id.txt_day) as TextView
                txtDay.setBackgroundColor(Color.TRANSPARENT)
                txtDay.setTextColor(textColor)

                // set today's item
                if (DateTimeUtils.isToday(day)) {
                    txtDay.background = (todayItemBackgroundDrawable)
                    txtDay.setTextColor(todayItemTextColor)
                }

                // set the selected item
                if (calenderType == CalenderType.SELECTING_MODE) {
                    if (isSelectedDay(day)) {
                        txtDay.background = (selectedItemBackgroundDrawable)
                        txtDay.setTextColor(selectedItemTextColor)
                    }
                }
            }
        }
    }

    private fun isDayAfterStartTime(day: Date): Boolean {
        if (startItem != null && day.before(startItem))
            return false
        return true
    }

    override fun reload() {
        mAdapter?.let { mAdapter ->
            mAdapter.calenderType = calenderType
            mAdapter.refresh()
            val calendar = Calendar.getInstance()
            val tempDatePattern: String =
                if (calendar.get(Calendar.YEAR) != mAdapter.calendar.get(Calendar.YEAR)) {
                    "MMMM yyyy"
                } else {
                    datePattern
                }
            // reset UI
            val dateFormat = SimpleDateFormat(tempDatePattern, Locale.ENGLISH)
            dateFormat.timeZone = mAdapter.calendar.timeZone
            mTxtTitle.text = dateFormat.format(mAdapter.calendar.time)
            mTableHead.removeAllViews()
            mTableBody.removeAllViews()

            var rowCurrent: TableRow
            rowCurrent = TableRow(context)
            rowCurrent.layoutParams = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (i in 0..6) {
                val view = mInflater.inflate(R.layout.layout_day_of_week, null)
                val txtDayOfWeek = view.findViewById<View>(R.id.txt_day_of_week) as TextView
                txtDayOfWeek.text = DateFormatSymbols().shortWeekdays[(i + firstDayOfWeek) % 7 + 1]
                view.layoutParams = TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                rowCurrent.addView(view)
            }
            mTableHead.addView(rowCurrent)

            // set day view
            for (i in 0 until mAdapter.count) {

                if (i % 7 == 0) {
                    rowCurrent = TableRow(context)
                    rowCurrent.layoutParams = TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    mTableBody.addView(rowCurrent)
                }
                val view = mAdapter.getView(i)
                view.layoutParams = TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                params.let { params ->
                    if (params != null && (DateUtils.getDiff(
                            mAdapter.getItem(i),
                            Date()
                        ) < params.prevDays || DateUtils.getDiff(
                            mAdapter.getItem(i), Date()
                        ) > params.nextDaysBlocked)
                    ) {
                        view.isClickable = false
                        view.alpha = 0.3f
                    } else {
                        view.setOnClickListener { v -> onItemClicked(v, mAdapter.getItem(i)) }
                    }
                }
                rowCurrent.addView(view)
            }

            redraw()
            mIsWaitingForUpdate = true
        }
    }

    private fun onItemClicked(view: View, day: Date) {
        select(day)


        val cal = mAdapter!!.calendar

        val newYear = DateUtils.getYear(day)
        val newMonth = DateUtils.getMonth(day)
        val oldYear = cal.get(Calendar.YEAR)
        val oldMonth = cal.get(Calendar.MONTH) + 1
        if (calenderType == CalenderType.NORMAL_MODE) {
            if (newMonth != oldMonth) {
                cal.set(DateUtils.getYear(day), DateUtils.getMonth(day), 1)

                if (newYear > oldYear || newMonth > oldMonth) {
                    mCurrentWeekIndex = 0
                }
                if (newYear < oldYear || newMonth < oldMonth) {
                    mCurrentWeekIndex = -1
                }
                if (mListener != null) {
                    mListener!!.onMonthChange(newYear, newMonth)
                }
                reload()
            }
        }

        if (mListener != null) {
            mListener!!.onItemClick(view)
        }
    }

    // public methods
    fun setAdapter(adapter: CalendarAdapter) {
        mAdapter = adapter
        adapter.setFirstDayOfWeek(firstDayOfWeek)

        reload()

        // init week
        mCurrentWeekIndex = suitableRowIndex
    }

    fun addEventTag(day: Date) {
        mAdapter!!.addEvent(Event(day))

        reload()
    }

    fun setEvents(events: ArrayList<Event>) {
        mAdapter!!.mEventList = events;
        reload()
    }

    fun addEventTag(day: Date, color: String) {
        mAdapter!!.addEvent(Event(day, color))

        reload()
    }

    private fun prevMonth() {
        val cal = mAdapter!!.calendar
        params.let {
            if (it != null && (Calendar.getInstance()
                    .get(Calendar.YEAR) * 12 + Calendar.getInstance()
                    .get(Calendar.MONTH) + it.prevDays / 30) > (cal.get(Calendar.YEAR) * 12 + cal.get(
                    Calendar.MONTH
                ))
            ) {
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator = BounceAnimator(0.1, 10.0)
                myAnim.interpolator = interpolator
                mTableBody.startAnimation(myAnim)
                mTableHead.startAnimation(myAnim)
                return
            }
            if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
                cal.set(cal.get(Calendar.YEAR) - 1, cal.getActualMaximum(Calendar.MONTH), 1)
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1)
            }
            if (SAVE_STATE) {
                YEAR = cal.get(Calendar.YEAR)
                MONTH = cal.get(Calendar.MONTH) + 1
            }
            reload()
            if (mListener != null) {
                mListener!!.onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            }
        }
    }

    private fun nextMonth() {
        val cal = mAdapter!!.calendar
        params.let {
            if (it != null && (Calendar.getInstance()
                    .get(Calendar.YEAR) * 12 + Calendar.getInstance()
                    .get(Calendar.MONTH) + it.nextDaysBlocked / 30) < (cal.get(Calendar.YEAR) * 12 + cal.get(
                    Calendar.MONTH
                ))
            ) {
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
                val interpolator = BounceAnimator(0.1, 10.0)
                myAnim.interpolator = interpolator
                mTableBody.startAnimation(myAnim)
                mTableHead.startAnimation(myAnim)
                return
            }
            if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
                cal.set(cal.get(Calendar.YEAR) + 1, cal.getActualMinimum(Calendar.MONTH), 1)
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
            }
            if (SAVE_STATE) {
                YEAR = cal.get(Calendar.YEAR)
                MONTH = cal.get(Calendar.MONTH) + 1
            }
            reload()
            if (mListener != null) {
                mListener!!.onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            }
        }
    }

    fun nextDay() {
        if (selectedItemPosition == mAdapter!!.count - 1) {
            nextMonth()
            mAdapter!!.getView(0).performClick()
            reload()
            mCurrentWeekIndex = 0
            collapseTo(mCurrentWeekIndex)
        } else {
            mAdapter!!.getView(selectedItemPosition + 1).performClick()
            if (((selectedItemPosition + 1 - mAdapter!!.calendar.firstDayOfWeek) / 7) > mCurrentWeekIndex) {
                nextWeek()
            }
        }
        mListener?.onDayChanged()
    }

    fun prevDay() {
        if (selectedItemPosition == 0) {
            prevMonth()
            mAdapter!!.getView(mAdapter!!.count - 1).performClick()
            reload()
            return;
        } else {
            mAdapter!!.getView(selectedItemPosition - 1).performClick()
            if (((selectedItemPosition - 1 + mAdapter!!.calendar.firstDayOfWeek) / 7) < mCurrentWeekIndex) {
                prevWeek()
            }
        }
        mListener?.onDayChanged()
    }

    private fun prevWeek() {
        if (mCurrentWeekIndex - 1 < 0) {
            mCurrentWeekIndex = -1
            prevMonth()
        } else {
            mCurrentWeekIndex--
            collapseTo(mCurrentWeekIndex)
        }
    }

    private fun nextWeek() {
        if (mCurrentWeekIndex + 1 >= mTableBody.childCount) {
            mCurrentWeekIndex = 0
            nextMonth()
        } else {
            mCurrentWeekIndex++
            collapseTo(mCurrentWeekIndex)
        }
    }

    private fun isSelectedDay(day: Date?): Boolean {
        return (day != null && DateUtils.isEqual(day, selectedItem))
    }

    /**
     * collapse in milliseconds
     */
    fun collapse(duration: Int) {

        if (state == STATE_EXPANDED) {
            state = STATE_PROCESSING
            STATE = state

            mLayoutBtnGroupMonth.visibility = View.GONE
            mLayoutBtnGroupWeek.visibility = View.VISIBLE
            mBtnPrevWeek.isClickable = false
            mBtnNextWeek.isClickable = false

            val index = suitableRowIndex
            mCurrentWeekIndex = index

            val currentHeight = mInitHeight
            val targetHeight = mTableBody.getChildAt(index).measuredHeight
            var tempHeight = 0
            for (i in 0 until index) {
                tempHeight += mTableBody.getChildAt(i).measuredHeight
            }
            val topHeight = tempHeight

            val anim = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                    mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                        targetHeight
                    else
                        currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                    mScrollViewBody.requestLayout()

                    if (mScrollViewBody.measuredHeight < topHeight + targetHeight) {
                        val position = topHeight + targetHeight - mScrollViewBody.measuredHeight
                        mScrollViewBody.smoothScrollTo(0, position)
                    }

                    if (interpolatedTime == 1f) {
                        state = STATE_COLLAPSED
                        STATE = state

                        mBtnPrevWeek.isClickable = true
                        mBtnNextWeek.isClickable = true
                    }
                }
            }
            anim.duration = duration.toLong()
            startAnimation(anim)
        }

        expandIconView.setState(ExpandIconView.MORE, true)
        reload()
    }

    private fun collapseTo(index: Int) {
        var index = index
        if (state == STATE_COLLAPSED) {
            if (index == -1) {
                index = mTableBody.childCount - 1
            }
            mCurrentWeekIndex = index

            val targetHeight = mTableBody.getChildAt(index).measuredHeight
            var tempHeight = 0
            for (i in 0 until index) {
                tempHeight += mTableBody.getChildAt(i).measuredHeight
            }
            val topHeight = tempHeight

            mScrollViewBody.layoutParams.height = targetHeight
            mScrollViewBody.requestLayout()

            mHandler.post { mScrollViewBody.smoothScrollTo(0, topHeight) }


            if (mListener != null) {
                mListener!!.onWeekChange(mCurrentWeekIndex)
            }
        }
    }

    fun expand(duration: Int) {
        if (state == STATE_COLLAPSED) {
            state = STATE_PROCESSING
            STATE = state

            mLayoutBtnGroupMonth.visibility = View.VISIBLE
            mLayoutBtnGroupWeek.visibility = View.GONE
            mBtnPrevMonth.isClickable = false
            mBtnNextMonth.isClickable = false

            val currentHeight = mScrollViewBody.measuredHeight
            val targetHeight = mInitHeight

            val anim = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                    mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                    mScrollViewBody.requestLayout()

                    if (interpolatedTime == 1f) {
                        state = STATE_EXPANDED
                        STATE = state

                        mBtnPrevMonth.isClickable = true
                        mBtnNextMonth.isClickable = true
                    }
                }
            }
            anim.duration = duration.toLong()
            startAnimation(anim)
        }

        expandIconView.setState(ExpandIconView.LESS, true)
        reload()
    }

    fun setStartDate(date: Date?) {
        this.startItem = date
        startItem!!.hours = 0
        startItem!!.minutes = 0
        startItem!!.seconds = 0
        mAdapter!!.startDate = date
        setAdapter(mAdapter!!)
    }

    fun setSelectedDate(date: Date?) {
        date?.let {
            this.selectedItem = it
            mAdapter!!.calendar.set(Calendar.YEAR, DateUtils.getYear(it))
            mAdapter!!.calendar.set(Calendar.MONTH, DateUtils.getMonth(it) - 1)
            redraw()
        }
    }

    private fun select(day: Date) {
        if (calenderType == CalenderType.NORMAL_MODE) {
            selectedItem = day
            redraw()
        } else {
            if (startItem != null) {
                if (DateUtils.isEqual(day, startItem) || day.after(startItem)) {
                    selectedItem = day
                    redraw()

                    if (mListener != null) {
                        mListener!!.onDaySelect(selectedItem)
                    }
                }
            } else {
                selectedItem = day
                redraw()

                if (mListener != null) {
                    mListener!!.onDaySelect(selectedItem)
                }
            }
        }
    }

    fun saveUIState(save: Boolean) {
        SAVE_STATE = save
    }

    fun setStateWithUpdateUI(state: Int) {
        this@CollapsibleCalendar.state = state

        if (state != state) {
            mIsWaitingForUpdate = true
            requestLayout()
        }
    }

    // callback
    fun setCalendarListener(listener: CalendarListener) {
        mListener = listener
    }

    interface CalendarListener {

        // triggered when a day is selected programmatically or clicked by user.
        fun onDaySelect(day: Date?)

        // triggered only when the views of day on calendar are clicked by user.
        fun onItemClick(v: View)

        // triggered when the data of calendar are updated by changing month or adding events.
        fun onDataUpdate()

        // triggered when the month are changed.
        fun onMonthChange(year: Int, month: Int)

        // triggered when the week position are changed.
        fun onWeekChange(position: Int)

        fun onClickListener()

        fun onDayChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setExpandIconVisible(visible: Boolean) {
        if (visible) {
            expandIconView.visibility = View.VISIBLE
        } else {
            expandIconView.visibility = View.GONE
        }
    }

    fun clearEvents() {
        mAdapter!!.mEventList.clear()
        reload()
    }


    companion object {
        var YEAR = Calendar.getInstance().get(Calendar.YEAR)
        var MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1
        var STATE = STATE_EXPANDED
        var SAVE_STATE = false
    }


    data class Params(val prevDays: Int, val nextDaysBlocked: Int)

    var params: Params? = null
        set(value) {
            field = value
        }
}

