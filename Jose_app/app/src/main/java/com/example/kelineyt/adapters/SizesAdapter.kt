package com.example.kelineyt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kelineyt.databinding.ColorRvItemBinding
import com.example.kelineyt.databinding.SizeRvItemBinding

class SizesAdapter: RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    inner class SizesViewHolder(val binding: SizeRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun render(size:String, position: Int){

            binding.tvSize.text = size
            if (position == selectedPosition){ //size is selected
                binding.apply {
                    civImageShadow.visibility = View.VISIBLE
                }
            }else{//size is not selected
                binding.apply {
                    civImageShadow.visibility = View.INVISIBLE
                }
            }
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

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.render(size, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition)//unselect selected item
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)
            onItemClick?.invoke(size)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onItemClick: ((String) -> Unit)? = null

}