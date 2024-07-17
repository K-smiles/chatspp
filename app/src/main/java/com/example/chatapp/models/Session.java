package com.example.chatapp.models;

import com.tencent.wcdb.WCDBField;
import com.tencent.wcdb.WCDBTableCoding;

@WCDBTableCoding
public class Session {
    @WCDBField(isPrimary = true)
    public String id;
    @WCDBField
    public String createId;
    @WCDBField
    public String targetId;
    @WCDBField
    public String timeStamp;

    public Session() {

    }

    public Session(String id, String createId, String targetId, String timeStamp) {
        this.id = id;
        this.createId = createId;
        this.targetId = targetId;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", createId='" + createId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
