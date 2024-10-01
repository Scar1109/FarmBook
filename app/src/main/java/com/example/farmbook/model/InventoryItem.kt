package com.example.farmbook.model

data class InventoryItem(
    var id: String = "",
    val name: String = "",
    val quality: String = "",
    val price: Double = 0.0,
    val stock: Int = 0 // Used for determining low/out of stock
)
