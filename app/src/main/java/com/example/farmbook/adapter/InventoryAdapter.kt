package com.example.farmbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmbook.databinding.ItemInventoryBinding
import com.example.farmbook.model.InventoryItem

class InventoryAdapter(
    private var items: List<InventoryItem>,
    private val onDeleteClick: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    // Use ViewBinding for the ViewHolder
    class InventoryViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem, onDeleteClick: (InventoryItem) -> Unit) {
            binding.tvItemName.text = item.name
            binding.tvItemQuantity.text = "Stock: ${item.stock}"
            binding.btnDeleteItem.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onDeleteClick)
    }

    override fun getItemCount(): Int = items.size

    // Update the list of items
    fun updateData(newItems: List<InventoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
