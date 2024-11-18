package com.dicoding.picodiploma.loginwithanimation.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R

class Button: AppCompatButton {
    private var txtcolor: Int = 0

    constructor(context: Context): super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setTextColor(txtcolor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = if (isEnabled) resources.getString(R.string.submit) else resources.getString(R.string.invalid)
    }

    private fun init(){
        txtcolor = ContextCompat.getColor(context, android.R.color.background_light)
    }
}