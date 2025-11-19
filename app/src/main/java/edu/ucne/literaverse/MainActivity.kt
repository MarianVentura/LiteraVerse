package edu.ucne.literaverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.literaverse.presentation.navigation.MainNavigation
import edu.ucne.literaverse.ui.theme.LiteraVerseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LiteraVerseTheme {
                val navController = rememberNavController()
                MainNavigation(navController = navController)
            }
        }
    }
}