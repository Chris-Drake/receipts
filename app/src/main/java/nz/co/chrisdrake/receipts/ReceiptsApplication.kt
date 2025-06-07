package nz.co.chrisdrake.receipts

import android.app.Application
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.ktx.Firebase
import nz.co.chrisdrake.receipts.DependencyRegistry.registerApplicationDependencies

class ReceiptsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerApplicationDependencies(this)

        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
    }
}