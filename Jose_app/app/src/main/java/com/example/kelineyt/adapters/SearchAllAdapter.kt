package com.example.kelineyt.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.ProductRvItemBinding
import java.net.Socket
import javax.crypto.Cipher

class SearchAllAdapter: RecyclerView.Adapter<SearchAllAdapter.SearchAllViewHolder>() {

    inner class SearchAllViewHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){

        private var remainingPercent = 0.0f
        private var priceAfterOffer = 0.0f
        fun render(product: Product){
            remainingPercent = 1f - (product.offerPercentage ?: 0.0f)
            priceAfterOffer = remainingPercent * product.price

            binding.apply {
                tvName.text = product.name
                if(product.offerPercentage == null){
                    tvNewPrice.visibility  = View.INVISIBLE
                }
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvPrice.text = "$ ${product.price}"

                Glide.with(imgProduct.context).load(product.images[0]).into(imgProduct)
            }
        }//RENDER

    }//SEARCH ALL VIEW HOLDER

    private val diffCallback =  object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAllViewHolder {
        return SearchAllViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchAllViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.render(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Product) -> Unit)? = null

}