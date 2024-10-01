package com.example.farmbook.modules

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmbook.adapter.InventoryAdapter
import com.example.farmbook.databinding.ActivityViewAllItemsBinding
import com.example.farmbook.model.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore

class ViewAllItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllItemsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var itemsAdapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance()
        binding.recyclerViewAllItems.layoutManager = LinearLayoutManager(this)

        // Get the item type (Recently Added, Low Stock, etc.) from the intent
        val itemType = intent.getStringExtra("ITEM_TYPE") ?: ""

        binding.tvTitle.text = itemType

        // Load the data based on the type
        loadItems(itemType)
    }

    private fun loadItems(itemType: String) {
        db.collection("inventory").get()
            .addOnSuccessListener { result ->
                val items = result.toObjects(InventoryItem::class.java)

                // Filter items based on the itemType
                val filteredItems = when (itemType) {
                    "Recently Added" -> items.takeLast(10) // Adjust the number as needed
                    "Low Stock" -> items.filter { it.stock in 1..49 }
                    "Out Of Stock" -> items.filter { it.stock == 0 }
                    else -> items
                }

                // Set up the adapter with filtered items
                itemsAdapter = InventoryAdapter(filteredItems) { deleteItem(it) }
                binding.recyclerViewAllItems.adapter = itemsAdapter
            }
    }

    private fun deleteItem(item: InventoryItem) {
        db.collection("inventory").document(item.name).delete()
            .addOnSuccessListener {
                // Reload the items after deletion
                val itemType = intent.getStringExtra("ITEM_TYPE") ?: ""
                loadItems(itemType)
            }
    }
}
