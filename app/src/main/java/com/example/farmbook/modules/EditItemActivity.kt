package com.example.farmbook.modules

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmbook.databinding.ActivityEditItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItemBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var itemId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Get the item details from intent
        val name = intent.getStringExtra("itemName")
        val stock = intent.getIntExtra("itemStock", 0)
        val price = intent.getDoubleExtra("itemPrice", 0.0)
        itemId = intent.getStringExtra("itemId") ?: ""

        // Populate fields with existing item data
        binding.etEditItemName.setText(name)
        binding.etEditItemQuantity.setText(stock.toString())
        binding.etEditItemPrice.setText(price.toString())

        // Update button listener
        binding.btnUpdate.setOnClickListener {
            if (itemId.isNotEmpty()) {
                updateItem(itemId)
            } else {
                Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateItem(itemId: String) {
        val name = binding.etEditItemName.text.toString().trim()
        val quantity = binding.etEditItemQuantity.text.toString().trim()
        val price = binding.etEditItemPrice.text.toString().trim()

        // Validate inputs
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val quantityInt = quantity.toIntOrNull()
        val priceDouble = price.toDoubleOrNull()

        if (quantityInt == null || priceDouble == null) {
            Toast.makeText(this, "Invalid quantity or price", Toast.LENGTH_SHORT).show()
            return
        }

        // Update the item in Firestore
        val updatedItem = mapOf(
            "name" to name,
            "stock" to quantityInt,
            "price" to priceDouble
        )

        db.collection("inventory").document(itemId)
            .update(updatedItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // Close activity after updating the item
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
