package com.example.kelineyt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kelineyt.databinding.ColorRvItemBinding

class ColorsAdapter:RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    private var selectedPosition = -1

    inner class ColorsViewHolder(val binding: ColorRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun render(color:Int, position: Int){
            val imageDrawable = ColorDrawable(color)
            binding.civImageColors.setImageDrawable(imageDrawable)

            if (position == selectedPosition){ //color is selected
                binding.apply {
                    civImageShadow.visibility = View.VISIBLE
                    ivImagePicked.visibility = View.VISIBLE
                }
            }else{//color is not selected
                binding.apply {
                    civImageShadow.visibility = View.INVISIBLE
                    ivImagePicked.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object:DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.render(color, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition)//unselect selected item
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)
            onItemClick?.invoke(color)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onItemClick: ((Int) -> Unit)? = null
}