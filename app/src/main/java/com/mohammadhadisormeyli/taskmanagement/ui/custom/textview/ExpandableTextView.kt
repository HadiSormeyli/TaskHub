package com.mohammadhadisormeyli.taskmanagement.ui.custom.textview

import android.animation.*
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ktx.BuildConfig
import com.mohammadhadisormeyli.taskmanagement.R

open class ExpandableTextView : AppCompatTextView {

    private var onStateChangeListener: (ExpandableTextView.(oldState: State, newState: State) -> Unit)? =
        null

    private var layoutHeight: Int
        get() {
            measure(
                makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            return measuredHeight
        }
        set(value) {
            fun setHeight() {
                layoutParams = layoutParams.apply { height = value }
            }
            layoutParams?.let { setHeight() } ?: post { setHeight() }
        }

    private val collapsedHeight: Int
        get() {
            super.setMaxLines(collapsedLines)
            return layoutHeight
        }

    private val expandedHeight: Int
        get() {
            super.setMaxLines(expandedLines)
            return layoutHeight
        }

    private fun initAttrs(
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        setOnClickListener { toggle() }
        super.setEllipsize(TextUtils.TruncateAt.END)
        context.obtainStyledAttributes(
            attrs,
            R.styleable.ExpandableTextView,
            defStyleAttr,
            defStyleRes
        ).apply {
            animationDuration =
                getInteger(R.styleable.ExpandableTextView_etv_animationDuration, animationDuration)
            collapsedLines =
                getInteger(R.styleable.ExpandableTextView_etv_collapsedLines, collapsedLines)

        }.recycle()
    }

    private fun updateState(restoredState: State? = null) {
        when (state) {
            State.Collapsing, State.Expanding -> return
            else -> {}
        }
        when (restoredState) {
            State.Collapsed -> collapse(withAnimation = true, forceCollapse = false)
            State.Expanded -> expand(withAnimation = true, forceExpand = false)
            else -> state =
                if (isEllipsized) {
                    if (collapsedLines != expandedLines) {
                        if (lineCount == expandedLines)
                            State.Expanded
                        else State.Collapsed
                    } else State.Static
                } else {
                    if (collapsedLines == expandedLines || lineCount <= collapsedLines)
                        State.Static
                    else State.Expanded
                }
        }

    }

    var state = State.Static
        protected set(value) {
            if (field != value) {
                log("$field -> $value")
                onStateChangeListener?.let { it(field, value) }
                field = value
            }
        }

    private var animationDuration = 300

    private var expandInterpolator: TimeInterpolator = AccelerateDecelerateInterpolator()

    private var collapseInterpolator: TimeInterpolator = AccelerateDecelerateInterpolator()


    var collapsedLines = Integer.MAX_VALUE
        set(value) {
            field = when {
                (value > expandedLines) -> throw IllegalArgumentException("Collapsed lines($value) cannot be greater than expanded lines($expandedLines)")
                (value < 0) -> 0
                (field == value) -> return
                else -> value
            }

            text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                text.toString() + Html.fromHtml("<ins>more</ins>\n", Html.FROM_HTML_MODE_LEGACY)
            } else {
                text.toString() + Html.fromHtml("<ins>more</ins>\n")
            }

            when (state) {
                State.Collapsed, State.Collapsing, State.Static -> collapse(
                    withAnimation = false,
                    forceCollapse = true
                )
                State.Expanding -> doNothing()
                State.Expanded -> if (field == expandedLines) state = State.Static else doNothing()
            }
        }

    var expandedLines = Integer.MAX_VALUE
        set(value) {
            field = when {
                (value < collapsedLines) -> throw IllegalArgumentException("Expanded lines ($value) cannot be less than collapsed lines($collapsedLines)")
                (value < 0) -> 0
                (field == value) -> return
                else -> value
            }
            when (state) {
                State.Collapsed -> if (field == collapsedLines) state =
                    State.Static else doNothing()
                State.Collapsing -> doNothing()
                State.Static, State.Expanding -> expand(withAnimation = false, forceExpand = true)
                State.Expanded -> expand(withAnimation = false, forceExpand = true)
            }
        }


    constructor(context: Context) : super(context) {
        initAttrs()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(attrs, defStyleAttr)
    }

