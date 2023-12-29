package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "item",
        foreignKeys = [androidx.room.ForeignKey(
    entity = Owner::class,
    parentColumns = ["id"],
    childColumns = ["owner_id"],
    onDelete = androidx.room.ForeignKey.CASCADE
)],
    indices = [Index("owner_id")]
)
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
    val color: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "purchase_location")
    val purchaseLocation: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: Long
)

fun Item.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance().format(itemPrice)

fun Item.getFormattedDate(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(purchaseDate)
}