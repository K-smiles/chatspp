package com.example.chatapp.mvrx

import com.airbnb.mvrx.MavericksState

data class CounterState(val count: Int = 0) : MavericksState