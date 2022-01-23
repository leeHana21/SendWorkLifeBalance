package com.github.leehana21.sendworklifebalance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.github.leehana21.sendworklifebalance.MainActivity.Companion.TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class SmsReceiver : BroadcastReceiver() {
    private var fireDB = Firebase.firestore
    private val docRef = fireDB.collection("SMS").document("CONTENTS")

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "receive called !!!!")
        Log.d(TAG, "receive called !!!!")
        val messages: Array<SmsMessage?>
        val bundle = intent.extras
        if (bundle != null) {
            messages = parseSmsMessage(bundle)
            if (messages.isNotEmpty()) {
                val sender: String = messages[0]?.originatingAddress.toString()
                val content: String = messages[0]?.messageBody.toString()
                val date = Date(messages[0]?.timestampMillis!!)
                Log.d(TAG, "sender: $sender")
                Log.d(TAG, "content: $content")
                Log.d(TAG, "date: $date")

                val smsData = hashMapOf(
                    "sender" to sender,
                    "contents" to content,
                    "date" to Utils.getStringDate(date)
                )

                docRef.set(smsData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added success")
                    }

                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }

    private fun parseSmsMessage(bundle: Bundle): Array<SmsMessage?> {
        // PDU: Protocol Data Units
        val objs = bundle["pdus"] as Array<*>?
        val msgArray = arrayOfNulls<SmsMessage>(objs!!.size)
        for (i in objs.indices) {
            val format = bundle.getString("format")
            msgArray[i] = SmsMessage.createFromPdu(objs[i] as ByteArray, format)
        }
        return msgArray
    }
}