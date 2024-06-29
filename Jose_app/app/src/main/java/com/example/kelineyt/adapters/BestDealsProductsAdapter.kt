package com.example.kelineyt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.BestDealsRvItemBinding
import java.text.DecimalFormat

class BestDealsProductsAdapter: RecyclerView.Adapter<BestDealsProductsAdapter.BestDealsProductsViewHolder>() {


    inner class BestDealsProductsViewHolder(private val binding: BestDealsRvItemBinding):RecyclerView.ViewHolder(binding.root){
        private var remainingPercent = 0.0f
        private var priceAfterOffer = 0.0f
        fun render(product: Product){
            remainingPercent = 1f - (product.offerPercentage ?: 0.0f)
            priceAfterOffer = remainingPercent * product.price

            binding.tvDealProductName.text = product.name
            binding.tvOldPrice.text = "$ ${product.price}"
            binding.tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"

            Glide.with(binding.imgBestDeal.context).load(product.images[0]).into(binding.imgBestDeal)
        }
    }


    private val diffCallBack = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsProductsViewHolder {
        return BestDealsProductsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: BestDealsProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.render(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Product) -> Unit)? = null
}