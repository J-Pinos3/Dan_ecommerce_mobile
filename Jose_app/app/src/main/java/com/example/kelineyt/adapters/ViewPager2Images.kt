package com.example.kelineyt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.databinding.ViewpagerImageItemBinding

class ViewPager2Images: RecyclerView.Adapter<ViewPager2Images.ViewPager2ViewHolder>() {

    inner class ViewPager2ViewHolder(val binding: ViewpagerImageItemBinding): RecyclerView.ViewHolder(binding.root){
        fun render(imageUrl: String){
            Glide.with(binding.ivProductDetails.context).load(imageUrl).into(binding.ivProductDetails)
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ViewHolder {
        return ViewPager2ViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewPager2ViewHolder, position: Int) {
        val imageUrl = differ.currentList[position]
        holder.render(imageUrl)
    }

    override fun getItemCount(): Int = differ.currentList.size

}
