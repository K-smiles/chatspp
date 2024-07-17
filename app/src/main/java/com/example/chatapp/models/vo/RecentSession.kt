package com.example.chatapp.models.vo


data class RecentSession(
    var toUserid: String = "",
    var name: String = "",
    var image: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    //session 创建时间或者最后消息时间
    var timeStamp: String = "",
    var isView: Boolean,
    var unRead: Int,
    var type: String
) {

    override fun toString(): String {
        return "RecentSession(toUserid='$toUserid', name='$name', image='', senderId='$senderId', receiverId='$receiverId', message='$message', timeStamp='$timeStamp', isView=$isView, unRead=$unRead, type='$type')\n"
    }
}