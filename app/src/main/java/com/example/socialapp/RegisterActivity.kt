package com.example.socialapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || pass.length < 6) {
                Toast.makeText(this, "პაროლი მინიმუმ 6 სიმბოლო", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { res ->
                    val uid = res.user!!.uid
                    FirebaseDatabase.getInstance("https://social-app-82a54-default-rtdb.europe-west1.firebasedatabase.app").getReference("users").child(uid)
                        .setValue(mapOf("name" to name, "email" to email, "bio" to ""))
                        .addOnCompleteListener { Toast.makeText(this, "რეგისტრაცია დასრულდა!", Toast.LENGTH_SHORT).show(); finish() }
                }
                .addOnFailureListener { Toast.makeText(this, "შეცდომა: ${it.message}", Toast.LENGTH_LONG).show() }
        }
    }
}
