package com.example.gim.title

import com.google.firebase.Timestamp

data class LogHours(
    var time : String,
    var logOutTime : Timestamp,
    var date : String,
    var timeInMilis : Timestamp,
    var prevTime :Long = 0

)