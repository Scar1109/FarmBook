package com.example.farmbook.modules

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmbook.R
import com.example.farmbook.adapter.InventoryAdapter
import com.example.farmbook.databinding.ActivityHomeBinding
import com.example.farmbook.model.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var recentlyAddedAdapter: InventoryAdapter
    private lateinit var outOfStockAdapter: InventoryAdapter
    private lateinit var lowStockAdapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Set up RecyclerViews
        setupRecyclerViews()

        // Load data from Firestore
        loadInventoryData()

        // Add new item on FAB click
        binding.fabAddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    private fun setupRecyclerViews() {
        // Set up RecyclerView for Recently Added items
        binding.recyclerRecentlyAdded.layoutManager = LinearLayoutManager(this)
        recentlyAddedAdapter = InventoryAdapter(emptyList()) { deleteItem(it) }
        binding.recyclerRecentlyAdded.adapter = recentlyAddedAdapter

        // Set up RecyclerView for Out of Stock items
        binding.recyclerOutOfStock.layoutManager = LinearLayoutManager(this)
        outOfStockAdapter = InventoryAdapter(emptyList()) { deleteItem(it) }
        binding.recyclerOutOfStock.adapter = outOfStockAdapter

        // Set up RecyclerView for Low Stock items
        binding.recyclerLowStock.layoutManager = LinearLayoutManager(this)
        lowStockAdapter = InventoryAdapter(emptyList()) { deleteItem(it) }
        binding.recyclerLowStock.adapter = lowStockAdapter
    }

    private fun loadInventoryData() {
        // Fetch all items from Firestore
        db.collection("inventory").get()
            .addOnSuccessListener { result ->
                val items = result.toObjects(InventoryItem::class.java)

                // Filter data for each section
                val recentlyAddedItems = items.takeLast(5) // Assuming last added items
                val outOfStockItems = items.filter { it.stock == 0 }
                val lowStockItems = items.filter { it.stock in 1..49 }

                // Update adapters with filtered data
                recentlyAddedAdapter.updateData(recentlyAddedItems)
                outOfStockAdapter.updateData(outOfStockItems)
                lowStockAdapter.updateData(lowStockItems)
            }
    }

    private fun deleteItem(item: InventoryItem) {
        // Delete item from Firestore
        db.collection("inventory").document(item.name).delete()
            .addOnSuccessListener {
                loadInventoryData() // Reload data after deletion
            }
    }
}
