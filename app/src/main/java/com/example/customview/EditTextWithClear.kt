package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat

class EditTextWithClear @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr) {
    private val iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_24)
    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        toggleClearIcon()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { e ->
            iconDrawable?.let {
                if ( e.action == MotionEvent.ACTION_UP
                    && e.x > width - it.intrinsicWidth
                    && e.x < width
                    && e.y > height / 2 - it.intrinsicHeight / 2
                    && e.y < height / 2 + it.intrinsicHeight / 2
                ) {
                    text?.clear()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun toggleClearIcon() {
        val icon = if (text?.isNotEmpty() == true) iconDrawable else null
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null)
    }

}