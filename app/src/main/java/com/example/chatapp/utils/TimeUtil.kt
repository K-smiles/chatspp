package com.example.chatapp.utils

import java.text.SimpleDateFormat

object TimeUtil {
    fun getTime(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
        return format.format(time)
    }
    //在最近聊天里面显示
    fun getTimeMD(time: Long): String {
        val currentTIme = System.currentTimeMillis()
        val curDate = SimpleDateFormat("yyyy-MM-dd")
        val timeDate = SimpleDateFormat("yyyy-MM-dd")
        //输入时间是今天
        if(curDate.equals(timeDate))
        {
            val format = SimpleDateFormat("HH:mm")
            return format.format(time)
        }
        val format = SimpleDateFormat("dd-HH:mm")
        return format.format(time)
    }
}