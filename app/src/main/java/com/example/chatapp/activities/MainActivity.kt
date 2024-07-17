package com.example.chatapp.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.adapters.ChatAdapter
import com.example.chatapp.adapters.RecentConversationAdapter
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.vo.RecentSession
import com.example.chatapp.models.Session
import com.example.chatapp.models.db.ChatUserDB
import com.example.chatapp.models.db.MessageDB
import com.example.chatapp.models.db.SessionDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.untilities.PreferenceManager
import com.example.chatapp.utils.showToastShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private var chatMessages: List<RecentSession> = ArrayList<RecentSession>()
    private lateinit var conversationAdapter: RecentConversationAdapter
    private lateinit var currentUser: ChatUser
    private lateinit var sessions: List<Session>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager.getInstance(applicationContext)
        setContentView(binding.root)
        setListeners()
        registerForContextMenu(binding.conversationRecyclerView)
        lifecycleScope.launch {
            val name = withContext(Dispatchers.IO)
            {
                preferenceManager.getString(Constants.KEY_NAME)
            }
            binding.textName.text = name
        }
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO)
            {
                val bytes =
                    Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            binding.imageProfile.setImageBitmap(bitmap)
        }
        init()
    }

    /**
     * 初始化数据
     */
    private fun init() {

        currentUser = ChatUserDB.getChatUserByID(
            preferenceManager.getString(Constants.KEY_USER_ID) ?: "error_id"
        )!!


        currentUser.synTopChatToTmp()
        conversationAdapter = RecentConversationAdapter().apply {
            setUserID(currentUser.id)
        }
        binding.conversationRecyclerView.adapter = conversationAdapter
        binding.progressBar.visibility = View.GONE
        binding.conversationRecyclerView.visibility = View.VISIBLE
    }

    /**
     * 更新数据，各个会话的头像，最新消息等
     */
    override fun onStart() {
        super.onStart()
        refreshData(true, true, true)
    }

    private fun setListeners() {
        //登出
        binding.imageSignOut.setOnClickListener {
            signOut()
        }
        //聊天布局按钮
        binding.chatLayoutSetting.setOnClickListener {
            if (ChatAdapter.displayModel == Constants.DISPLAYMODEL_LEFTRIGHT) {
                ChatAdapter.displayModel = Constants.DISPLAYMODEL_LEFTALIGH
            } else {
                ChatAdapter.displayModel = Constants.DISPLAYMODEL_LEFTRIGHT
            }
        }
        //跳转到用户activity
        binding.fabNewChat.setOnClickListener {
            runBlocking {
                Log.d("runBlocking", "启动一个协程")
            }
            GlobalScope.launch {
                Log.d("launch", "启动一个协程")
            }
            GlobalScope.async {
                Log.d("async", "启动一个协程")
            }
            //startActivity(Intent(applicationContext, UserActivity::class.java))
        }
    }


    /**
     * 登出，清空 sharepreference数据
     */
    private fun signOut() {
        try {
            showToastShort("正在注销...")
            preferenceManager.clear()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        } catch (exception: Exception) {
            showToastShort("注销失败")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //获取到的是listView里的条目信息
        when (item.itemId) {
            1 -> {
                //置顶设置与取消，如果已经置顶，就取消，否则置顶
                if (currentUser.topChattemp.contains(item.groupId.toString())) {
                    currentUser.topChattemp.remove(item.groupId.toString())
                } else {
                    currentUser.addTopChat(item.groupId.toString())
                }
                refreshData(false, false, true)
            }

            2 -> {
                //删除置顶,会话及历史消息
                if (currentUser.topChattemp.contains(item.groupId.toString())) {
                    currentUser.topChattemp.remove(item.groupId.toString())
                }
                SessionDB.deleteSession(currentUser.id, item.groupId.toString())
                MessageDB.delete(currentUser.id, item.groupId.toString())
                //更新
                refreshData(true, true, true)
            }

            3 -> {
                //已读会话
                MessageDB.updateMessageViewStatus(currentUser.id, item.groupId.toString())
                refreshData(false, true, false)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun refreshData(
        updateSession: Boolean,
        updateUnread: Boolean,
        updateChatTop: Boolean
    ) {
        conversationAdapter.apply {
            if (updateChatTop) {
                setTopChat(currentUser.topChattemp)
            }
            if (updateSession) {
                //加载会话
                sessions =
                    SessionDB.getSession(
                        preferenceManager.getString(Constants.KEY_USER_ID) ?: "error_id"
                    )
            }
            if (updateSession || updateUnread) {
                Log.i("fcm", "before read " + chatMessages)
                //根据会话更新聊天
                chatMessages = MessageDB.getNewerMessage2(
                    preferenceManager.getString(Constants.KEY_USER_ID) ?: "error_id",
                    sessions
                )
                setMessages(chatMessages)
                Log.i("fcm", "after read " + chatMessages)
            }
            sortMessage()
        }
        conversationAdapter.notifyDataSetChanged()
    }
}