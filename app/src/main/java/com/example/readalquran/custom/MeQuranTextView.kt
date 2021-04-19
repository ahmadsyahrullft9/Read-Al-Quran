package com.example.readalquran.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView


@SuppressLint("AppCompatCustomView")
class MeQuranTextView : TextView {

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :
            super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        if (!isInEditMode) {
            val tf = Typeface.createFromAsset(context.assets, "me_quran_regular.ttf")
            setTypeface(tf)
        }
    }
}