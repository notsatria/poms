package com.notsatria.poms.utils

fun formatTime(time: Long): String {
    val minutes = time / 60
    val seconds = (time % 60)
    return "%02d:%02d".format(minutes, seconds)
}