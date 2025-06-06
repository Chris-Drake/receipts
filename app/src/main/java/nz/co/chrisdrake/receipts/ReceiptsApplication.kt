package nz.co.chrisdrake.receipts

import android.app.Application
import nz.co.chrisdrake.receipts.DependencyRegistry.registerApplicationDependencies

class ReceiptsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerApplicationDependencies(this)
    }
}