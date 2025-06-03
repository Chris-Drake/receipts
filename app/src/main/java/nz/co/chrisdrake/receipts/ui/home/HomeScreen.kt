package nz.co.chrisdrake.receipts.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

}