package com.example.gim.title

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gim.title.TimeCheck.fb
import com.example.gim.title.TimeCheck.ref
import com.example.gim.title.TimeCheck.user
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*


class TitleViewModel : ViewModel() {
//    var fb = FirebaseFirestore.getInstance()
//    private val user = Firebase.auth.currentUser

    var ableToRecord = false

    private var registration : ListenerRegistration

    private var _dataList = MutableLiveData<MutableList<LogHours>>()
    val dataList: LiveData<MutableList<LogHours>>
        get() = _dataList



    init {
        //Getting data for list
        val docRef = fb.collection("${user!!.uid}")
            .orderBy("timeInMilis", Query.Direction.DESCENDING).limit(7)

         registration = docRef
            .addSnapshotListener(MetadataChanges.INCLUDE) { document, e ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.documents}")
                    var temp : Array<Any> = arrayOf( "time", Timestamp.now(), "date", Timestamp.now(), Long.MIN_VALUE)
                    val tempLog: MutableList<LogHours> = mutableListOf()
                    for (doc in document.documents) {
                        for (element in doc.data!!.keys) {

                            when(element){
                                "dateString" -> temp[2]= doc.data!![element]!!
                                "totalTime" -> temp[0]= doc.data!![element]!!
                                "timeInMilis" ->  temp[3]= doc.data!![element]!!
                                "logOutTime" -> temp[1]= doc.data!![element]!!
                                "totalPrevTime" -> temp[4] = doc.data!![element]!!
                            }
                        }
                        tempLog.add(
                            LogHours(
                                temp[0].toString(),
                                temp[1] as Timestamp,
                                temp[2].toString(),
                                temp[3] as Timestamp,
                                temp[4] as Long
                            )
                        )
                        temp = arrayOf( "time", Timestamp.now(), "date", Timestamp.now())
                    }
                    _dataList.value = tempLog
                } else {
                    Log.d("TAG", "No such document")
                }
            }
//            .addOnFailureListener { exception ->
//                Log.d("TAG", "get failed with ", exception)
//            }
    }


    fun postData() {
        val cal = Calendar.getInstance()
        val dateString =
            "${cal.get(Calendar.DATE)}/${cal.get(Calendar.MONTH)}/${cal.get(Calendar.YEAR)}"
        val startTime = SimpleDateFormat("h:mm a")
            .format(System.currentTimeMillis())
        val timinMilis = Timestamp(Date(cal.timeInMillis))
        val prevTime = TimeCheck.totalPrevTime

        ref
            .set(
                hashMapOf(
                    "dateString" to dateString,
                    "totalTime" to startTime,
                    "timeInMilis" to timinMilis,
                    "logOutTime" to timinMilis,
                    "totalPrevTime" to prevTime
                )
            ).addOnSuccessListener {
                Log.i("SUCCESSMSG", "Woooh data sent")
            }
            .addOnFailureListener {
                Log.i("TRYAGAINMSG", "No data sent : $it")
            }
    }

    fun addStopTime(stopTrack: Timestamp) {
//        val ref = fb.collection("${user!!.uid}")
//            .document("0${cal.get(Calendar.DATE)}0${cal.get(Calendar.MONTH)}${cal.get(Calendar.YEAR)}")

        ref.update(
                mapOf(
                    "logOutTime" to stopTrack,
                    "totalPrevTime" to TimeCheck.totalPrevTime
                )
            )
            .addOnSuccessListener {
                Log.i("SUCCESSMSG 2", "Woooh data updated")
            }
            .addOnFailureListener {
                Log.i("TRYAGAINMSG 2", "No data updated : $it")
            }
        ableToRecord = false
    }

    override fun onCleared() {
        super.onCleared()
        registration.remove()
    }
}