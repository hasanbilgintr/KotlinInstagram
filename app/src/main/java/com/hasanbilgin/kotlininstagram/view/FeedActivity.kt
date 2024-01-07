package com.hasanbilgin.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hasanbilgin.kotlininstagram.R
import com.hasanbilgin.kotlininstagram.adapter.FeedRecyclerAdapeter
import com.hasanbilgin.kotlininstagram.databinding.ActivityFeedBinding
import com.hasanbilgin.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var feedAdapter: FeedRecyclerAdapeter

    //    var postArrayList:ArrayList<Post>?=null//yada
    private lateinit var postArrayList: ArrayList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Post>()
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapeter(postArrayList)
        binding.recyclerView.adapter = feedAdapter

    }

    private fun getData() {
        //addOnSuccessListener bir kerelik çekim listesi için
        //addSnapshotListener herzaman güncellendiğinde yenilenen listedir
        //whereEqualTo("userEmail","atil@gmail.com")//userEmail kolonunun atil@gmail.com olanları çekicektir
        //whereGreaterThan("score",10) //score alanı 10dan büyük olanlar gelicektir
        //whereLessThan("score",10) //score alanı 10dan küçük olanlar gelicektir
        //whereNotEqualTo("score",10) //score alanı 10a eşit olamayanlar olanlar gelicektir
        //Query ->  com.google.firebase.firestore alındı
        //burda date ile sırala büyükten küçüğe
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        postArrayList.clear()
                        for (document in documents) {
                            val comment = document.get("commnet") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("dowsnloadUrl") as String
                            //println(comment)
                            val post = Post(userEmail, comment, downloadUrl)
                            postArrayList.add(post)

                        }

                        //veri güncellendiğinde çalışır
                        feedAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }

    //3 nokta için bağlama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //3 nokta için tıklanma
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post) {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout) {
            //çıkış yapıcaktır
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}