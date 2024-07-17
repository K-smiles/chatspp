package com.example.chatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.databinding.ActivityUserBinding
import com.example.chatapp.listeners.UserListener
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.db.ChatUserDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.untilities.PreferenceManager

class UserActivity : AppCompatActivity(), UserListener {

    private lateinit var binding: ActivityUserBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var currentUserId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager.getInstance(applicationContext)
        setContentView(binding.root)
        setListeners()
        getUser()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getUser() {
        loading(true)
        ChatUserDB.getAllChatUser()?.let {
            loading(false)
            currentUserId = preferenceManager.getString(Constants.KEY_USER_ID) ?: "errorId"
            if (it.isNotEmpty()) {
                val users = ArrayList<ChatUser>()
                for (queryDocumentSnapshot in it) {
                    // 如果其 ID 不等于当前用户的 ID，则创建一个新的 User 对象,也就是说 不显示当前登入的用户
                    if (currentUserId == queryDocumentSnapshot.id) continue
                    users.add(queryDocumentSnapshot)
                }
                if (users.size > 0) {
                    val userAdapter = UserAdapter().apply {
                        setCurrentUserId(currentUserId)
                        setData(users)
                        setUserListener(UserActivity())
                    }
                    binding.userRecyclerView.adapter = userAdapter
                    binding.userRecyclerView.visibility = View.VISIBLE
                } else
                    showErrorMessage()
            } else
                showErrorMessage()
        } ?: {
            showErrorMessage()
        }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "没有可用的用户")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onUserClicked(user: ChatUser) {
        finish()
    }
}