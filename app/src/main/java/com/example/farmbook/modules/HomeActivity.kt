package com.example.farmbook.modules

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmbook.R
import com.example.farmbook.adapter.InventoryAdapter
import com.example.farmbook.databinding.ActivityHomeBinding
import com.example.farmbook.model.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var recentlyAddedAdapter: InventoryAdapter
    private lateinit var outOfStockAdapter: InventoryAdapter
    private lateinit var lowStockAdapter: InventoryAdapter
    private lateinit var addItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        // Set up RecyclerViews
        setupRecyclerViews()

        // Load data from Firestore
        loadInventoryData()

        // Initialize the result launcher
        addItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Reload the data when returning from AddItemActivity
                refreshInventoryLists()
            }
        }

        // Add new item on FAB click
        binding.fabAddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
        binding.recentSeeAll.setOnClickListener {
            val intent = Intent(this, ViewAllItemsActivity::class.java)
            intent.putExtra("ITEM_TYPE", "Recently Added")
            startActivity(intent)
        }

        binding.lowStockSeeAll.setOnClickListener {
            val intent = Intent(this, ViewAllItemsActivity::class.java)
            intent.putExtra("ITEM_TYPE", "Low Stock")
            startActivity(intent)
        }

        binding.outStockViewAll.setOnClickListener {
            val intent = Intent(this, ViewAllItemsActivity::class.java)
            intent.putExtra("ITEM_TYPE", "Out Of Stock")
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut() // Sign the user out from Firebase

            // After logging out, redirect to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the task stack
            startActivity(intent)
            finish() // Close the HomeActivity
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
                val items = result.documents.map { document ->
                    var item = document.toObject(InventoryItem::class.java)
                    item?.id = document.id  // Assign Firestore document ID to InventoryItem
                    item
                }.filterNotNull()

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
        db.collection("inventory").document(item.id).delete()
            .addOnSuccessListener {
                loadInventoryData() // Reload data after deletion
            }
    }

    private fun refreshInventoryLists() {
        // Load the data again from Firestore
        db.collection("inventory").get()
            .addOnSuccessListener { result ->
                val items = result.documents.map { document ->
                    var item = document.toObject(InventoryItem::class.java)
                    item?.id = document.id  // Ensure Firestore document ID is assigned
                    item  // Return the item with the ID
                }.filterNotNull()

                // Filter the data for each section
                val recentlyAddedItems = items.takeLast(10)  // Recently added
                val lowStockItems = items.filter { it.stock in 1..49 }  // Low stock
                val outOfStockItems = items.filter { it.stock == 0 }  // Out of stock

                // Update the adapters for each section
                recentlyAddedAdapter.updateData(recentlyAddedItems)
                lowStockAdapter.updateData(lowStockItems)
                outOfStockAdapter.updateData(outOfStockItems)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to refresh lists: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
