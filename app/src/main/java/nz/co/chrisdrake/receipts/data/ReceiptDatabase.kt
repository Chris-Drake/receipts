package nz.co.chrisdrake.receipts.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ReceiptEntity::class,
        ReceiptItemEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class ReceiptDatabase : RoomDatabase() {

    abstract fun receiptDao(): ReceiptDao
}