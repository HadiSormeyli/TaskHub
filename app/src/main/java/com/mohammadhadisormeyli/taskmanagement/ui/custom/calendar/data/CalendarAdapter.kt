package com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CalenderType
import com.mohammadhadisormeyli.taskmanagement.utils.DateUtils
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils
import java.util.*
import kotlin.math.ceil


class CalendarAdapter(
    context: Context, cal: Calendar, type: CalenderType,
) {
    private var mFirstDayOfWeek = 0
    var calendar: Calendar = cal.clone() as Calendar
    private val mInflater: LayoutInflater
    var calenderType = type

    private val mItemList = ArrayList<Date>()
    private val mViewList = ArrayList<View>()
    var startDate: Date? = null
    var mEventList = ArrayList<Event>()
    val tempCal = Calendar.getInstance()
    // public methods
    val count: Int
        get() = mItemList.size

    init {
        this.calendar.set(Calendar.DAY_OF_MONTH, 1)

        mInflater = LayoutInflater.from(context)

        refresh()
    }

    fun getItem(position: Int): Date {
        return mItemList[position]
    }

    fun getView(position: Int): View {
        return mViewList[position]
    }

    fun setFirstDayOfWeek(firstDayOfWeek: Int) {
        mFirstDayOfWeek = firstDayOfWeek
    }

    fun addEvent(event: Event) {
        mEventList.add(event)
    }

    @SuppressLint("InflateParams")
    fun refresh() {
        // clear data
        mItemList.clear()
        mViewList.clear()

        // set calendar
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        calendar.set(year, month, 1)

        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // generate day list
        val offset = 0 - (firstDayOfWeek - mFirstDayOfWeek) + 1
        val length = ceil(((lastDayOfMonth - offset + 1).toFloat() / 7).toDouble()).toInt() * 7
        for (i in offset until length + offset) {
            val numYear: Int
            val numMonth: Int
            val numDay: Int

            when {
                i <= 0 -> { // prev month
                    if (month == 0) {
                        numYear = year - 1
                        numMonth = 11
                    } else {
                        numYear = year
                        numMonth = month - 1
                    }
                    tempCal.set(numYear, numMonth, 1)
                    numDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH) + i
                }

                i > lastDayOfMonth -> { // next month
                    if (month == 11) {
                        numYear = year + 1
                        numMonth = 0
                    } else {
                        numYear = year
                        numMonth = month + 1
                    }
                    tempCal.set(numYear, numMonth, 1)
                    numDay = i - lastDayOfMonth
                }

                else -> {
                    numYear = year
                    numMonth = month
                    numDay = i
                }
            }


            val day = DateTimeUtils.formatDate(
                "$numYear-${
                    String.format(
                        "%02d",
                        (numMonth + 1)
                    )
                }-${String.format("%02d", (numDay))}"
            )

            day.hours = 0
            day.minutes = 0
            day.seconds = 0

            val view = mInflater.inflate(R.layout.day_layout, null)
            val txtDay = view.findViewById<View>(R.id.txt_day) as TextView
            val eventLayout = view.findViewById<View>(R.id.events_linear_layout) as LinearLayout

            txtDay.text = numDay.toString()
            if (calenderType == CalenderType.NORMAL_MODE) {
                if (numMonth != calendar.get(Calendar.MONTH)) {
                    txtDay.alpha = 0.3f
                }
            } else {
                if (startDate != null && DateUtils.getDiff(day, startDate) < 0) {
                    txtDay.alpha = 0.3f
                }

                if (numMonth != calendar.get(Calendar.MONTH)) {
                    txtDay.alpha = 0f
                }
            }

            for (j in mEventList.indices) {
                val event = mEventList[j]
                if (numYear == DateUtils.getYear(event.day)
                    && (numMonth + 1) == DateUtils.getMonth(event.day)
                    && numDay == DateUtils.getDay(event.day)
                ) {
                    if (eventLayout.childCount < 3) {
                        val eventImageView = ImageFilterView(view.context)
                        eventImageView.layoutParams = LinearLayout.LayoutParams(12, 12)
                        val margin = ScreenUtils.toDp(2.toFloat()).toInt()
                        (eventImageView.layoutParams as LinearLayout.LayoutParams).setMargins(
                            margin,
                            0,
                            margin,
                            0
                        );
                        eventImageView.roundPercent = 1F
                        eventImageView.setBackgroundColor(Color.parseColor(event.color))
                        eventLayout.addView(eventImageView)
                    }
                }
            }

            mItemList.add(day)
            mViewList.add(view)
        }
    }
}
