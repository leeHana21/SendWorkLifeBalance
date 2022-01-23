package com.github.leehana21.sendworklifebalance

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.leehana21.sendworklifebalance.MainActivity.Companion.CHANNEL_ID
import com.github.leehana21.sendworklifebalance.MainActivity.Companion.TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainViewModel : ViewModel() {
    private var fireDB = Firebase.firestore

    private val _fireStoreGetData = MutableLiveData<String>()
    val fireStoreGetData: LiveData<String> get() = _fireStoreGetData

    private val _fireStoreSetData = MutableLiveData<String>()
    val fireStoreSetData: LiveData<String> get() = _fireStoreSetData

    private val docRef = fireDB.collection("SMS").document("CONTENTS")


    fun setData() {
        val smsData = hashMapOf(
            "sender" to "test",
            "contents" to "test",
            "date" to "test"
        )

        docRef.set(smsData)
            .addOnSuccessListener {
                _fireStoreSetData.value = "데이터 저장 성공"
                Log.d(TAG, "DocumentSnapshot added success")
            }
            .addOnFailureListener { e ->
                _fireStoreSetData.value = "데이터 저장 실패 $e"
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getRealTimeData() {
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val showData =
                    "보낸사람 :${snapshot.data!!["sender"].toString()} \n날짜 :${snapshot.data!!["date"].toString()} \n내용 : ${snapshot.data!!["contents"].toString()}"
                _fireStoreGetData.value = showData
                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                _fireStoreGetData.value = null
                Log.d(TAG, "Current data: null")
            }
        }
    }

    fun getLastData() {
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val showData =
                        "보낸사람 :${document.data!!["sender"].toString()} \n날짜 :${document.data!!["date"].toString()} \n내용 : ${document.data!!["contents"].toString()}"
                    _fireStoreGetData.value = showData
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    _fireStoreGetData.value = null
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun showNotice(context: Context, data : String){
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("업무폰에 문자왔삼!")
            .setContentText(data)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(data))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }


}