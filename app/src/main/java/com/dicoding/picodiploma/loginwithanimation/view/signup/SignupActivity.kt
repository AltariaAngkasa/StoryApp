package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.customview.Button
import com.dicoding.picodiploma.loginwithanimation.customview.EmailCustom
import com.dicoding.picodiploma.loginwithanimation.customview.NamaCustom
import com.dicoding.picodiploma.loginwithanimation.customview.PasswordCustom
import com.dicoding.picodiploma.loginwithanimation.data.pref.ResultData
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var nameText: NamaCustom
    private lateinit var passwordText: PasswordCustom
    private lateinit var emailText: EmailCustom
    private lateinit var myButton: Button
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        passwordText = binding.edRegisterPassword
        emailText = binding.edRegisterMail
        nameText = binding.nameEditText
        myButton = binding.signupButton


        passwordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButton()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        emailText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButton()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        setupView()
        playAnimation()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
//            val email = binding.edRegisterMail.text.toString()

            AlertDialog.Builder(this).apply {
                setTitle("Yeayy")
                setMessage("Akun kamu sudah terdaftar.")
                setPositiveButton("Next") { _, _ ->
                    val intent = Intent(context, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun register() {
        val name = binding.nameEditText.text.toString()
        val email = binding.edRegisterMail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.register(name, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultData.Loading -> {
                        showLoading(true)
                    }

                    is ResultData.Success -> {
                        showToast(result.data.toString())
                        showLoading(false)
                        setupAction()
                    }

                    is ResultData.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.edRegisterMail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setMyButton() {
        val emailResult = emailText.text.toString().isNotEmpty() && emailText.error == null
        val passwordResult = passwordText.text.toString().isNotEmpty() && passwordText.error == null

        if (emailResult && passwordResult) {
            myButton.isEnabled = true
            myButton.setOnClickListener { register() }
        } else {
            myButton.isEnabled = false
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}