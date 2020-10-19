package com.example.gim.title

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

object TimeCheck {
    val cal = Calendar.getInstance()
    val fb = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser
    val ref = fb.collection("${user!!.uid}")
        .document("0${cal.get(Calendar.DATE)}0${cal.get(Calendar.MONTH)}${cal.get(Calendar.YEAR)}")
    var totalPrevTime : Long = 0
    get()
    {
        ref.get().addOnSuccessListener {
            field = it.data?.get("totalPrevTime") as Long
        }.addOnFailureListener {
            field = 0
        }
    return field
    }
}