package com.example.chatapp.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.databinding.ItemContainerReceivedImageBinding
import com.example.chatapp.databinding.ItemContainerReceivedMessageBinding
import com.example.chatapp.databinding.ItemContainerSentImageBinding
import com.example.chatapp.databinding.ItemContainerSentMessageBinding
import com.example.chatapp.models.Message
import com.example.chatapp.models.db.MessageDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.utils.MyLog
import com.example.chatapp.utils.TimeUtil


class ChatAdapter : Adapter<ViewHolder>() {

    private var receiverProfileImage: Bitmap? = null
    private var senderProfileImage: Bitmap? = null
    private lateinit var chatMessages: MutableList<Message>
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private var hasMore = true
    private lateinit var context: Context

    //一次查询数量
    private var limit: Long = 10

    //查询位置
    private var offset: Long = 0
    private val VIEW_TYPE_SENT_MESSAGE = 11
    private val VIEW_TYPE_RECEIVED_MESSAGE = 21
    private val VIEW_TYPE_SENT_IMAGE = 12
    private val VIEW_TYPE_RECEIVED_IMAGE = 22

    companion object {
        var displayModel = 1
    }
    //默认左右分布
    fun setReceiverProfileImage(image: Bitmap) {
        receiverProfileImage = image
    }

    fun setSenderProfileImage(image: Bitmap) {
        senderProfileImage = image
    }

