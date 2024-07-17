package com.example.chatapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivitySignInBinding
import com.example.chatapp.models.db.ChatUserDB
import com.example.chatapp.untilities.Constants
import com.example.chatapp.untilities.PreferenceManager
import com.example.chatapp.utils.showToastShort
import com.example.chatapp.utils.textToString
import com.example.chatapp.utils.textToStringAndTrim


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager.getInstance(applicationContext)
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetails()) {
                signIn()
            }
        }
    }

    private fun signIn() {
        loading(true)
        //查询与输入的邮箱和密码匹配的用户
        ChatUserDB.isContainChatUser(
            binding.inputEmail.textToString(),
            binding.inputPassword.textToString()
        )?.let  {
            it.synTopChatToTmp();
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
            preferenceManager.putString(Constants.KEY_USER_ID, it.id)
            preferenceManager.putString(
                Constants.KEY_IMAGE,
                it.image
            )
            preferenceManager.putString(
                Constants.KEY_NAME,
                it.name
            )
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } ?:let {
            loading(false)
            showToastLong("登入失败,请检查用户信息")
        }
        Log.i("fcm","false")
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonSignIn.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToastLong(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun isValidSignInDetails(): Boolean {
        return if (binding.inputEmail.textToStringAndTrim().isEmpty()) {
            showToastShort("请输入邮箱地址")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.textToString()).matches()) {
            showToastShort("无效邮箱地址")
            false
        } else if (binding.inputPassword.textToStringAndTrim().isEmpty()) {
            showToastShort("请输入密码")
            false
        } else true
    }
}