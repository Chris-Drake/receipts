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
import nz.co.chrisdrake.receipts.ui.profile.ProfileRoute
import nz.co.chrisdrake.receipts.ui.profile.ProfileScreen
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptRoute
import nz.co.chrisdrake.receipts.ui.receipt.ReceiptScreen
import nz.co.chrisdrake.receipts.ui.signin.SignInRoute
import nz.co.chrisdrake.receipts.ui.signin.SignInScreen
import nz.co.chrisdrake.receipts.ui.signup.SignUpRoute
import nz.co.chrisdrake.receipts.ui.signup.SignUpScreen

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
                        navController.navigate(ProfileRoute)
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
                navigateToSignUp = { navController.navigate(SignUpRoute) },
            )
        }

        composable<SignUpRoute> {
            SignUpScreen(
                navigateBack = { navController.popBackStack() },
                navigateToHome = { navController.popBackStack(HomeRoute, false) },
            )
        }

        composable<ProfileRoute> {
            ProfileScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}
