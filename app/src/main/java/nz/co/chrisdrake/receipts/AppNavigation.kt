package nz.co.chrisdrake.receipts

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nz.co.chrisdrake.receipts.ui.home.HomeRoute
import nz.co.chrisdrake.receipts.ui.home.HomeScreen
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptRoute
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptScreen
import nz.co.chrisdrake.receipts.ui.signin.SignInRoute
import nz.co.chrisdrake.receipts.ui.signin.SignInScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                navigateToProfile = {
                    if (Firebase.auth.currentUser == null) {
                        navController.navigate(SignInRoute)
                    } else {
                        TODO()
                    }
                },
                navigateToReceipt = { navController.navigate(ReceiptRoute(id = it)) },
            )
        }

        composable<ReceiptRoute> {
            ReceiptScreen(
                id = it.toRoute<ReceiptRoute>().id,
                dismiss = { navController.popBackStack() },
            )
        }

        composable<SignInRoute> {
            SignInScreen(
                navigateBack = { navController.popBackStack() },
                navigateToSignUp = { TODO() },
            )
        }
    }
}
