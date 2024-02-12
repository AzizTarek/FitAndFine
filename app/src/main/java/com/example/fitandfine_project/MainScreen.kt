package com.example.fitandfine_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitandfine_project.ui.navigation.BottomBarScreen
import com.example.fitandfine_project.ui.navigation.HomeNavGraph
import com.example.fitandfine_project.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
Sets the app's theme (top bar + bottom bar)
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController= rememberNavController()) {
//    val navController = rememberNavController()
    // theme for our app.
    Scaffold(
        topBar = {
            CustomTopAppBar(canNavigateBack = false, navController = navController)
        },
        bottomBar = {BottomBar(navController = navController) }

    )
    {
       HomeNavGraph(navController = navController)
    }
}



/**
 * App bar to display title and conditionally display the back navigation.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomTopAppBar(
    navController: NavHostController,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit = {}
) {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val formatted = current.format(formatter)
    Log.d("Test","Current Date and Time is: $formatted")
    
    if (canNavigateBack) {
        TopAppBar(
           title = { Text( text = "Fit & Fine",  color = Color.White) },
            actions = {
                val context = LocalContext.current
                IconButton(onClick = {
                    navController.navigate(SettingsDestination.route)
//                    context.startActivity(Intent(context, SettingsActivity_::class.java))
                }) { Icon(Icons.Filled.Settings, "Settings button icon") }
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            },
            // below line is use to give background color
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            elevation = 12.dp
        )
    } else {
        TopAppBar(
            title = { Text( text = "Fit & Fine",  color = Color.White) },
            actions = {
                val context = LocalContext.current
                IconButton(onClick = {
                    navController.navigate(SettingsDestination.route)
//                    context.startActivity(Intent(context, SettingsActivity_::class.java))
                }) { Icon(Icons.Filled.Settings, "Settings button icon") }
            },
            // below line is use to give background color
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            elevation = 12.dp
        )
    }
}
@Composable
fun BottomBar (navController: NavHostController)
{
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Goals,
        BottomBarScreen.History
    )
    val navBackStackEntry by  navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
        screens.forEach{
                screen-> AddItem(
            screen = screen,
            currentDestination =currentDestination ,
            navController = navController )
        }
    }
}
@Composable
fun RowScope.AddItem(
     screen: BottomBarScreen,
     currentDestination: NavDestination?,
     navController: NavHostController
){
    BottomNavigationItem( //Define some properties for a bottom navigation item
        label = {
            Text(text=screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
       },
        selected = currentDestination?.hierarchy?.any{
            it.route ==screen.route
        }  == true,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            navController.navigate(screen.route){
                popUpTo(navController.graph.findStartDestination().id) //Immediately go to start destination when back button is pressed
                launchSingleTop= true
            }
        }
    )

}



