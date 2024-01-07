package com.hasanbilgin.kotlininstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hasanbilgin.kotlininstagram.databinding.ActivityUploadBinding
import com.hasanbilgin.kotlininstagram.databinding.RecyclerRowBinding
import com.hasanbilgin.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapeter(private val postList: ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapeter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRecyclerAdapeter.PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedRecyclerAdapeter.PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentTextView.text = postList.get(position).comment
        //picassso ve glide resim çekimi için
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}