package com.example.farmbook.modules

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.farmbook.databinding.ActivityAddItemBinding
import com.example.farmbook.model.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Enable Edge-to-Edge
        enableEdgeToEdge()

        // Submit button listener
        binding.btnSubmit.setOnClickListener {
            addItem()
        }
    }

    private fun addItem() {
        val name = binding.etItemName.text.toString().trim()
        val quantity = binding.etItemQuantity.text.toString().trim()
        val price = binding.etItemPrice.text.toString().trim()

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            binding.etItemName.error = "Item name is required"
            return
        }
        if (TextUtils.isEmpty(quantity)) {
            binding.etItemQuantity.error = "Quantity is required"
            return
        }
        if (TextUtils.isEmpty(price)) {
            binding.etItemPrice.error = "Price is required"
            return
        }

        val quantityInt = quantity.toIntOrNull()
        val priceDouble = price.toDoubleOrNull()

        if (quantityInt == null || priceDouble == null) {
            Toast.makeText(this, "Invalid quantity or price", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new inventory item
        val newItem = InventoryItem(
            name = name,
            stock = quantityInt,
            price = priceDouble
        )

        // Add to Firestore
        db.collection("inventory").document(name).set(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,HomeActivity::class.java))
                finish() // Close activity after adding the item
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
