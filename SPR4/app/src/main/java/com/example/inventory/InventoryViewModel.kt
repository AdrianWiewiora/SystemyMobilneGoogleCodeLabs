package com.example.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.data.Owner
import com.example.inventory.data.OwnerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class InventoryViewModel(
    private val itemDao: ItemDao,
    private val ownerDao: OwnerDao
) : ViewModel() {

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItemEntry(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemCategory: String,
        itemPurchaseDate: Date,
        itemColor: String,
        itemLocation: String,
        itemPurchaseLocation: String,
        ownerId: Long
    ): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            category = itemCategory,
            purchaseDate = itemPurchaseDate,
            color = itemColor,
            location = itemLocation,
            purchaseLocation = itemPurchaseLocation,
            ownerId = ownerId
        )
    }

    private val _allItems = MutableLiveData<List<Item>>()
    val allItems: LiveData<List<Item>> get() = _allItems

    val allOwners: LiveData<List<Owner>> = ownerDao.getAllOwners().asLiveData()

    fun addNewItem(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemCategory: String,
        itemPurchaseDate: Date,
        itemColor: String,
        itemLocation: String,
        itemPurchaseLocation: String,
        ownerId: Long
    ) {
        val newItem = getNewItemEntry(
            itemName,
            itemPrice,
            itemCount,
            itemCategory,
            itemPurchaseDate,
            itemColor,
            itemLocation,
            itemPurchaseLocation,
            ownerId
        )
        insertItem(newItem)
    }

    fun isEntryValid(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemCategory: String,
        itemColor: String,
        itemLocation: String,
        itemPurchaseLocation: String,
    ): Boolean {
        return !(itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank() ||
                itemCategory.isBlank() || itemColor.isBlank() || itemLocation.isBlank() ||
                itemPurchaseLocation.isBlank())
    }

    suspend fun getOwnerIdByName(ownerFullName: String): Int {
        return viewModelScope.async(Dispatchers.IO) {
            var ownerId = -1
            val owner = ownerDao.getOwnerByName(ownerFullName)
            ownerId = owner?.id ?: -1
            ownerId
        }.await()
    }


    fun isEntryValidOwner(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        address: String,
        email: String
    ): Boolean {
        return !(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()
                || address.isBlank() || email.isBlank())
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun retrieveOwner(id: Int): LiveData<Owner> {
        return ownerDao.getOwner(id).asLiveData()
    }
    private fun updateOwner(owner: Owner) {
        viewModelScope.launch {
            ownerDao.update(owner)
        }
    }

    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    fun deleteOwner(owner: Owner) {
        viewModelScope.launch {
            ownerDao.delete(owner)
        }
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemCategory: String,
        itemPurchaseDate: Date,
        itemColor: String,
        itemLocation: String,
        itemPurchaseLocation: String,
        ownerId: Long
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            category = itemCategory,
            purchaseDate = itemPurchaseDate,
            color = itemColor,
            location = itemLocation,
            purchaseLocation = itemPurchaseLocation,
            ownerId = ownerId
        )
    }

    private fun getUpdatedOwnerEntry(
        ownerId: Int,
        ownerFirstName: String,
        ownerSecondName: String,
        ownerPhoneNumber: String,
        ownerAddress: String,
        ownerEmail: String,
    ): Owner {
        return Owner(
            id = ownerId,
            firstName = ownerFirstName,
            lastName = ownerSecondName,
            phoneNumber = ownerPhoneNumber,
            address = ownerAddress,
            email = ownerEmail,
        )
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemCategory: String,
        itemPurchaseDate: Date,
        itemColor: String,
        itemLocation: String,
        itemPurchaseLocation: String,
        ownerId: Long
    ) {
        val updatedItem = getUpdatedItemEntry(
            itemId,
            itemName,
            itemPrice,
            itemCount,
            itemCategory,
            itemPurchaseDate,
            itemColor,
            itemLocation,
            itemPurchaseLocation,
            ownerId
        )
        updateItem(updatedItem)
    }

    fun updateOwner(
        ownerId: Int,
        ownerFirstName: String,
        ownerSecondName: String,
        ownerPhoneNumber: String,
        ownerAddress: String,
        ownerEmail: String,
    ) {
        val updatedOwner = getUpdatedOwnerEntry(
            ownerId,
            ownerFirstName,
            ownerSecondName,
            ownerPhoneNumber,
            ownerAddress,
            ownerEmail,
        )
        updateOwner(updatedOwner)
    }
    fun sortItems(sortType: SortType) {
        viewModelScope.launch {
            val sortedItems = when (sortType) {
                SortType.ASCENDING_NAME -> itemDao.getItemsSortedByNameAscending()
                SortType.DESCENDING_NAME -> itemDao.getItemsSortedByNameDescending()
                SortType.BY_PRICE_ASC -> itemDao.getItemsSortedByPriceAscending()
                SortType.BY_PRICE_DESC -> itemDao.getItemsSortedByPriceDescending()
                SortType.BY_QUANTITY_ASC -> itemDao.getItemsSortedByQuantityAscending()
                SortType.BY_QUANTITY_DESC -> itemDao.getItemsSortedByQuantityDescending()
            }

            _allItems.postValue(sortedItems)
        }
    }
    suspend fun getOwnerNameById(ownerId: Long): String? {
        return ownerDao.getOwnerNameById(ownerId)
    }

    private fun insertOwner(owner: Owner) {
        viewModelScope.launch {
            ownerDao.insert(owner)
        }
    }
    private fun getNewOwnerEntry(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        address: String,
        email: String
    ): Owner {
        return Owner(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            address = address,
            email = email
        )
    }
    fun addNewOwner(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        address: String,
        email: String
    ) {
        val newOwner = getNewOwnerEntry(firstName, lastName, phoneNumber, address, email)
        insertOwner(newOwner)
    }

}

class InventoryViewModelFactory(
    private val itemDao: ItemDao,
    private val ownerDao: OwnerDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao, ownerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}