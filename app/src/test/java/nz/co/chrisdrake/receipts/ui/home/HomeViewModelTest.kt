package nz.co.chrisdrake.receipts.ui.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import nz.co.chrisdrake.receipts.R
import nz.co.chrisdrake.receipts.domain.GetReceipts
import nz.co.chrisdrake.receipts.domain.PerformSync
import nz.co.chrisdrake.receipts.domain.SearchReceipts
import nz.co.chrisdrake.receipts.domain.createReceipt
import nz.co.chrisdrake.receipts.util.ResourceProvider
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class HomeViewModelTest {

    private val receipt = createReceipt()
    private val receipts = listOf(receipt)
    private val getReceipts = mock<GetReceipts> {
        on { invoke() } doReturn flowOf(receipts)
    }
    private val searchReceipts = mock<SearchReceipts> {
        on { invoke(receipts, "query") } doReturn receipts
    }
    private val resourceProvider = mock<ResourceProvider> {
        on { getQuantityString(R.plurals.home_item_count, 1, 1) } doReturn "1 item"
    }
    private val performSync = mock<PerformSync>()

    private val viewModel = HomeViewModel(
        getReceipts = getReceipts,
        searchReceipts = searchReceipts,
        resourceProvider = resourceProvider,
        performSync = performSync,
    )

    @Test
    fun `initial state contains empty receipts and default search bar`() {
        with(viewModel.viewState.value) {
            assertThat(receipts).isEmpty()
            assertThat(searchBar.query).isEmpty()
            assertThat(searchBar.results).isEmpty()
        }
    }

    @Test
    fun `performSync is called on init`() {
        verify(performSync).invoke()
    }

    @Test
    fun `receipts are loaded and mapped to list items`() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state.receipts).hasSize(1)
            assertThat(state.receipts.first()).isEqualTo(
                ReceiptListItem(
                    id = receipt.id,
                    merchant = receipt.merchant,
                    backupStatus = receipt.backUpStatus,
                    itemCount = "1 item",
                    totalAmount = "$1.00",
                    imageUri = "local/thumb.jpg",
                    dateTime = "1 Jan 2025",
                )
            )
        }
    }

    @Test
    fun `search updates results in search bar`() = runTest {
        viewModel.viewState.value.searchBar.onQueryChange("query")

        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state.searchBar.query).isEqualTo("query")
            assertThat(state.searchBar.results).hasSize(1)
        }
    }
}