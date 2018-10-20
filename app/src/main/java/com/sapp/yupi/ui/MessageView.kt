package com.sapp.yupi.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sapp.yupi.R


class MessageView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthPadding = paddingLeft + paddingRight
        var bubbleWidth = measuredWidth - widthPadding

        val horizontalSpace = MeasureSpec.getSize(widthMeasureSpec)
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)
        val arrowWidth = resources.getDimensionPixelSize(R.dimen.message_bubble_arrow_width)
        val composePadding = resources.getDimensionPixelSize(R.dimen.compose_msg_padding)

        val maxBubbleWidth = (horizontalSpace - iconSize - arrowWidth - composePadding * 2)

        bubbleWidth = Math.min(maxBubbleWidth, bubbleWidth)

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(bubbleWidth + widthPadding, MeasureSpec.EXACTLY),
                heightMeasureSpec)
    }
}
