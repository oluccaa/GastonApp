// src/main/java/com/seuapp/utils/DateUtils.kt

package com.seuapp.utils

import java.util.Date

fun dateToLong(date: Date): Long = date.time

fun longToDate(timestamp: Long): Date = Date(timestamp)
