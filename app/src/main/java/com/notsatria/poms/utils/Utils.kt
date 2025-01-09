package com.notsatria.poms.utils

fun Int.secondsToMillis(): Long = this * 1000L
fun Int.minutesToMillis(): Long = this * 60 * 1000L
fun Int.hoursToMillis(): Long = this * 60 * 60 * 1000L
