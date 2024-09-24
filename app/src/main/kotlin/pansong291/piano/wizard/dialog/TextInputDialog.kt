package pansong291.piano.wizard.dialog

import android.app.Application
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import pansong291.piano.wizard.ViewUtil
import pansong291.piano.wizard.dialog.actions.DialogCommonActions

class TextInputDialog(application: Application) : BaseDialog(application) {
    private val textInput = EditText(application)
    var onTextConfirmed: (t: CharSequence) -> Unit = {}

    init {
        dialog.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val horizontalMargin = ViewUtil.dpToPx(application, 16f).toInt()
        textInput.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = horizontalMargin
            marginEnd = horizontalMargin
        }
        findContentWrapper().addView(textInput)
        DialogCommonActions.loadIn(this) { ok, _ ->
            ok.setOnClickListener {
                onTextConfirmed.invoke(textInput.text ?: "")
            }
        }
    }

    fun setText(text: CharSequence) {
        textInput.setText(text)
    }

    fun setHint(text: CharSequence) {
        textInput.hint = text
    }

    fun setHint(id: Int) {
        textInput.setHint(id)
    }
}
