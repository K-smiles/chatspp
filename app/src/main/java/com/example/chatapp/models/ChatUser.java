package com.example.chatapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tencent.wcdb.WCDBField;
import com.tencent.wcdb.WCDBTableCoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@WCDBTableCoding
public class ChatUser implements Parcelable {
    @WCDBField(isPrimary = true)
    public String id;
    @WCDBField
    public String name;
    @WCDBField
    String password;
    @WCDBField
    public String image;
    @WCDBField
    public String email;
    @WCDBField
    public byte[] topChat;
    //存储置顶聊天用户
    public HashSet<String> topChattemp;

    public ChatUser() {
        this.topChattemp = new HashSet<>();
    }

    ChatUser(Parcel source) {
        id = source.readString();
        name = source.readString();
        email = source.readString();
        image = source.readString();
    }

    public ChatUser(String id, String name, String password, String image, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.image = image;
        this.email = email;
        this.topChattemp = new HashSet<>();
    }

    public boolean addTopChat(String id) {
        if (topChattemp.contains(id))
            return false;
        topChattemp.add(id);
        return true;
    }

    public boolean synTmpToTopChat() {
        if (topChattemp.isEmpty())
            return true;
        topChat = new byte[topChattemp.size()];
        int i = 0;
        for (String str : topChattemp) {
            byte tmp = Integer.valueOf(str).byteValue();
            topChat[i] = tmp;
            i++;
        }
        return true;
    }

    public boolean synTopChatToTmp() {
        topChattemp = new HashSet<>();
        if (topChat == null || topChat.length == 0) {
            return true;
        }
        for (byte i : topChat) {
            int tmp = Integer.valueOf(i);
            topChattemp.add(String.valueOf(tmp));
        }
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUser chatUser = (ChatUser) o;
        return Objects.equals(id, chatUser.id) && Objects.equals(name, chatUser.name) && Objects.equals(password, chatUser.password) && Objects.equals(image, chatUser.image) && Objects.equals(email, chatUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, image, email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(image);
    }

    /**
     * 负责反序列化
     */
    public static final Creator<ChatUser> CREATOR = new Creator<ChatUser>() {
        /**
         * 从序列化对象中，获取原始的对象
         * @param source
         * @return
         */
        @Override
        public ChatUser createFromParcel(Parcel source) {
            return new ChatUser(source);
        }

        /**
         * 创建指定长度的原始对象数组
         * @param size
         * @return
         */
        @Override
        public ChatUser[] newArray(int size) {
            return new ChatUser[0];
        }
    };
}
