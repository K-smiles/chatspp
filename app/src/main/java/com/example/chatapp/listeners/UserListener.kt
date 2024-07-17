package com.example.chatapp.listeners

import com.example.chatapp.models.ChatUser


interface UserListener {
    fun onUserClicked(user: ChatUser)
}