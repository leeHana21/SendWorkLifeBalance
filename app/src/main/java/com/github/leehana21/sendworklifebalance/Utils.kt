package com.github.leehana21.sendworklifebalance

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getStringDate(date: Date?): String {
            val simpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            return if(date != null){
                simpleDateFormat.format(date)
            } else {
                simpleDateFormat.format(Date())
            }
        }
    }
}