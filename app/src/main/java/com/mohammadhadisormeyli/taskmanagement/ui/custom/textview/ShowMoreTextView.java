package com.mohammadhadisormeyli.taskmanagement.ui.custom.textview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mohammadhadisormeyli.taskmanagement.R;


public class ShowMoreTextView extends TextView {

    private static final String TAG = ShowMoreTextView.class.getName();

    private int showingLine = 1;
    private int showingChar;
    private boolean isCharEnable;

    private String showMore = "more";
    private String dotdot = "...";

    private int MAGIC_NUMBER = 5;

    private int showMoreTextColor = Color.BLUE;
    private int animationDuration;
    private int collapsedLines = 4;

    private String mainText;

    private boolean isAlreadySet;


    public ShowMoreTextView(Context context) {
        this(context, null);
    }

    public ShowMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, 0, 0);

        animationDuration =
                typedArray.getInteger(R.styleable.ExpandableTextView_etv_animationDuration, animationDuration);

        setShowingLine(typedArray.getInteger(R.styleable.ExpandableTextView_etv_collapsedLines, collapsedLines));
        setShowMoreColor(typedArray.getInteger(R.styleable.ExpandableTextView_etv_moreColor, R.color.black));
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mainText = getText().toString();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    private void addShowMore() {
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String text = getText().toString();
                if (!isAlreadySet) {
                    mainText = getText().toString();
                    isAlreadySet = true;
                }
                String showingText = "";
                if (isCharEnable) {
                    if (showingChar >= text.length()) {
                        try {
                            throw new Exception("Character count cannot be exceed total line count");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String newText = text.substring(0, showingChar);
                    newText += showMore + dotdot;

                    SaveState.isCollapse = true;

                    setText(newText);
                    Log.d(TAG, "Text: " + newText);
                } else {

                    if (showingLine >= getLineCount()) {
                        try {
                            throw new Exception("Line Number cannot be exceed total line count");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        return;
                    }
                    int start = 0;
                    int end;
                    for (int i = 0; i < showingLine; i++) {
                        end = getLayout().getLineEnd(i);
                        showingText += text.substring(start, end);
                        start = end;
                    }

                    String newText = showingText.substring(0, showingText.length() - (dotdot.length() + showMore.length() + MAGIC_NUMBER));
                    Log.d(TAG, "Text: " + newText);
                    Log.d(TAG, "Text: " + showingText);
                    newText += showMore + dotdot;

                    SaveState.isCollapse = true;

                    setText(newText);
                }

                setShowMoreColoringAndClickable();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });


    }

    private void setShowMoreColoringAndClickable() {
        final SpannableString spannableString = new SpannableString(getText());

        Log.d(TAG, "Text: " + getText());
        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(@Nullable View view) {
                                        setMaxLines(Integer.MAX_VALUE);
                                        setText(mainText);
                                        SaveState.isCollapse = false;
                                        showLessButton();
                                        Log.d(TAG, "Item clicked: " + mainText);

                                    }
                                },
                getText().length() - (dotdot.length() + showMore.length()),
                getText().length(), 0);

        spannableString.setSpan(new ForegroundColorSpan(showMoreTextColor),
                getText().length() - (dotdot.length() + showMore.length()),
                getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    //TODO: CHANGE MORE STATUS
    private void showLessButton() {
        String text = getText().toString();
        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(@Nullable View view) {

                                        setMaxLines(showingLine);

                                        addShowMore();

                                        Log.d(TAG, "Item clicked: ");

                                    }
                                },
                text.length() - (dotdot.length()),
                text.length(), 0);


        setMovementMethod(LinkMovementMethod.getInstance());
        setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    /*
     * User added field
     * */

    /**
     * User can add minimum line number to show collapse text
     *
     * @param lineNumber int
     */
    public void setShowingLine(int lineNumber) {
        if (lineNumber == 0) {
            try {
                throw new Exception("Line Number cannot be 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        isCharEnable = false;

        showingLine = lineNumber;

        setMaxLines(showingLine);

        if (SaveState.isCollapse) {
            addShowMore();
        } else {
            setMaxLines(Integer.MAX_VALUE);
            showLessButton();
        }

    }

    /**
     * User can limit character limit of text
     *
     * @param character int
     */
    public void setShowingChar(int character) {
        if (character == 0) {
            try {
                throw new Exception("Character length cannot be 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        isCharEnable = true;
        this.showingChar = character;

        if (SaveState.isCollapse) {
            addShowMore();
        } else {
            setMaxLines(Integer.MAX_VALUE);
            showLessButton();
        }
    }

    /**
     * User can add their own  show more text
     *
     * @param text String
     */
    public void addShowMoreText(String text) {
        showMore = text;
    }

    /**
     * User Can add show more text color
     *
     * @param color Integer
     */
    public void setShowMoreColor(int color) {
        showMoreTextColor = color;
    }

    public static class SaveState {
        public static boolean isCollapse = true;
    }
}