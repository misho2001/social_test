package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance("https://social-app-82a54-default-rtdb.europe-west1.firebasedatabase.app").getReference("posts")
    private val posts = mutableListOf<Pair<String, String>>() // author : text
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PostAdapter(posts)
        binding.recyclerPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerPosts.adapter = adapter

        binding.btnPost.setOnClickListener {
            val text = binding.etPost.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            val author = auth.currentUser?.email ?: "unknown"
            db.push().setValue(mapOf("author" to author, "text" to text))
                .addOnSuccessListener { binding.etPost.setText(""); Toast.makeText(this, "გამოქვეყნდა!", Toast.LENGTH_SHORT).show() }
        }
        binding.btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                posts.clear()
                for (p in snap.children) {
                    val a = p.child("author").getValue(String::class.java) ?: ""
                    val t = p.child("text").getValue(String::class.java) ?: ""
                    posts.add(0, a to t)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    class PostAdapter(val items: List<Pair<String, String>>) : RecyclerView.Adapter<PostAdapter.VH>() {
        class VH(v: TextView) : RecyclerView.ViewHolder(v)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false) as TextView)
        override fun getItemCount() = items.size
        override fun onBindViewHolder(h: VH, pos: Int) {
            val tv = h.itemView as TextView
            tv.text = items[pos].second
            tv.setLines(3); tv.maxLines = 6
        }
    }
}
