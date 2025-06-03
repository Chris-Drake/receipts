package nz.co.chrisdrake.receipts

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nz.co.chrisdrake.receipts.ui.home.HomeRoute
import nz.co.chrisdrake.receipts.ui.home.HomeScreen
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptRoute
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                navigateToReceipt = { navController.navigate(ReceiptRoute) }
            )
        }

        composable<ReceiptRoute> {
            ReceiptScreen(
                dismiss = { navController.popBackStack() },
            )
        }
    }
}

