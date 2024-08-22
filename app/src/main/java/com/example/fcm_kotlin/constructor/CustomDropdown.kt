package com.example.fcm_kotlin.constructor

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class CustomDropdown @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr) {

    init {

        val dropdownColor = Color.parseColor("#f5f2f1")
        setBackgroundColor(dropdownColor)
    }

    fun setItems(items: List<String>) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, items)
        setAdapter(adapter)
    }
}
