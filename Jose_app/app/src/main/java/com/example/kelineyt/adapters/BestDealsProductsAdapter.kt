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
        val df = DecimalFormat("#####.##")
        fun render(product: Product){
            binding.tvDealProductName.text = product.name
            binding.tvOldPrice.text = df.format(product.price).toString()
            binding.tvNewPrice.text =df.format(product.offerPercentage ).toString()

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
    }

    override fun getItemCount(): Int = differ.currentList.size

}