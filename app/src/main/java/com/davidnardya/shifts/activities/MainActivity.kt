package com.davidnardya.shifts.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.davidnardya.shifts.navigation.SetupNavGraph
import com.davidnardya.shifts.ui.theme.ShiftsTheme
import com.davidnardya.shifts.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Locale.setDefault(Locale("he"))
            ShiftsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    SetupNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            // Permission is already granted, proceed with your app's logic
            // For example, save the file here
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted, proceed with your app's logic
//                // For example, save the file here
//            } else {
//                // Permission is denied, handle the denial
//                // For example, show a message to the user indicating that the permission is required
//            }
//        }
//    }
}