    fun setChatMessage(message: List<Message>) {
        chatMessages = message.toMutableList()
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun setSenderId(id: String) {
        senderId = id
    }

    fun setReceiverId(id: String) {
        receiverId = id
    }

    fun initData() {
        val chatData = MessageDB.getAllMessage(senderId, receiverId, limit, offset)
        setChatMessage(chatData)
    }

    //将信息添加到尾部
    fun addMessage(message: Message) {
        chatMessages.add(message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (displayModel) {
            //布局左对齐
            Constants.DISPLAYMODEL_LEFTALIGH -> {
                when (viewType) {
                    VIEW_TYPE_RECEIVED_MESSAGE -> {
                        return ReceivedMessageViewHolder(
                            ItemContainerReceivedMessageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }

                    VIEW_TYPE_RECEIVED_IMAGE -> {
                        return ReceivedImageViewHolder(
                            ItemContainerReceivedImageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }
                }
            }

            else -> {
                when (viewType) {
                    VIEW_TYPE_RECEIVED_MESSAGE -> {
                        return ReceivedMessageViewHolder(
                            ItemContainerReceivedMessageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }

                    VIEW_TYPE_RECEIVED_IMAGE -> {
                        return ReceivedImageViewHolder(
                            ItemContainerReceivedImageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }

                    VIEW_TYPE_SENT_MESSAGE -> {
                        return SentMessageViewHolder(
                            ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }

                    VIEW_TYPE_SENT_IMAGE -> {
                        return SentImageViewHolder(
                            ItemContainerSentImageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }

                    else -> {
                        return SentImageViewHolder(
                            ItemContainerSentImageBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false
                            )
                        )
                    }
                }
            }
        }
        return SentImageViewHolder(
            ItemContainerSentImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(
            "fcm",
            "onBindViewHolder  getItemViewType(position) = " + getItemViewType(position).toString()
        )
        when (displayModel) {
            Constants.DISPLAYMODEL_LEFTALIGH -> {
                when (getItemViewType(position)) {
                    VIEW_TYPE_RECEIVED_MESSAGE -> {
                        holder as ReceivedMessageViewHolder
                        chatMessages[position].let {
                            holder.setData(
                                it,
                                if(it.senderId == senderId) senderProfileImage else receiverProfileImage,
                                it.senderId
                            )
                        }
                    }

                    VIEW_TYPE_RECEIVED_IMAGE -> {
                        holder as ReceivedImageViewHolder
                        chatMessages[position].let {
                            holder.setData(
                                it,
                                if(it.senderId == senderId) senderProfileImage else receiverProfileImage,
                                it.senderId
                            )
                        }
                    }
                }
            }

            else -> {
                when (getItemViewType(position)) {
                    VIEW_TYPE_RECEIVED_MESSAGE -> {
                        holder as ReceivedMessageViewHolder
                        chatMessages[position].let {
                            holder.setData(
                                it,
                                receiverProfileImage,
                                it.senderId
                            )
                        }
                    }

                    VIEW_TYPE_RECEIVED_IMAGE -> {
                        holder as ReceivedImageViewHolder
                        chatMessages[position].let {
                            holder.setData(
                                it,
                                receiverProfileImage,
                                it.senderId
                            )
                        }
                    }

                    VIEW_TYPE_SENT_MESSAGE -> {
                        holder as SentMessageViewHolder
                        chatMessages[position].let {
                            holder.setData(it, senderProfileImage)
                        }
                    }

                    VIEW_TYPE_SENT_IMAGE -> {
                        holder as SentImageViewHolder
                        chatMessages[position].let {
                            holder.setData(it, senderProfileImage)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("fcm", "getItemViewType = " + chatMessages[position].type)
        when (displayModel) {
            Constants.DISPLAYMODEL_LEFTALIGH -> {
                return if (chatMessages[position].type == Constants.CHAT_TYPE_IMAGE) {
                    Log.i("fcm", "getItemViewType image")
                    VIEW_TYPE_RECEIVED_IMAGE
                } else {
                    Log.i("fcm", "getItemViewType message")
                    VIEW_TYPE_RECEIVED_MESSAGE
                }
            }

            else -> {
                return if ((chatMessages[position].senderId ?: "error_id") == senderId) {
                    if (chatMessages[position].type == Constants.CHAT_TYPE_IMAGE) {
                        Log.i("fcm", "getItemViewType image")
                        VIEW_TYPE_SENT_IMAGE
                    } else {
                        Log.i("fcm", "getItemViewType message")
                        VIEW_TYPE_SENT_MESSAGE
                    }
                } else {
                    if (chatMessages[position].type == Constants.CHAT_TYPE_IMAGE) {
                        Log.i("fcm", "getItemViewType image")
                        VIEW_TYPE_RECEIVED_IMAGE
                    } else {
                        Log.i("fcm", "getItemViewType message")
                        VIEW_TYPE_RECEIVED_MESSAGE
                    }
                }
            }
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) :
        ViewHolder(binding.root) {
        fun setData(chatMessage: Message, senderProfileImage: Bitmap?) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = TimeUtil.getTime(chatMessage.dateTime.toLong())
            binding.imageProfile.setImageBitmap(senderProfileImage)
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemContainerReceivedMessageBinding) :
        ViewHolder(binding.root) {
        fun setData(
            chatMessage: Message,
            receiverProfileImage: Bitmap?,
            id: String
        ) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = TimeUtil.getTime(chatMessage.dateTime.toLong())
            binding.imageProfile.setImageBitmap(receiverProfileImage)
            if (id == senderId) {
                binding.textMessage.backgroundTintList =
                    ColorStateList.valueOf(android.graphics.Color.parseColor("#97C5E9"))
            }
        }
    }

    inner class SentImageViewHolder(private val binding: ItemContainerSentImageBinding) :
        ViewHolder(binding.root) {
        fun setData(chatMessage: Message, senderProfileImage: Bitmap?) {
            val bytes = Base64.decode(chatMessage.message, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imageShow.setImageBitmap(bitmap)
            binding.textDateTime.text = TimeUtil.getTime(chatMessage.dateTime.toLong())
            binding.imageProfile.setImageBitmap(senderProfileImage)
        }
    }

    inner class ReceivedImageViewHolder(private val binding: ItemContainerReceivedImageBinding) :
        ViewHolder(binding.root) {
        fun setData(
            chatMessage: Message,
            receiverProfileImage: Bitmap?,
            id: String
        ) {
            val bytes = Base64.decode(chatMessage.message, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imageShow.setImageBitmap(bitmap)
            binding.textDateTime.text = TimeUtil.getTime(chatMessage.dateTime.toLong())
            binding.imageProfile.setImageBitmap(receiverProfileImage)
            if (id == senderId) {
                binding.imageShow.backgroundTintList =
                    ColorStateList.valueOf(android.graphics.Color.parseColor("#97C5E9"))
            }
        }
    }

    fun updateList(): Int {
        if (hasMore) {
            offset += limit
            val newData = MessageDB.getAllMessage(senderId, receiverId, limit, offset)
            Log.i("fcm", "refresh $newData ")
            // 在原有的数据之上增加新数据
            if (newData.isNotEmpty()) {
                chatMessages.addAll(0, newData)
                return newData.size
            } else {
                hasMore = false
                return -1;
            }
        } else {
            //no more data
            Toast.makeText(context, "No more Data", Toast.LENGTH_LONG).show()
            return -1;
        }
    }
}