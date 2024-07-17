package com.example.chatapp.models.state

import com.airbnb.mvrx.MavericksState
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.Session
import com.example.chatapp.models.vo.RecentSession

data class MainMVXRState(
    val recentSessions: List<RecentSession>,
    val sessions: List<Session>
) : MavericksState {

}