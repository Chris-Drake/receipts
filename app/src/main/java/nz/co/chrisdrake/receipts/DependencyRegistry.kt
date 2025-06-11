package nz.co.chrisdrake.receipts

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import nz.co.chrisdrake.receipts.data.ReceiptDatabase
import nz.co.chrisdrake.receipts.data.ReceiptRepository
import nz.co.chrisdrake.receipts.data.RemoteDataSource
import nz.co.chrisdrake.receipts.data.UserPreferencesRepository
import nz.co.chrisdrake.receipts.domain.BackupReceiptsAsync
import nz.co.chrisdrake.receipts.domain.DeleteReceipt
import nz.co.chrisdrake.receipts.domain.GetOriginalImageUri
import nz.co.chrisdrake.receipts.domain.GetReceipt
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.PerformSync
import nz.co.chrisdrake.receipts.domain.SaveReceipt
import nz.co.chrisdrake.receipts.domain.SearchReceipts
import nz.co.chrisdrake.receipts.domain.UpdateReceipt
import nz.co.chrisdrake.receipts.domain.auth.GetCurrentUser
import nz.co.chrisdrake.receipts.domain.auth.SignIn
import nz.co.chrisdrake.receipts.domain.auth.SignOut
import nz.co.chrisdrake.receipts.domain.auth.SignUp
import nz.co.chrisdrake.receipts.domain.image.CopyImagesToInternalStorage
import nz.co.chrisdrake.receipts.domain.image.CreateImageFilePaths
import nz.co.chrisdrake.receipts.domain.image.GetTempImageUri
import nz.co.chrisdrake.receipts.domain.image.GetUriForFile
import nz.co.chrisdrake.receipts.domain.image.OpenImage
import nz.co.chrisdrake.receipts.domain.image.ScanImage
import kotlin.reflect.KClass

object DependencyRegistry {

    private val _dependencies = mutableMapOf<KClass<*>, Lazy<*>>()

    val dependencies: Map<KClass<*>, Lazy<*>> = _dependencies

    inline fun <reified T : Any> get(): T = dependencies.getValue(T::class).value as T

    fun registerApplicationDependencies(application: Application) {
        register { UserPreferencesRepository(context = application) }

        register {
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

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

        register { GetUriForFile(context = application) }

        register { GetTempImageUri(context = application, getUriForFile = get()) }

        register { OpenImage(context = application, getUriForFile = get()) }

        register { GetOriginalImageUri(remoteDataSource = get()) }

        register { CreateImageFilePaths(context = application) }

        register { CopyImagesToInternalStorage(context = application, createImageFilePaths = get()) }

        register { ScanImage(context = application) }

        register { SaveReceipt(repository = get()) }

        register { UpdateReceipt(repository = get()) }

        register { DeleteReceipt(repository = get(), getCurrentUser = get()) }

        register { SearchReceipts() }

        register { BackupReceiptsAsync(receiptRepository = get(), getCurrentUser = get(), connectivityManager = get()) }

        register { GetReceipts(repository = get()) }

        register { GetReceipt(repository = get()) }

        register { GetCurrentUser() }

        register { PerformSync(receiptRepository = get(), userPreferencesRepository = get(), remoteDataSource = get(), getCurrentUser = get(), createImageFilePaths = get(), connectivityManager = get()) }

        register { SignIn(performSync = get()) }

        register { SignUp() }

        register { SignOut(userPreferencesRepository = get()) }
    }

    private inline fun <reified T : Any> register(crossinline block: () -> T) {
        _dependencies[T::class] = lazy { block() }
    }
}
