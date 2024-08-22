package com.example.fcm_kotlin.dataClass

data class Medicine(
    val title: String,
    val message: String,
    val imageUrl: String,
    val startDate: String,
    val endDate: String,
    val foodBoolean: Boolean,
    val dosePairs: Map<Int, String>,
    val requestCodePair: Map<Int, Int>,
    val timePair: Map<Int, String>
)
