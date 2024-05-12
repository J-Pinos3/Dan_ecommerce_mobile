package com.example.kelineyt.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.ProductRvItemBinding
import java.text.DecimalFormat

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding):RecyclerView.ViewHolder(binding.root){
        val df = DecimalFormat("#####.##")
        fun render(product: Product){
            binding.tvName.text = product.name
            binding.tvNewPrice.text = product.offerPercentage.toString()
            binding.tvPrice.text = product.price.toString()

            Glide.with(binding.imgProduct.context).load(product.images[0]).into(binding.imgProduct)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}