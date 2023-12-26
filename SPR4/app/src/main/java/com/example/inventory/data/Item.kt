package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.Date

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "price")
    val itemPrice: Double,
    @ColumnInfo(name = "quantity")
    val quantityInStock: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Date,
    @ColumnInfo(name = "color")
    val color: Int,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "purchase_location")
    val purchaseLocation: String
)

fun Item.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance().format(itemPrice)
