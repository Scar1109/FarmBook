package com.example.farmbook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmbook.databinding.ItemInventoryBinding
import com.example.farmbook.model.InventoryItem
import com.example.farmbook.modules.EditItemActivity

class InventoryAdapter(
    private var items: List<InventoryItem>,
    private val onDeleteClick: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    // Use ViewBinding for the ViewHolder
    class InventoryViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem, onDeleteClick: (InventoryItem) -> Unit) {
            binding.tvItemName.text = item.name
            binding.tvItemQuantity.text = "Stock: ${item.stock}"

            // Handle delete button click
            binding.btnDeleteItem.setOnClickListener {
                onDeleteClick(item)
            }

            // Handle edit button click
            binding.btnEditItem.setOnClickListener {
                // Launch the EditItemActivity to edit the item
                val context = binding.root.context
                val intent = Intent(context, EditItemActivity::class.java).apply {
                    putExtra("itemId", item.id) // Pass the item ID to the edit screen
                    putExtra("itemName", item.name)
                    putExtra("itemStock", item.stock)
                    putExtra("itemPrice", item.price)
                }
                context.startActivity(intent)
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
