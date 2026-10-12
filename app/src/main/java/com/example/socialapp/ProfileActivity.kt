package com.example.socialapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = auth.currentUser?.uid ?: return finish()
        val ref = FirebaseDatabase.getInstance("https://social-app-82a54-default-rtdb.europe-west1.firebasedatabase.app").getReference("users").child(uid)

        ref.get().addOnSuccessListener { snap ->
            binding.etName.setText(snap.child("name").getValue(String::class.java) ?: "")
            binding.etBio.setText(snap.child("bio").getValue(String::class.java) ?: "")
        }
        binding.btnSave.setOnClickListener {
            ref.child("name").setValue(binding.etName.text.toString().trim())
            ref.child("bio").setValue(binding.etBio.text.toString().trim())
            Toast.makeText(this, "შენახულია!", Toast.LENGTH_SHORT).show()
        }
        binding.btnLogout.setOnClickListener { auth.signOut(); finish() }
    }
}
