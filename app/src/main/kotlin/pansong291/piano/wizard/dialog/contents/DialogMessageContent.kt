package pansong291.piano.wizard.dialog.contents

import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import pansong291.piano.wizard.dialog.base.IDialog
import pansong291.piano.wizard.utils.ViewUtil

object DialogMessageContent {
    fun loadIn(dialog: IDialog): TextView {
        val context = dialog.getContext()
        val textView = TextView(context)
        val scrollView = ScrollView(context)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val horizontalPadding = ViewUtil.dpToPx(context, 16f).toInt()
        scrollView.setPadding(horizontalPadding, horizontalPadding, horizontalPadding, 0)
        textView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        scrollView.addView(textView)
        dialog.findContentWrapper().addView(scrollView)
        return textView
    }
}
