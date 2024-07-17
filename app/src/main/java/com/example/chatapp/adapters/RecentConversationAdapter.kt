package com.example.chatapp.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.databinding.ItemContainerRecentConversionBinding
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.vo.RecentSession
import com.example.chatapp.untilities.Constants
import com.example.chatapp.utils.TimeUtil

class RecentConversationAdapter : Adapter<RecentConversationAdapter.ConversionViewHolder>() {

    private lateinit var chatMessages: List<RecentSession>
    private lateinit var userID: String
    private lateinit var setTopChat: Set<String>

    fun setMessages(message: List<RecentSession>) {
        chatMessages = message
    }

    fun setUserID(id: String) {
        userID = id
    }

    fun setTopChat(set: Set<String>) {
        setTopChat = set
    }

    fun sortMessage() {
        Log.i("fcm", "置顶的用户为为$setTopChat")
        //sort by top chat
        val c1: Comparator<RecentSession> = Comparator { o1, o2 ->
            val o1Re = if (userID.equals(o1.senderId)) o1.receiverId else o1.senderId
            val o2Re = if (userID.equals(o2.senderId)) o2.receiverId else o2.senderId

            val o1Res = setTopChat.contains(o1Re)
            val o2Res = setTopChat.contains(o2Re)
            if (o1Res && o2Res) {
                //compare time
                if (o1.timeStamp > o2.timeStamp) {
                    -1
                } else {
                    1
                }

            } else if (o1Res) {
                -1
            } else if (o2Res) {
                1
            } else {
                //compare time
                if (o1.timeStamp > o2.timeStamp) {
                    -1
                } else {
                    1
                }

            }
        }
        Log.i("fcm", "排序前的数据为$chatMessages")
        chatMessages = chatMessages.sortedWith(c1)
        Log.i("fcm", "排序后的数据为$chatMessages")
    }

    inner class ConversionViewHolder(private val binding: ItemContainerRecentConversionBinding) :
        ViewHolder(binding.root), OnCreateContextMenuListener {
        private lateinit var message: RecentSession

        init {
            binding.root.setOnCreateContextMenuListener(this)
        }

        fun setData(
            chatMessage: RecentSession,
        ) {
            message = chatMessage
            //如果是收到消息
            if (chatMessage.receiverId.equals(userID)) {
                if (message.type == Constants.CHAT_TYPE_IMAGE) {
                    binding.textRecentMessage.text =
                        message.name + " : " + "图片"
                } else {
                    if (chatMessage.message.isBlank()) {
                        binding.textRecentMessage.text = ""
                    } else {
                        binding.textRecentMessage.text =
                            message.name + " : " + chatMessage.message
                    }
                }
            } else {
                if (message.type == Constants.CHAT_TYPE_IMAGE) {
                    binding.textRecentMessage.text = "图片"
                } else {
                    if (chatMessage.message.isBlank()) {
                        binding.textRecentMessage.text = ""
                    } else {
                        binding.textRecentMessage.text = chatMessage.message
                    }
                }
            }

            binding.textName.text = message.name
            binding.time.text = TimeUtil.getTimeMD(chatMessage.timeStamp.toLong())
            binding.imageProfile.setImageBitmap(getUserImage(message.image))

            //未读消息
            //current id equal send it
            if (!chatMessage.senderId.equals(userID) && !chatMessage.isView) {
                binding.isunread.visibility = View.VISIBLE
                //设置未读消息数
                binding.isunread.text =
                    if (message.unRead > 99) "99+" else message.unRead.toString()
            } else {
                binding.isunread.visibility = View.GONE
            }
            //置顶颜色设置
            if (setTopChat.contains(message.toUserid)) {
                binding.root.setBackgroundColor(android.graphics.Color.CYAN)
                Log.i(
                    "fcm",
                    "setTopChat.contains ${message.toUserid}" + setTopChat.contains(message.toUserid)
                        .toString()
                )
            } else {
                binding.root.setBackgroundColor(android.graphics.Color.WHITE)
            }

            binding.root.setOnClickListener {
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra(
                    Constants.KEY_USER,
                    ChatUser(message.toUserid, message.name, "", message.image, "")
                )
                startActivity(it.context, intent, null)
            }
        }

        private fun getUserImage(encodedImage: String): Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            if (menu != null) {
                menu.setHeaderTitle("Select The Action")
                val id =
                    if (userID.equals(message.senderId)) message.receiverId else message.senderId
                if (v != null) {
                    menu.add(id.toInt(), 1, 0, "置顶")
                    menu.add(id.toInt(), 2, 0, "删除")
                    menu.add(id.toInt(), 3, 0, "已读")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }
}