package com.example.chatapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.chatapp.adapters.ChatAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.dialog.CustomDialog
import com.example.chatapp.models.ChatUser
import com.example.chatapp.models.Message
import com.example.chatapp.models.db.MessageDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.untilities.PreferenceManager
import com.example.chatapp.utils.MyLog
import com.example.chatapp.utils.showToastShort
import com.google.android.material.internal.ViewUtils.dpToPx
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.UUID

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: ChatUser
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
    }

    override fun onStart() {
        super.onStart()
        binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun init() {
        preferenceManager = PreferenceManager.getInstance(applicationContext)
        chatAdapter = ChatAdapter().apply {
            if (receiverUser.image.isNotBlank()) {
                setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image))
            }
            setSenderProfileImage(
                getBitmapFromEncodedString(
                    preferenceManager.getString(Constants.KEY_IMAGE) ?: ""
                )
            )
            setSenderId(preferenceManager.getString(Constants.KEY_USER_ID) ?: "errorId")
            setReceiverId(receiverUser.id)
            setContext(applicationContext)
            initData()
        }
        binding.chatRecyclerView.adapter = chatAdapter
        binding.progressBar.visibility = View.GONE
        binding.chatRecyclerView.visibility = View.VISIBLE
        //update read status
        MessageDB.updateMessageViewStatus(
            preferenceManager.getString(Constants.KEY_USER_ID) ?: "errorId", receiverUser.id
        )
    }

    private fun sendMessage() {
        val message = Message(
            UUID.randomUUID().toString(),
            preferenceManager.getString(Constants.KEY_USER_ID) ?: "errorUserId",
            receiverUser.id,
            binding.inputMessage.text.toString(),
            System.currentTimeMillis().toString(),
            false,
            "group1"
        )
        MessageDB.insert(message)
        binding.inputMessage.text = null
        //更新消息
        chatAdapter.addMessage(message)
        chatAdapter.notifyItemInserted(chatAdapter.itemCount)
        binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadReceiverDetails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            receiverUser = intent.getParcelableExtra(Constants.KEY_USER, ChatUser::class.java)
                ?: ChatUser().also { MyLog.v("未能读取到用户信息") }
            binding.textName.text = receiverUser.name
        } else {
            receiverUser = intent.getParcelableExtra<ChatUser>(Constants.KEY_USER) as ChatUser
            binding.textName.text = receiverUser.name
        }
    }

    private fun setListeners() {
        //返回
        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        //修改按钮颜色
        binding.inputMessage.doAfterTextChanged {
            if (it != null) {
                if (it.isNotEmpty()) {
                    binding.layoutSend.setOnClickListener {
                        sendMessage()
                    }
                    binding.sendIcon.imageTintList =
                        ColorStateList.valueOf(android.graphics.Color.BLUE)
                } else {
                    binding.layoutSend.isClickable = false
                    binding.sendIcon.imageTintList =
                        ColorStateList.valueOf(android.graphics.Color.LTGRAY)
                }
            }
        }
        //上拉刷新
        binding.srLayout.setOnRefreshListener {
            binding.srLayout.isRefreshing = true
            //chatAdapter.resetData()
            val res = chatAdapter.updateList()
            if (res > 0) {
                chatAdapter.notifyItemRangeInserted(0, res)
            }
            //binding.chatRecyclerView.scrollToPosition(if (chatAdapter.itemCount > 10) 10 else 0)
            mHandler.postDelayed({ binding.srLayout.isRefreshing = false }, 1000)
        }
        //图片上传
        binding.layoutImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = dpToPx(applicationContext, 150).toInt()
//        根据原始图片的宽高比 来计算缩略图的高度
        val previewHeight = bitmap.height * previewWidth / bitmap.width
//        false表示不应用滤镜
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val bos = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val bytes = bos.toByteArray()
//      Bitmap对象编码为一个经过压缩和缩放的JPEG格式的Base64编码字符串
//      Base64 字符串可以用于网络传输或存储，因为它已经是文本格式，不受二进制数据可能遇到的问题
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage = registerForActivityResult(
        //StartActivityForResult 用于启动任意意图（Intent）,并且期望从那个 Activity 获取结果
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                //第一个data是ActivityResult 类型的 result 对象的属性,表示从返回的 Activity 中获取的 Intent 对象，包含了所有返回的数据
                //第二个data 是 Intent 类型对象的属性
                val imageUri = result.data?.data
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val encodedImage = encodeImage(bitmap)
                    //打开对话框显示图片

                    val dialog = CustomDialog(this);
                    dialog.setAll("您确定要发送给 ${receiverUser.name}", "", "取消", "确定");
                    dialog.setImage(encodedImage)
                    dialog.setBitmap(bitmap)
                    dialog.setOnClickBottomListener(object : CustomDialog.OnClickBottomListener {

                        override fun onPositiveClick() {
                            //发送图片到数据库
                            val message = Message(
                                UUID.randomUUID().toString(),
                                preferenceManager.getString(Constants.KEY_USER_ID) ?: "errorUserId",
                                receiverUser.id,
                                encodedImage,
                                System.currentTimeMillis().toString(),
                                false,
                                "group1",
                                Constants.CHAT_TYPE_IMAGE
                            )
                            Log.i("fcm", message.type)
                            MessageDB.insert(message)
                            //更新消息
                            chatAdapter.addMessage(message)
                            chatAdapter.notifyItemInserted(chatAdapter.itemCount)
                            dialog.dismiss();
                        }

                        override fun onNegtiveClick() {
                            showToastShort("请选择头像")
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

}