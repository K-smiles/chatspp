package com.example.chatapp.viewmodel


import com.airbnb.mvrx.MavericksViewModel
import com.example.chatapp.models.db.MessageDB
import com.example.chatapp.models.db.SessionDB
import com.example.chatapp.models.state.MainMVXRState

class MainViewModel(initialState: MainMVXRState) : MavericksViewModel<MainMVXRState>(initialState) {

    fun init(id: String) {
        val sessions =
            SessionDB.getSession(id)
        val recentSessions = MessageDB.getNewerMessage2(id, sessions)
        setState { copy(sessions = sessions, recentSessions = recentSessions) }
    }

}