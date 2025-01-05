package com.notsatria.poms.utils

fun formatTimeToMinuteAndSecond(time: Long): String {
    val minutes = time / 60
    val seconds = (time % 60)
    return "%02d:%02d".format(minutes, seconds)
}

fun formatTimeToMinuteOrSecond(time: Long): String {
    val minutes = time / 60 / 1000
    if (minutes < 1) {
        val seconds = (time / 1000)
        return "%2d seconds".format(seconds)
    }
    return "%2d minutes".format(minutes)
}