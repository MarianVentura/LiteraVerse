package edu.ucne.literaverse.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.ucne.literaverse.presentation.home.HomeScreen
import edu.ucne.literaverse.presentation.login.LoginScreen
import edu.ucne.literaverse.presentation.welcome.WelcomeScreen
import edu.ucne.literaverse.presentation.register.RegisterScreen
import edu.ucne.literaverse.presentation.library.LibraryScreen
import edu.ucne.literaverse.presentation.perfil.PerfilScreen
import edu.ucne.literaverse.presentation.search.SearchScreen
import edu.ucne.literaverse.presentation.write.WriteScreen
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome
    ) {
        composable<Screen.Welcome> {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Welcome) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                }
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                onNavigateToBuscar = {
                    navController.navigate(Screen.Search)
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library)
                },
                onNavigateToWrite = {
                    navController.navigate(Screen.Write)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil)
                }
            )
        }

        composable<Screen.Search> {
            SearchScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library)
                },
                onNavigateToWrite = {
                    navController.navigate(Screen.Write)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil)
                }
            )
        }

        composable<Screen.Library> {
            LibraryScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToBuscar = {
                    navController.navigate(Screen.Search)
                },
                onNavigateToWrite = {
                    navController.navigate(Screen.Write)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil)
                }
            )
        }

        composable<Screen.Write> {
            WriteScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToBuscar = {
                    navController.navigate(Screen.Search)
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil)
                }
            )
        }

        composable<Screen.Perfil> {
            PerfilScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToBuscar = {
                    navController.navigate(Screen.Search)
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library)
                },
                onNavigateToWrite = {
                    navController.navigate(Screen.Write)
                }
            )
        }
    }
}

