package com.example.fixu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var fullname: String
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        sessionManager = SessionManager(this)

        onFocusNameListener()
        onFocusPhoneNumListener()
        onFocusEmailListener()
        onFocusPasswordListener()

        binding.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateConfirmPasswords()
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        binding.signupButton.setOnClickListener {
            if (isInputNotEmpty()) {
                validateAll()
                if (isValidAll()){
                    registerAccountWithFirebase()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Not valid.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } else {
                validateAll()
            }
        }

        binding.loginDirect.setOnClickListener {
            moveToLogin()
        }
    }

    private fun registerAccountWithFirebase() {
        showLoading(true)
        email = binding.edtEmail.getText().toString().trim()
        password = binding.edtPassword.getText().toString().trim()
        fullname = binding.edtName.getText().toString().trim()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.addAuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname)
                                .build()
                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        getToken { token ->
                                            sessionManager.saveSession(
                                                token = token,
                                                userId = user.uid,
                                                email = user.email ?: "",
                                                name = user.displayName
                                            )
                                            moveToLogin()
                                        }
                                    }
                                }
                        }
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getToken(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token ?: ""
                callback(idToken)
            } else {
                Log.e("Token Firebase", "Failed to get token", task.exception)
                callback("")
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.signUpLoading.visibility = View.VISIBLE
        } else {
            binding.signUpLoading.visibility = View.GONE
        }
    }

    private fun onFocusNameListener() {
        binding.edtName.setOnFocusChangeListener{ _, focused ->
            if (!focused) {
                binding.nameContainer.error = validateName()
            }
        }
    }

    private fun validateName(): String? {
        val nameText = binding.edtName.text.toString()
        if (nameText.length < 4) {
            return "Name must have at least 4 characters"
        }
        return null
    }

    private fun onFocusPhoneNumListener() {
        binding.edtPhoneNumber.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.phoneNumberContainer.error = validatePhoneNum()
            }
        }
    }

    private fun validatePhoneNum(): String? {
        val phoneNum = binding.edtPhoneNumber.text.toString()
        if (!phoneNum.matches(".*[0-9].*".toRegex())) {
            return "Phone number must all digits"
        } else if (phoneNum.length <10 || phoneNum.length > 13){
            return "Phone number must have 10-13 digits"
        }
        return null
    }

    private fun onFocusEmailListener() {
        binding.edtEmail.setOnFocusChangeListener{ _, focused ->
            if (!focused) {
                binding.emailContainer.error = validateEmail()
            }
        }
    }

    private fun validateEmail(): String? {
        val emailText = binding.edtEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid email address"
        }

        return null
    }

    private fun onFocusPasswordListener() {
        binding.edtPassword.setOnFocusChangeListener{ _, focused ->
            if (!focused) {
                binding.passwordContainer.error = validatePassword()
            }
        }
    }

    private fun validatePassword(): String? {
        val passwordText = binding.edtPassword.text.toString()
        if (passwordText.length < 8) {
            return "Password must have at least 8 characters"
        }
        if (!passwordText.matches(".*[A-Z]+.*".toRegex())) {
            return "Password must contain an uppercase"
        }
        if (!passwordText.matches(".*[0-9]+.*".toRegex())) {
            return "Password must contain a number"
        }

        return null
    }

    private fun validateConfirmPasswords() {
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        if (password != confirmPassword) {
            binding.confirmPasswordContainer.error = "Password not match"
        } else {
            binding.confirmPasswordContainer.error = null
        }
    }

    private fun validateAll() {
        binding.nameContainer.error = validateName()
        binding.phoneNumberContainer.error = validatePhoneNum()
        binding.passwordContainer.error = validatePassword()
        binding.emailContainer.error = validateEmail()
        validateConfirmPasswords()
    }

    private fun isValidAll(): Boolean {
        return (binding.nameContainer.error == null && binding.phoneNumberContainer.error == null
                && binding.emailContainer.error == null && binding.passwordContainer.error == null
                && binding.confirmPasswordContainer.error == null)
    }

    private fun isInputNotEmpty(): Boolean {
        val nameText = binding.edtName.text.toString()
        val phoneNum = binding.edtPhoneNumber.text.toString()
        val emailText = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        return (nameText.isNotEmpty() && phoneNum.isNotEmpty()
                && emailText.isNotEmpty() && password.isNotEmpty()
                && confirmPassword == password )
    }
}