package com.example.chatapp.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.chatapp.databinding.ChatImageDisplayBinding

class CustomDialog  //显示的消息
constructor(context: Context) : AlertDialog(
    context
) {
    private lateinit var binding: ChatImageDisplayBinding
    private var message: String? = null
    private var title: String? = null
    private var positive: String? = null
    private var negtive: String? = null
    private var image: String? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatImageDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
    }

    //初始化页面控件的显示数据
    private fun refreshView() {
        //如果自定义了title和message的信息，则会在弹出框中显示
        if (!TextUtils.isEmpty(title)) {
            //设置标题控件的文本为自定义的title
            binding.title.text = title
            //标题控件设置为显示状态
            binding.title.visibility = View.VISIBLE
        } else {
            //否则标题控件设置为隐藏状态
            binding.title.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(message)) {
            //设置标题控件的文本为自定义的message
            binding.message.text = message
        }
        //如果没有自定义按钮的文本，则默认显示“确认”和“取消”
        if (!TextUtils.isEmpty(positive)) {
            //设置按钮控件的文本为自定义文本
            binding.positive.text = positive
        } else {
            binding.positive.text = "确定"
        }
        if (!TextUtils.isEmpty(negtive)) {
            //设置按钮控件的文本为自定义文本
            binding.negtive.text = negtive
        } else {
            binding.negtive.text = "取消"
        }
        binding.imageProfile.setImageBitmap(bitmap)

    }

    //初始化界面的确定和取消的监听器
    private fun initEvent() {
        //设置“确定”按钮的点击事件的监听器
        binding.positive.setOnClickListener {
            if (onClickBottomListener != null) {
                onClickBottomListener!!.onPositiveClick()
            }
        }
        binding.negtive.setOnClickListener {
            if (onClickBottomListener != null) {
                onClickBottomListener!!.onNegtiveClick()
            }
        }
    }

    override fun show() {
        super.show()
        refreshView()
    }

    interface OnClickBottomListener {
        fun onPositiveClick()

        fun onNegtiveClick()
    }

    //设置“确定”和“取消”按钮的回调
    var onClickBottomListener: OnClickBottomListener? = null

    fun setOnClickBottomListener(onClickBottomListener: OnClickBottomListener?): CustomDialog {
        this.onClickBottomListener = onClickBottomListener
        return this
    }

    fun setImage(
        image: String,
    ): CustomDialog {
        this.image = image
        return this
    }

    fun setBitmap(
        bitmap: Bitmap,
    ): CustomDialog {
        this.bitmap = bitmap
        return this
    }

    fun setAll(
        message: String?,
        title: String?,
        negtive: String?,
        positive: String?
    ): CustomDialog {
        this.message = message
        this.title = title
        this.negtive = negtive
        this.positive = positive
        return this
    }
}
