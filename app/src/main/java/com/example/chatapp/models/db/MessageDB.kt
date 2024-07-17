package com.example.chatapp.models.db

import android.util.Log
import com.example.chatapp.models.DBMessage
import com.example.chatapp.models.Message
import com.example.chatapp.models.vo.RecentSession
import com.example.chatapp.models.Session
import com.tencent.wcdb.base.Value
import com.tencent.wcdb.core.Database
import com.tencent.wcdb.winq.Column
import com.tencent.wcdb.winq.Expression
import com.tencent.wcdb.winq.Order
import com.tencent.wcdb.winq.StatementSelect
import java.util.UUID
import kotlin.collections.HashSet

object MessageDB {

    fun getAllMessage(sendId: String, receiverId: String): List<Message> {
        try {
            Log.i("fcm", "result = $sendId  $receiverId")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val expression1 = Expression(Column("senderId")).eq(sendId)
                .and(Expression(Column("receiverId")).eq(receiverId))
            val expression2 = Expression(Column("senderId")).eq(receiverId)
                .and(Expression(Column("receiverId")).eq(sendId))
            val result = database.getAllObjects(
                DBMessage.allFields(),
                "messageTable",
                expression1.or(expression2),
                DBMessage.dateTime.order(Order.Asc)
            )
            Log.i("fcm", "result = $result")
            return result
        } catch (exception: Exception) {
            Log.i("fcm", "error")
            return ArrayList<Message>()
        }
    }

    fun getAllMessage(
        sendId: String,
        receiverId: String,
        limit: Long,
        offset: Long
    ): List<Message> {
        try {
            Log.i("fcm", "getAllMessage para $sendId  $receiverId $offset $limit  ")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val expression1 = Expression(Column("senderId")).eq(sendId)
                .and(Expression(Column("receiverId")).eq(receiverId))
            val expression2 = Expression(Column("senderId")).eq(receiverId)
                .and(Expression(Column("receiverId")).eq(sendId))
            //按照时间降序
            val result = database.getAllObjects(
                DBMessage.allFields(),
                "messageTable",
                expression1.or(expression2),
                DBMessage.dateTime.order(Order.Desc),
                limit,
                offset
            )
            result.reverse()
            Log.i("fcm", "getAllMessage size = ${result.size} result = $result")
            return result
        } catch (exception: Exception) {
            Log.i("fcm", "getAllMessage error")
            return ArrayList<Message>()
        }
    }

    fun insert(message: Message) {
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        try {
            database.insertObject(message, DBMessage.allFields(), "messageTable")
        } catch (exception: Exception) {
            Log.i("WCDB", exception.printStackTrace().toString())
        }
    }

    fun getNewerMessage(sendId: String): List<Message> {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val expression1 = Expression(Column("mygroup")).eq(sendId)
            val result = database.getAllObjects(
                DBMessage.allFields(),
                "messageTable",
                expression1,
                DBMessage.dateTime.order(Order.Desc),
                1
            )
            Log.i("fcm", "newer result = $result")
            return result
        } catch (exception: Exception) {
            Log.i("fcm", "error")
            return ArrayList<Message>()
        }
    }

    fun getNewerMessage2(sendId: String, session: List<Session>): List<RecentSession> {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val targetIDs: List<String> = session.map { it.targetId }
            val result1 = database.getAllObjects(
                DBMessage.allFields(), "messageTable",
                DBMessage.receiverId.eq(sendId).and(DBMessage.senderId.notEq(sendId))
            )
            val result2 = database.getAllObjects(
                DBMessage.allFields(), "messageTable",
                DBMessage.senderId.eq(sendId).and(DBMessage.receiverId.notEq(sendId))
            )
            result1.addAll(result2)
            result1.sortByDescending { message: Message? -> message?.dateTime }

            val tmpSet = HashSet<String>()
            //获取最新聊天记录
            val res2 = result1.filter { message ->
                val chatID =
                    if (message.senderId.equals(sendId)) message.receiverId else message.senderId
                val isExist = !tmpSet.contains(chatID)
                tmpSet.add(chatID)
                isExist
            }
            //只有targetID里面的聊天记录才会被返回
            val res: MutableList<Message> = res2.filter { message ->
                val chatID =
                    if (message.senderId.equals(sendId)) message.receiverId else message.senderId
                targetIDs.contains(chatID)
            } as MutableList

            val tmpTargetId: List<String> =
                res.map { if (it.senderId.equals(sendId)) it.receiverId else it.senderId }
            //如果某个session 没有聊天数据，就mock 一个空的message
            for (item in session) {
                if (!tmpTargetId.contains(item.targetId)) {
                    //mock 一个数据
                    res.add(
                        Message(
                            UUID.randomUUID().toString(),
                            item.targetId,
                            sendId,
                            "",
                            item.timeStamp,
                            true,
                            ""
                        )
                    )
                }
            }
            val recentSession = res.map {
                val targetUser =
                    ChatUserDB.getChatUserByID(if (it.senderId.equals(sendId)) it.receiverId else it.senderId)
                //计算未读消息

                RecentSession(
                    targetUser?.id ?: "",
                    targetUser?.name ?: "",
                    targetUser?.image ?: "",
                    it.senderId,
                    it.receiverId,
                    it.message,
                    it.dateTime,
                    it.isView,
                    getUnreadNum(sendId, targetUser?.id ?: ""),
                    it.type
                )
            }
            Log.i("fcm", "result = $recentSession")
            return recentSession
        } catch (exception: Exception) {
            Log.i("fcm", " getNewerMessage2 error ${exception.printStackTrace()}")
            return ArrayList<RecentSession>()
        }
    }

    //获得未读消息数量
    fun getUnreadNum(userID: String, receiverID: String): Int {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val item = database.getValueFromStatement(
                StatementSelect().select(DBMessage.id.count().distinct()).from("messageTable")
                    .where(
                        DBMessage.receiverId.eq(userID).and(DBMessage.senderId.eq(receiverID))
                            .and(DBMessage.isView.eq(false))
                    )
            )
            val res = item?.int ?: 0
            Log.i("fcm", " getUnreadNum $userID: String, $receiverID: String ${res}")
            return res
        } catch (exception: Exception) {
            Log.i("fcm", " updateMessageViewStatus error ${exception.printStackTrace()}")
            return 0
        }
    }


    //将userID和receId中所有user没有读的都设置为已读
    fun updateMessageViewStatus(userID: String, receiverID: String): Boolean {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            database.updateValue(
                Value(true),
                DBMessage.isView,
                "messageTable",
                DBMessage.receiverId.eq(userID).and(DBMessage.senderId.eq(receiverID))
            )
            return true
        } catch (exception: Exception) {
            Log.i("fcm", " updateMessageViewStatus error ${exception.printStackTrace()}")
            return false
        }
    }

    fun delete(sendId: String, receiverId: String) {
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        try {
            Log.i("WCDB", "delete message by senderId $sendId and receiverId $receiverId")
            val expression1 = Expression(Column("senderId")).eq(sendId)
                .and(Expression(Column("receiverId")).eq(receiverId))
            val expression2 = Expression(Column("senderId")).eq(receiverId)
                .and(Expression(Column("receiverId")).eq(sendId))
            database.deleteObjects("messageTable", expression1.or(expression2));
        } catch (exception: Exception) {
            Log.i("WCDB", exception.printStackTrace().toString())
        }
    }
}