package com.example.dermai

data class ResultData(
    val disease: String = "",
    val confidence: Double = 0.0,
    val alt1: String = "",
    val alt1Conf: Double = 0.0,
    val alt2: String = "",
    val alt2Conf: Double = 0.0,
    val timestamp: String = "",
    var docId: String = ""
)

