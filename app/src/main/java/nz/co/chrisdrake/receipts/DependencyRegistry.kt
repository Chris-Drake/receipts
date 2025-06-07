package nz.co.chrisdrake.receipts

import android.app.Application
import androidx.room.Room
import nz.co.chrisdrake.receipts.data.ReceiptDatabase
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.domain.CopyPictureToInternalStorage
import nz.co.chrisdrake.receipts.domain.GetReceipt
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.GetTempImageUri
import nz.co.chrisdrake.receipts.domain.SaveReceipt
import nz.co.chrisdrake.receipts.domain.SignIn
import nz.co.chrisdrake.receipts.domain.SignUp
import nz.co.chrisdrake.receipts.domain.UpdateReceipt
import kotlin.reflect.KClass

object DependencyRegistry {

    private val _dependencies = mutableMapOf<KClass<*>, Lazy<*>>()

    val dependencies: Map<KClass<*>, Lazy<*>> = _dependencies

    inline fun <reified T : Any> get(): T = dependencies.getValue(T::class).value as T

    fun registerApplicationDependencies(application: Application) {
        register {
            Room.databaseBuilder(
                context = application,
                klass = ReceiptDatabase::class.java,
                name = "receipt_database"
            ).build()
        }

        register { get<ReceiptDatabase>().receiptDao() }

        register { ReceiptRepository(dao = get()) }

        register { GetTempImageUri(context = application) }

        register { CopyPictureToInternalStorage(context = application) }

        register { SaveReceipt(repository = get()) }

        register { UpdateReceipt(repository = get()) }

        register { GetReceipts(repository = get()) }

        register { GetReceipt(repository = get()) }

        register { SignIn() }

        register { SignUp() }
    }

    private inline fun <reified T : Any> register(crossinline block: () -> T) {
        _dependencies[T::class] = lazy { block() }
    }
}
