package nz.co.chrisdrake.receipts

import android.app.Application
import androidx.room.Room
import nz.co.chrisdrake.receipts.data.ReceiptDatabase
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.domain.BackupReceiptsAsync
import nz.co.chrisdrake.receipts.domain.CopyPictureToInternalStorage
import nz.co.chrisdrake.receipts.domain.DeleteReceipt
import nz.co.chrisdrake.receipts.domain.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.GetPictureFile
import nz.co.chrisdrake.receipts.domain.GetReceipt
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.GetTempImageUri
import nz.co.chrisdrake.receipts.domain.PerformSync
import nz.co.chrisdrake.receipts.domain.SaveReceipt
import nz.co.chrisdrake.receipts.domain.ScanImage
import nz.co.chrisdrake.receipts.domain.SignIn
import nz.co.chrisdrake.receipts.domain.SignOut
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

        register { RemoteDataSource() }

        register { ReceiptRepository(dao = get(), remoteDataSource = get()) }

        register { GetTempImageUri(context = application) }

        register { GetPictureFile(context = application) }

        register { CopyPictureToInternalStorage(context = application, getPictureFile = get()) }

        register { ScanImage(context = application) }

        register { SaveReceipt(repository = get()) }

        register { UpdateReceipt(repository = get()) }

        register { DeleteReceipt(repository = get(), getCurrentUser = get()) }

        register { BackupReceiptsAsync(receiptRepository = get(), getCurrentUser = get()) }

        register { GetReceipts(repository = get()) }

        register { GetReceipt(repository = get()) }

        register { GetCurrentUser() }

        register { PerformSync(receiptRepository = get(), remoteDataSource = get(), getCurrentUser = get(), getPictureFile = get()) }

        register { SignIn(performSync = get()) }

        register { SignUp() }

        register { SignOut() }
    }

    private inline fun <reified T : Any> register(crossinline block: () -> T) {
        _dependencies[T::class] = lazy { block() }
    }
}
