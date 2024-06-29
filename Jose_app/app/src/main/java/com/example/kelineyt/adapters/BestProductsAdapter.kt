package com.example.kelineyt.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.ProductRvItemBinding
import java.text.DecimalFormat

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding):RecyclerView.ViewHolder(binding.root){
        private var remainingPercent = 0.0f
        private var priceAfterOffer = 0.0f
        fun render(product: Product){
            remainingPercent = 1f - (product.offerPercentage ?: 0.0f)
            priceAfterOffer = remainingPercent * product.price

            binding.tvName.text = product.name
            if( product.offerPercentage == null )
                binding.tvNewPrice.visibility = View.INVISIBLE

            binding.tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
            binding.tvPrice.paintFlags  = Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvPrice.text = "$ ${product.price}"

            Glide.with(binding.imgProduct.context).load(product.images[0]).into(binding.imgProduct)

        }
    }


    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.render(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Product) -> Unit)? = null
}