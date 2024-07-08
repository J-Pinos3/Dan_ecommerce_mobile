package com.example.kelineyt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kelineyt.R
import com.example.kelineyt.data.Address
import com.example.kelineyt.databinding.AddressRvItemBinding


class AddressAdapter: RecyclerView.Adapter<AddressAdapter.AddressVieHolder>() {

    inner class AddressVieHolder(val binding: AddressRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun render(address: Address, isSelected: Boolean){
            binding.apply {
                buttonAddress.text = address.addressTitle
                if(isSelected){
                    val color = ContextCompat.getColor( itemView.context ,R.color.g_blue )
                    buttonAddress.background = ColorDrawable( color )
                }else{
                    val color = ContextCompat.getColor( itemView.context ,R.color.g_white )
                    buttonAddress.background = ColorDrawable( color )
                }
            }
        }
    }


    private val diffUtil = object: DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
                    && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)


    var selectedAddress = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVieHolder {
        return AddressVieHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    init {
        differ.addListListener{_, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun onBindViewHolder(holder: AddressVieHolder, position: Int) {
        val address = differ.currentList[position]
        holder.render(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if(selectedAddress >= 0){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    override fun getItemCount() = differ.currentList.size


    var onClick: ((Address) -> Unit)? = null
}