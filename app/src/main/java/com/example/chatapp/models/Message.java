package com.example.chatapp.models;

import com.tencent.wcdb.WCDBField;
import com.tencent.wcdb.WCDBTableCoding;

@WCDBTableCoding
public class Message {
    @WCDBField(isPrimary = true)
    public String id;
    @WCDBField
    public String senderId;
    @WCDBField
    public String receiverId;
    @WCDBField
    public String message;
    @WCDBField
    public String dateTime;
    @WCDBField
    public Boolean isView;
    @WCDBField
    public String session;
    //1 为普通类型 2 为图片
    @WCDBField
    public String type;


    public Message() {
    }

    public Message(String id, String senderId, String receiverId, String message, String dateTime, Boolean isView, String session) {
        this(id, senderId, receiverId, message, dateTime, isView, senderId, "1");
    }

    public Message(String id, String senderId, String receiverId, String message, String dateTime, Boolean isView, String session, String type) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.isView = isView;
        this.session = session;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", isView='" + isView + '\'' +
                ", session='" + session + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