    @JvmOverloads
    fun toggle(withAnimation: Boolean = true) = when (state) {
        State.Expanded -> collapse(withAnimation)
        State.Collapsed -> expand(withAnimation)
        else -> false
    }

    @JvmOverloads
    fun expand(withAnimation: Boolean = true, forceExpand: Boolean = false): Boolean {
        if (state == State.Collapsed || forceExpand) {
            if (withAnimation) {
                state = State.Expanding

                ValueAnimator.ofInt(collapsedHeight, expandedHeight).apply {
                    super.setMaxLines(expandedLines)
                    interpolator = expandInterpolator
                    duration = if (withAnimation) animationDuration.toLong() else 0

                    addUpdateListener { animation ->
                        layoutHeight = animation.animatedValue as Int
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super@ExpandableTextView.setMaxLines(expandedLines)
                            state = State.Expanded

                            log("end expanding")
                            showLessButton()
                        }
                    })
                    log("start expanding")
                }.start()
            } else {
                super.setMaxLines(expandedLines)
                state = State.Expanded
            }
            return true
        } else return false
    }

    fun showLessButton() {
        val text = text.toString()
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            null,
            text.length - "...".length,
            text.length, 0
        )
        movementMethod = LinkMovementMethod.getInstance()
        setText(spannableString, BufferType.SPANNABLE)
    }

    @JvmOverloads
    fun collapse(withAnimation: Boolean = true, forceCollapse: Boolean = false): Boolean {
        if (state == State.Expanded || forceCollapse) {
            if (withAnimation) {
                state = State.Collapsing
                ValueAnimator.ofInt(expandedHeight, collapsedHeight).apply {
                    super.setMaxLines(expandedLines)
                    interpolator = collapseInterpolator
                    duration = animationDuration.toLong()

                    addUpdateListener { animation ->
                        layoutHeight = animation.animatedValue as Int
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super@ExpandableTextView.setMaxLines(collapsedLines)
                            state = State.Collapsed
                            log("end collapsing")
                        }
                    })
                    log("start collapsing")
                }.start()
            } else {
                super.setMaxLines(collapsedLines)
                state = State.Collapsed
            }
            return true
        } else return false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun onStateChange(func: (ExpandableTextView.(oldState: State, newState: State) -> Unit)?) {
        onStateChangeListener = func
    }


    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        // Layout is null at this moment, so delay any checks
        post { updateState() }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateState()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Setting ellipsize will have no effect, since ellipsize is forced to TruncateAt.END to ensure correct behaviour")
    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        doNothing()
    }

    @Deprecated(
        "Library uses `maxLines` internally to ensure correct behavior. Use collapsedLines or expandedLines property instead",
        ReplaceWith("setCollapsedLines(lines) or setExpandedLines(lines) ")
    )
    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
    }

    override fun onSaveInstanceState(): Parcelable =
        SavedState(super.onSaveInstanceState()!!).apply {
            state = this@ExpandableTextView.state
        }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(state.superState)

        post {
            when (ss.state) {
                State.Collapsed, State.Expanded -> updateState(restoredState = ss.state)
                State.Collapsing -> updateState(restoredState = State.Collapsed)
                State.Expanding -> updateState(restoredState = State.Expanded)
                else -> return@post
            }
        }
    }

    /** Protected class that is being used to save and restore [ExpandableTextView]'s state on configuration change. */
    protected class SavedState : BaseSavedState {
        var state: State = State.Collapsed

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel) : super(source) {
            state = State.valueOf(source.readString().toString())
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(state.name)
        }

        companion object {
            @JvmField
            @Suppress("unused")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel) = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    /** Enum representing current state of the [ExpandableTextView]. */
    enum class State {
        Collapsed,
        Collapsing,
        Expanding,
        Expanded,
        Static
    }
}

//
//inline fun ViewManager.expandableTextView(text: String = "", init: (ExpandableTextView).() -> Unit)
//        = ankoView(::ExpandableTextView, 0) { setText(text); init() }

// Internal extensions

internal inline val TextView.isEllipsized get() = layout.getEllipsisCount(lineCount - 1) > 0

internal fun log(message: String) {
    if (BuildConfig.DEBUG)
        Log.d("ExpandableTextView", message)
}

internal fun doNothing() {}