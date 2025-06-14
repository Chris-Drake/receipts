package nz.co.chrisdrake.receipts.util

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

class ResourceProvider(private val resources: Resources) {

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return resources.getString(resId, *formatArgs)
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String {
        return resources.getQuantityString(resId, quantity, *formatArgs)
    }
}