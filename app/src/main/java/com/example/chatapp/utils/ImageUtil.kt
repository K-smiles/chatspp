package com.example.chatapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.google.android.material.internal.ViewUtils.dpToPx
import java.io.ByteArrayOutputStream


object ImageUtil {
    @SuppressLint("RestrictedApi")
    fun encodeImage(context: Context, bitmap: Bitmap): String {
        val previewWidth = dpToPx(context, 150).toInt()
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
}