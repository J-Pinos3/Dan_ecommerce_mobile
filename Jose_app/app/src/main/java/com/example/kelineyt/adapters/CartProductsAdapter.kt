package com.example.kelineyt.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.data.Product
import com.example.kelineyt.databinding.CartProductItemBinding
import com.example.kelineyt.databinding.SpecialRvItemBinding
import com.example.kelineyt.helper.getProductPrice

class CartProductsAdapter: RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder( val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root){
        fun render(cartProduct: CartProduct){
            //binding.tvSpecialProductName.text = cartProduct.product.name
            binding.tvProductCartName.text = cartProduct.product.name
            binding.tvProductCartQuantity.text = cartProduct.quantity.toString()

            val priceAfterPercentage = cartProduct
                .product.offerPercentage.getProductPrice(cartProduct.product.price)

            binding.tvProductCartPrice.text = priceAfterPercentage.toString()
            Glide.with(itemView).load(cartProduct.product.images[0]).into(binding.ivimageCartProduct)

            binding.cimimageCartProductColor.setImageDrawable(
                ColorDrawable(cartProduct.selectedColor ?: Color.TRANSPARENT)
            )

            binding.tvCartProductSize.text = cartProduct.selectedSize ?: "".also {
                binding.cimimageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }


        }
    }


    private val diffCallBack = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.render(product)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(product)
        }

        holder.binding.ivimagePlus.setOnClickListener {
            onPlusClick?.invoke(product)
        }

        holder.binding.ivimageMinus.setOnClickListener {
            onMinusClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick:  ((CartProduct) -> Unit)? = null
    var onMinusClick:  ((CartProduct) -> Unit)? = null
}