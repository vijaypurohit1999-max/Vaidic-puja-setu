package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PujaBooking
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PujaDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun observeUserProfile(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfile(userId: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET isPanditOnline = :isOnline, updatedAtMillis = :updatedAt WHERE userId = :userId")
    suspend fun updatePanditOnlineStatus(userId: String, isOnline: Boolean, updatedAt: Long)

    @Query("SELECT * FROM user_profiles WHERE UPPER(referralCode) = UPPER(:referralCode) LIMIT 1")
    suspend fun findUserByReferralCode(referralCode: String): UserProfile?

    @Query("UPDATE user_profiles SET vedicCoins = vedicCoins + :coins, updatedAtMillis = :updatedAt WHERE userId = :userId")
    suspend fun incrementVedicCoins(userId: String, coins: Int, updatedAt: Long)

    @Query("UPDATE user_profiles SET appliedReferralCode = :referralCode, updatedAtMillis = :updatedAt WHERE userId = :userId")
    suspend fun updateAppliedReferralCode(userId: String, referralCode: String, updatedAt: Long)

    @Query("SELECT * FROM puja_bookings ORDER BY createdAtMillis DESC")
    fun observeAllBookings(): Flow<List<PujaBooking>>

    @Query("SELECT * FROM puja_bookings ORDER BY createdAtMillis DESC")
    suspend fun getAllBookings(): List<PujaBooking>

    @Query("SELECT * FROM puja_bookings WHERE id = :bookingId LIMIT 1")
    suspend fun getBookingById(bookingId: String): PujaBooking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: PujaBooking)

    @Query("UPDATE puja_bookings SET bookingStatus = :newStatus, updatedAtMillis = :updatedAt WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, newStatus: String, updatedAt: Long)
}

@Database(
    entities = [UserProfile::class, PujaBooking::class],
    version = 2,
    exportSchema = false
)
abstract class PujaDatabase : RoomDatabase() {
    abstract fun pujaDao(): PujaDao

    companion object {
        @Volatile
        private var INSTANCE: PujaDatabase? = null

        @Suppress("DEPRECATION")
        fun getInstance(context: Context): PujaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PujaDatabase::class.java,
                    "vaidik_puja_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
