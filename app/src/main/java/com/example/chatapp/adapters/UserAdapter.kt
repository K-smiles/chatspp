package com.example.chatapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.databinding.ItemContainerUserBinding
import com.example.chatapp.listeners.UserListener
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.db.SessionDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.utils.MyLog

class UserAdapter : Adapter<UserAdapter.UserViewHolder>() {

    private var users = emptyList<ChatUser>()
    private var userListener: UserListener? = null
    private lateinit var currentUserId: String

    fun setCurrentUserId(id: String) {
        currentUserId = id
    }

    private fun getUserImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContainerUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        MyLog.v("这个是users的长度${users.size}")
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position]) {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ChatUser>) {
        users = data
        notifyDataSetChanged()
    }

    fun setUserListener(listener: UserListener) {
        userListener = listener
    }

    inner class UserViewHolder(private val binding: ItemContainerUserBinding) :
        ViewHolder(binding.root) {
        fun setUserData(user: ChatUser, callback: () -> Unit = {}) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            user.image?.let {
                if (it.isNotBlank())
                    binding.imageProfile.setImageBitmap(getUserImage(it))
            }
            binding.root.setOnClickListener {
                //创建会话
                SessionDB.insertSession(currentUserId, user.id)
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra(Constants.KEY_USER, user)
                startActivity(it.context, intent, null)
                userListener?.onUserClicked(user)
            }
            callback()
        }
    }
}