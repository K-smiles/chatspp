package com.example.chatapp.models.db

import android.util.Log
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.DBChatUser
import com.tencent.wcdb.core.Database
import com.tencent.wcdb.winq.Column
import com.tencent.wcdb.winq.Expression

object ChatUserDB {

    fun insertChatUser(user: ChatUser, success: () -> Unit, failure: () -> Unit) {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            database.insertObject(user, DBChatUser.allFields(), "userTable")
            success()
        } catch (exception: Exception) {
            failure()
        }
    }

    fun isContainChatUser(email: String, password: String): ChatUser? {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val expressionEmail = Expression(Column("email")).eq(email)
            val expressionPassword = Expression(Column("password")).eq(password)
            val result: ChatUser? = database.getFirstObject(DBChatUser.allFields(), "userTable",expressionEmail.and(expressionPassword))
            Log.i("fcm", "result = $result")
            return result
        } catch (exception: Exception) {
            return null
        }
    }

    fun getAllChatUser(): List<ChatUser>? {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")

            val result = database.getAllObjects(DBChatUser.allFields(), "userTable")
            Log.i("fcm", "result = $result")
            return result
        } catch (exception: Exception) {
            return null
        }
    }

    fun getChatUserByID(id:String): ChatUser? {
        try {
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val result = database.getFirstObject(DBChatUser.allFields(), "userTable",DBChatUser.id.eq(id))
            Log.i("fcm", "search by id result = $result")
            return result
        } catch (exception: Exception) {
            return null
        }
    }
}