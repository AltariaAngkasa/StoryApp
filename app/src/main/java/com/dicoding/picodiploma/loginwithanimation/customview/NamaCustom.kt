package com.dicoding.picodiploma.loginwithanimation.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.dicoding.picodiploma.loginwithanimation.R

class NamaCustom: AppCompatEditText {
    private var validatedName: Boolean = false
    private lateinit var iconPerson: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        iconPerson = ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable
        onShowVisibilityIcon(iconPerson)

        addTextChangedListener(onTextChanged = {p0: CharSequence?, p1: Int, p2: Int, p3: Int ->
            val name = text?.trim()
            if (name.isNullOrEmpty()) {
                validatedName = false
                error = resources.getString(R.string.name_required)
            } else {
                validatedName = true
            }
        })
    }

    private fun onShowVisibilityIcon(icon: Drawable){
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}