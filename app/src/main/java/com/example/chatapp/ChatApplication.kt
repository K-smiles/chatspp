package com.example.chatapp


import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.airbnb.mvrx.Mavericks
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.DBChatUser
import com.example.chatapp.models.DBMessage
import com.example.chatapp.models.DBSession
import com.example.chatapp.models.Message
import com.example.chatapp.models.Session
import com.example.chatapp.untilities.Constants
import com.example.chatapp.utils.ImageUtil.encodeImage
import com.tencent.wcdb.core.Database

class ChatApplication : Application() {

    companion object {
        var context: Context? = null
    }

    //启动时加载数据，包括用户，聊天记录
    override fun onCreate() {
        super.onCreate()
        initDataBaseAndTable()
        mockChatUserData()
        mockMessageData()
        mockSessionData()
        context = applicationContext
        Mavericks.initialize(applicationContext,null,null)
    }

    private fun initDataBaseAndTable() {
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        database.createTable("userTable", DBChatUser.INSTANCE)
        database.createTable("sessionTable", DBSession.INSTANCE)
        database.createTable("messageTable", DBMessage.INSTANCE)
    }

    private fun mockChatUserData() {
        val encodedImage = encodeImage(
            applicationContext,
            BitmapFactory.decodeResource(resources, com.example.chatapp.R.drawable.back)
        )
        val encodedImage2 = encodeImage(
            applicationContext,
            BitmapFactory.decodeResource(resources, com.example.chatapp.R.drawable.awatr)
        )
        val user1 = ChatUser("1", "Jack", "123", encodedImage, "jack@gmail.com")
        val user2 = ChatUser("2", "Alex", "123", encodedImage2, "alex@gmail.com")
        val user3 = ChatUser("3", "Candy", "123", encodedImage, "candy@gmail.com")
        val user4 = ChatUser("4", "July", "123", encodedImage2, "july@gmail.com")
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        try {
            database.insertObject(user1, DBChatUser.allFields(), "userTable")
            database.insertObject(user2, DBChatUser.allFields(), "userTable")
            database.insertObject(user3, DBChatUser.allFields(), "userTable")
            database.insertObject(user4, DBChatUser.allFields(), "userTable")
        } catch (exception: Exception) {
            Log.i("WCDB", "Insert chat user fail")
        }
    }

    private fun mockSessionData() {
        val date = System.currentTimeMillis()
        val session1 = Session("1", "1", "2", date.toString())
        val session2 = Session("2", "1", "3", date.toString())
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        try {
            database.insertObject(session1, DBSession.allFields(), "sessionTable")
            database.insertObject(session2, DBSession.allFields(), "sessionTable")
            database.insertObject(
                Session("3", "1", "4", date.toString()),
                DBSession.allFields(),
                "sessionTable"
            )
            Log.i("WCDB", "insert successful")
        } catch (exception: Exception) {
            Log.i("WCDB", exception.printStackTrace().toString())
        }
    }

    private fun mockMessageData() {
        val date = System.currentTimeMillis()
        val database = Database("/data/data/com.example.chatapp/chatuser.db")
        try {
            database.insertObject(
                Message("1", "1", "2", "hi", date.toString(), true, "group1"),
                DBMessage.allFields(),
                "messageTable"
            )
            database.insertObject(
                Message(
                    "2",
                    "2",
                    "1",
                    "hi",
                    (date + 2000).toString(),
                    false,
                    "group1"
                ), DBMessage.allFields(), "messageTable"
            )
//            database.insertObject(Message("3", "1", "3", "hi", (date).toString(), false, "group2"), DBMessage.allFields(), "messageTable")
//            database.insertObject(Message("4", "1", "3", "hi", (date + 3000).toString(), false, "group2"), DBMessage.allFields(), "messageTable")
            database.insertObject(
                Message("5", "1", "4", "hi", (date).toString(), true, "group2"),
                DBMessage.allFields(),
                "messageTable"
            )
            database.insertObject(
                Message("6", "4", "1", "hi", (date + 1000).toString(), true, "group2"),
                DBMessage.allFields(),
                "messageTable"
            )
            database.insertObject(
                Message(
                    "7",
                    "4",
                    "1",
                    "hi jack",
                    (date + 1000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "8",
                    "4",
                    "1",
                    "This is July",
                    (date + 1000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "9",
                    "4",
                    "1",
                    "I want to ",
                    (date + 2000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "10",
                    "4",
                    "1",
                    "go to swim today",
                    (date + 3000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "11",
                    "4",
                    "1",
                    "how about you",
                    (date + 4000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "12",
                    "1",
                    "4",
                    "I am fine",
                    (date + 5000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "13",
                    "1",
                    "4",
                    "I also want to go",
                    (date + 6000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "14",
                    "1",
                    "4",
                    "What is you plan",
                    (date + 7000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "15",
                    "4",
                    "1",
                    "I am still working",
                    (date + 8000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "16",
                    "4",
                    "1",
                    "I will finish all work on time",
                    (date + 9000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "17",
                    "4",
                    "1",
                    "test",
                    (date + 10000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "18",
                    "1",
                    "4",
                    "test",
                    (date + 11000).toString(),
                    true,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )
            database.insertObject(
                Message(
                    "19",
                    "4",
                    "1",
                    "test",
                    (date + 12000).toString(),
                    false,
                    "group2"
                ), DBMessage.allFields(), "messageTable"
            )


        } catch (exception: Exception) {
            Log.i("WCDB", exception.printStackTrace().toString())
        }
    }
}