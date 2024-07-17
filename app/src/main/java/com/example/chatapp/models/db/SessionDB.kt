package com.example.chatapp.models.db

import android.util.Log
import com.example.chatapp.models.DBMessage
import com.example.chatapp.models.DBSession
import com.example.chatapp.models.Message
import com.example.chatapp.models.Session
import com.tencent.wcdb.core.Database
import com.tencent.wcdb.winq.Column
import com.tencent.wcdb.winq.Expression
import java.util.UUID

object SessionDB {

    fun getSession(ownerId: String): List<Session> {
        try {
            Log.i("fcm", "ownerId = $ownerId  ")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")

            val result = database.getAllObjects(
                DBSession.allFields(),
                "sessionTable",
                DBSession.createId.eq(ownerId)
            )
            Log.i("fcm", "sessions result = $result")
            return result
        } catch (exception: Exception) {
            Log.i("fcm", " sessions error")
            return ArrayList<Session>()
        }
    }

    fun isExistSession(ownerId: String, targetId: String): Boolean {
        try {
            Log.i("fcm", "isExistSession ownerId = $ownerId targetId = $targetId ")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            val id = database.getFirstObject(
                DBSession.allFields(),
                "sessionTable",
                DBSession.createId.eq(ownerId).and(DBSession.targetId.eq(targetId))
            )
            Log.i("fcm", "isExistSession result is $id")
            return id != null
        } catch (exception: Exception) {
            Log.i("fcm", " sessions error")
            return false
        }
    }

    fun deleteSession(ownerId: String, targetId: String): Boolean {
        try {
            Log.i("fcm", "deleteSession ownerId = $ownerId targetId = $targetId ")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            database.deleteObjects(
                "sessionTable",
                DBSession.createId.eq(ownerId).and(DBSession.targetId.eq(targetId))
            )
            return true
        } catch (exception: Exception) {
            Log.i("fcm", " sessions error")
            return false
        }
    }

    fun insertSession(ownerId: String, targetId: String): Boolean {
        try {
            Log.i("fcm", "insertSession ownerId = $ownerId targetId = $targetId ")
            val database = Database("/data/data/com.example.chatapp/chatuser.db")
            //检查是否已经创建
            if (isExistSession(ownerId, targetId)) {
                Log.i("fcm", "insertSession success session always exists")
                return true
            }

            val session = Session(
                UUID.randomUUID().toString(),
                ownerId,
                targetId,
                System.currentTimeMillis().toString()
            )
            database.insertObject(session, DBSession.allFields(), "sessionTable")
            Log.i("fcm", "insertSession success")
            return true
        } catch (exception: Exception) {
            Log.i("fcm", " insertSession error" + exception.printStackTrace())
            return false
        }
    }

}