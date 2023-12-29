package com.example.inventory.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owner")
    fun getAllOwners(): Flow<List<Owner>>
    @Query("SELECT * FROM owner WHERE id = :ownerId")
    fun getOwner(ownerId: Int):  Flow<Owner>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(owner: Owner)
    @Update
    suspend fun update(owner: Owner)
    @Delete
    suspend fun delete(owner: Owner)
    @Query("SELECT * FROM owner WHERE first_name || ' ' || last_name = :ownerFullName LIMIT 1")
    fun getOwnerByName(ownerFullName: String): Owner?
    @Query("SELECT first_name || ' ' || last_name FROM owner WHERE id = :ownerId")
    suspend fun getOwnerNameById(ownerId: Long): String?

}