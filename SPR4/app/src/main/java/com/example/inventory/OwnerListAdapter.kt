package com.example.inventory

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Owner
import com.example.inventory.databinding.OwnerListOwnerBinding

class OwnerListAdapter(private val onOwnerClicked: (Owner) -> Unit):
    ListAdapter<Owner, OwnerListAdapter.OwnerViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        return OwnerViewHolder(
            OwnerListOwnerBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }
    override fun onBindViewHolder(holder: OwnerListAdapter.OwnerViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            Log.d("AddItemFragmentAdapter", "Binding owner: $current")
            onOwnerClicked(current)
        }
        holder.bind(current)
    }
    class OwnerViewHolder(private var binding: OwnerListOwnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(owner: Owner) {
            with(binding) {
                ownerFirstName.text = owner.firstName
                ownerLastName.text = owner.lastName
            }
        }
    }
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Owner>() {
            override fun areItemsTheSame(oldItem: Owner, newItem: Owner): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Owner, newItem: Owner): Boolean {
                return oldItem == newItem
            }
        }
    }
}