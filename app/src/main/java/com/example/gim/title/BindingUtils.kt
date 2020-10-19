package com.example.gim.title


import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.system.measureTimeMillis

@BindingAdapter( "totalTime")
fun TextView.totalTime(time : LogHours){
    var s = time.logOutTime?.seconds?.minus(time.timeInMilis.seconds)
    s = abs(s) + TimeCheck.totalPrevTime
    val hours = s/(60*60)
    val tempMin = s%(60*60)
    val minutes = tempMin/60
    text = "${hours}h${minutes}m"
}

