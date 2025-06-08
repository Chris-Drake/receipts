package nz.co.chrisdrake.receipts.domain.image

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nz.co.chrisdrake.receipts.domain.model.ReceiptItem
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class ScanImage(private val context: Context) {

    private val resultJsonSchema = Schema.obj(
        mapOf(
            "merchant" to Schema.string(nullable = true),
            "date" to Schema.string(nullable = true),
            "time" to Schema.string(nullable = true),
            "items" to Schema.array(
                items = Schema.obj(
                    mapOf(
                        "name" to Schema.string(nullable = true),
                        "amount" to Schema.string(nullable = true),
                    )
                )
            ),
        )
    )

    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.0-flash-lite",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = resultJsonSchema
            },
        )
    }

    suspend operator fun invoke(imageUri: Uri): Result = withContext(Dispatchers.IO) {
        val data = checkNotNull(context.contentResolver.openInputStream(imageUri))
            .use(InputStream::readBytes)

        val prompt = content {
            inlineData(data, "image/jpeg")
            text("Extract the text from the receipt image and return the result.")
            text("Format the date as YYYY-MM-DD and the time as HH:mm.")
            text("If the receipt itemizes GST or tax separately, add it to the amount for each individual " +
                    "item (i.e. by multiplying the original amount by the percentage of the tax).")
            text("If the receipt itemizes GST or tax separately, do not include it as a separate item.")
            text("If the receipt includes a total, do not include it as a separate item.")
        }

        val responseJson = checkNotNull(model.generateContent(prompt).text)

        Json.decodeFromString<ResultEntity>(responseJson).toResult()
    }

    private fun ResultEntity.toResult(): Result {
        val merchant = merchant?.sanitize()?.capitalizeWords()
        val date = date?.sanitize()?.let { LocalDate.parse(it) }
        val time = time?.sanitize()?.let { LocalTime.parse(it) }
        val items = items.mapNotNull {
            ReceiptItem(
                id = UUID.randomUUID().toString(),
                name = it.name?.sanitize()?.capitalizeWords() ?: return@mapNotNull null,
                amount = it.amount?.sanitize()?.toBigDecimalOrNull() ?: return@mapNotNull null,
            )
        }

        return Result(
            merchant = merchant,
            date = date,
            time = time,
            items = items.takeIf { it.isNotEmpty() },
        )
    }

    private fun String.sanitize(): String? = trim().takeUnless(String::isEmpty)

    private fun String.capitalizeWords(): String {
        return lowercase()
            .split(" ")
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
    }

    data class Result(
        val merchant: String?,
        val date: LocalDate?,
        val time: LocalTime?,
        val items: List<ReceiptItem>?,
    )

    @Serializable
    private data class ResultEntity(
        val merchant: String?,
        val date: String?,
        val time: String?,
        val items: List<Item>,
    ) {
        @Serializable
        data class Item(
            val name: String?,
            val amount: String?,
        )
    }
}